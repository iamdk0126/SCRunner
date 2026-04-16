package custom

import java.sql.*
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.io.PrintWriter
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.configuration.RunConfiguration

public class MxfManager {
	// 儲存目前的資料庫連線 URL
	private static String currentDbUrl = ""

	/**
	 * 內部工具方法：取得資料庫連線並優化 SQLite 設定
	 */
	private static Connection getConnection() throws SQLException {
		if (currentDbUrl == "") {
			// 如果沒設定，預設放在專案根目錄下
			String defaultPath = RunConfiguration.getProjectDir() + "/mxf_task_queue_default.db"
			currentDbUrl = "jdbc:sqlite:" + defaultPath
		}
		Connection conn = DriverManager.getConnection(currentDbUrl)
		Statement stm = conn.createStatement()

		// 開啟 WAL 模式與忙碌超時，這是解決「資料庫鎖定」報錯的關鍵
		stm.execute("PRAGMA journal_mode = WAL;")
		stm.execute("PRAGMA busy_timeout = 10000;")
		return conn
	}

	/**
	 * 初始化資料庫、掃描目錄並同步輸出 CSV
	 * @param dbFilePath 資料庫檔案路徑
	 * @param targetFolder 要掃描的目錄
	 * @param shouldClean 是否在掃描前清空舊資料 (DB 與 CSV)
	 */
	@Keyword
	static void initAndScanMxf(String dbFilePath, String targetFolder, boolean shouldClean) {
		// 1. 自動檢查並建立目錄路徑
		try {
			Path dbPathObject = Paths.get(dbFilePath)
			Path dbDir = dbPathObject.getParent()
			if (dbDir != null && !Files.exists(dbDir)) {
				Files.createDirectories(dbDir)
			}
		} catch (Exception e) {
			KeywordUtil.logInfo("目錄檢查跳過: " + e.message)
		}

		// 2. 設定 CSV 路徑
		String csvFilePath = dbFilePath.replaceAll("(?i)\\.db\$", ".csv")
		currentDbUrl = "jdbc:sqlite:" + dbFilePath

		Connection conn = null
		PrintWriter csvWriter = null

		try {
			conn = getConnection()
			Statement stm = conn.createStatement()

			// 3. 建立 Table
			stm.executeUpdate("""
                CREATE TABLE IF NOT EXISTS mxf_queue (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    filename TEXT,
                    full_path TEXT,
                    status INTEGER DEFAULT 0,
                    worker_id TEXT
                )
            """)

			// 4. 如果 shouldClean 為 true，清空 DB
			if (shouldClean) {
				stm.executeUpdate("DELETE FROM mxf_queue")
				stm.executeUpdate("DELETE FROM sqlite_sequence WHERE name='mxf_queue'")
				KeywordUtil.logInfo("已清空資料庫舊紀錄。")
			}

			// 5. 初始化 CSV 輸出 (FileOutputStream 預設就是覆蓋模式，這會直接清除舊 CSV 內容)
			FileOutputStream fos = new FileOutputStream(csvFilePath)
			fos.write(0xef); fos.write(0xbb); fos.write(0xbf) // 寫入 BOM 預防亂碼
			csvWriter = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"))
			csvWriter.println("ID,Filename,Full_Path") // 寫入標題列

			Path scanRoot = Paths.get(targetFolder)
			int fileCount = 0

			// 6. 開始掃描
			if (Files.exists(scanRoot)) {
				String insertSql = "INSERT INTO mxf_queue (filename, full_path) VALUES (?, ?)"
				PreparedStatement pstmt = conn.prepareStatement(insertSql)

				Files.walkFileTree(scanRoot, new SimpleFileVisitor<Path>() {
							@Override
							FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
								String fileName = file.getFileName().toString()
								if (fileName.toLowerCase().endsWith(".mxf")) {
									String absPath = file.toAbsolutePath().toString()

									// 寫入 DB 批次
									pstmt.setString(1, fileName)
									pstmt.setString(2, absPath)
									pstmt.addBatch()

									fileCount++

									// 寫入 CSV
									csvWriter.println("${fileCount},\"${fileName}\",\"${absPath}\"")

									if (fileCount % 500 == 0) pstmt.executeBatch()
								}
								return FileVisitResult.CONTINUE
							}
						})

				pstmt.executeBatch()
				csvWriter.flush()
				KeywordUtil.logInfo("掃描完成！共匯入 ${fileCount} 筆任務。")
			} else {
				KeywordUtil.markFailed("掃描目錄不存在: " + targetFolder)
			}
		} catch (Exception e) {
			KeywordUtil.markFailed("掃描失敗: " + e.message)
		} finally {
			if (conn != null) conn.close()
			if (csvWriter != null) csvWriter.close() // 關閉 Writer 會完成檔案寫入
		}
	}

	/**
	 * 切換資料庫路徑 (用於不同 Test Case 呼叫時)
	 */
	@Keyword
	static void setDbPath(String dbFilePath) {
		currentDbUrl = "jdbc:sqlite:" + dbFilePath
		KeywordUtil.logInfo("目前使用的任務池已切換至: " + dbFilePath)
	}

	/**
	 * 領取下一個任務 (原子操作，確保多個腳本執行不重複)
	 */
	@Keyword
	static List grabNextJob(String robotName) {
		Connection conn = null
		List result = null
		try {
			conn = getConnection()
			// 1. 搶佔任務
			String updateSql = "UPDATE mxf_queue SET status = 1, worker_id = ? " +
					"WHERE id = (SELECT id FROM mxf_queue WHERE status = 0 LIMIT 1)"
			PreparedStatement pstmt = conn.prepareStatement(updateSql)
			pstmt.setString(1, robotName)

			if (pstmt.executeUpdate() > 0) {
				// 2. 取出該筆資料
				String selectSql = "SELECT id, full_path FROM mxf_queue WHERE worker_id = ? AND status = 1 ORDER BY id DESC LIMIT 1"
				PreparedStatement selectPstmt = conn.prepareStatement(selectSql)
				selectPstmt.setString(1, robotName)
				ResultSet rs = selectPstmt.executeQuery()

				if (rs.next()) {
					// 將 id 放在索引 0，路徑放在索引 1
					result = [
						rs.getInt("id"),
						rs.getString("full_path")
					]
				}
			}
		} catch (Exception e) {
			KeywordUtil.logInfo("抓取任務失敗: " + e.message)
		} finally {
			if (conn != null) conn.close()
		}
		return result
	}

	/**
	 * 標記任務完成 (status = 2)
	 */
	@Keyword
	static boolean markAsDone(String mxfPath) {
		Connection conn = null
		try {
			conn = getConnection()
			String doneSql = "UPDATE mxf_queue SET status = 2 WHERE full_path = ?"
			PreparedStatement pstmt = conn.prepareStatement(doneSql)
			pstmt.setString(1, mxfPath)
			pstmt.executeUpdate()
			return true
		} catch (Exception e) {
			return false
		} finally {
			if (conn != null) conn.close()
		}
	}

	/**
	 * 重設所有任務狀態為 0
	 */
	@Keyword
	static boolean resetAllJobs() {
		Connection conn = null
		try {
			conn = getConnection()
			String resetSql = "UPDATE mxf_queue SET status = 0, worker_id = NULL"
			int rows = conn.createStatement().executeUpdate(resetSql)
			KeywordUtil.logInfo("已重置 ${rows} 筆任務。")
			return true
		} catch (Exception e) {
			return false
		} finally {
			if (conn != null) conn.close()
		}
	}

	/**
	 * 驗證打印資料庫內容 (含完整路徑)
	 */
	@Keyword
	static void printDbContent(int offset, int limit) {
		Connection conn = null
		try {
			conn = getConnection()
			String selectSql = "SELECT id, filename, status, full_path FROM mxf_queue LIMIT ? OFFSET ?"
			PreparedStatement pstmt = conn.prepareStatement(selectSql)
			pstmt.setInt(1, limit)
			pstmt.setInt(2, offset)
			ResultSet rs = pstmt.executeQuery()

			println "--- DB Content (Limit: ${limit}, Offset: ${offset}) ---"
			while (rs.next()) {
				println String.format("[%d] 狀態:%d | %s", rs.getInt("id"), rs.getInt("status"), rs.getString("filename"))
				println "    └── 路徑: " + rs.getString("full_path")
			}
		} catch (Exception e) {
			println "打印失敗: " + e.message
		} finally {
			if (conn != null) conn.close()
		}
	}

	/**
	 * 將指定 ID 的任務狀態重設為 0 (未使用)，以便循環使用
	 * @param id 任務的資料庫 ID
	 */
	@Keyword
	static boolean resetStatus(int id) {
		Connection conn = null
		try {
			conn = getConnection()
			String resetSql = "UPDATE mxf_queue SET status = 0, worker_id = NULL WHERE id = ?"
			PreparedStatement pstmt = conn.prepareStatement(resetSql)
			pstmt.setInt(1, id)
			int rows = pstmt.executeUpdate()
			return rows > 0
		} catch (Exception e) {
			KeywordUtil.logInfo("重設狀態失敗: " + e.message)
			return false
		} finally {
			if (conn != null) conn.close()
		}
	}
	/**
	 * 僅初始化資料表（確保表存在），如果已有資料則「不動作」
	 * 這樣你每次執行 init 都不會影響舊的 counter 值
	 */
	@Keyword
	static void initCounterTable() {
		Connection conn = null
		try {
			conn = getConnection()
			Statement stm = conn.createStatement()
			
			// 1. 建立表 (若不存在)
			stm.executeUpdate("CREATE TABLE IF NOT EXISTS upload_counter (id INTEGER PRIMARY KEY, current_value INTEGER)")
			
			// 2. 檢查表內是否已經有初始資料
			ResultSet rs = stm.executeQuery("SELECT count(*) FROM upload_counter")
			if (rs.next() && rs.getInt(1) == 0) {
				// 只有在完全沒資料時才插入 1，這保證了「維持現狀」
				stm.executeUpdate("INSERT INTO upload_counter (id, current_value) VALUES (1, 1)")
				KeywordUtil.logInfo("序號表已初始化，從 1 開始。")
			} else {
				KeywordUtil.logInfo("序號表已存在，將維持目前的數值。")
			}
		} catch (Exception e) {
			KeywordUtil.logInfo("初始化序號表失敗: " + e.message)
		} finally {
			if (conn != null) conn.close()
		}
	}
	
	/**
	 * 強制重置序號 (當你真的想要從頭開始時才呼叫這個)
	 * @param startValue 起始值，預設為 1
	 */
	@Keyword
	static void resetCounter(int startValue = 1) {
		Connection conn = null
		try {
			conn = getConnection()
			String sql = "UPDATE upload_counter SET current_value = ? WHERE id = 1"
			PreparedStatement pstmt = conn.prepareStatement(sql)
			pstmt.setInt(1, startValue)
			pstmt.executeUpdate()
			KeywordUtil.logInfo("序號已手動重置為: " + startValue)
		} catch (Exception e) {
			KeywordUtil.logInfo("重置序號失敗: " + e.message)
		} finally {
			if (conn != null) conn.close()
		}
	}

	/**
	 * 原子操作：領取下一個流水號並累加
	 * 解決多線程/多機競爭問題
	 */
	@Keyword
	static int getNextSerialNumber() {
		Connection conn = null
		int nextValue = -1
		try {
			conn = getConnection()
			conn.setAutoCommit(false) // 開啟事務處理

			// 1. 鎖定並更新序號 (+1)
			String updateSql = "UPDATE upload_counter SET current_value = current_value + 1 WHERE id = 1"
			conn.createStatement().executeUpdate(updateSql)

			// 2. 取得更新後的數值
			String selectSql = "SELECT current_value FROM upload_counter WHERE id = 1"
			ResultSet rs = conn.createStatement().executeQuery(selectSql)

			if (rs.next()) {
				// 我們取回來的如果是 11，代表本次任務使用 10 (或直接用 11 也可以，只要唯一即可)
				nextValue = rs.getInt("current_value") - 1
			}

			conn.commit()
		} catch (Exception e) {
			if (conn != null) conn.rollback()
			KeywordUtil.logInfo("領取序號失敗: " + e.message)
		} finally {
			if (conn != null) conn.close()
		}
		return nextValue
	}
}