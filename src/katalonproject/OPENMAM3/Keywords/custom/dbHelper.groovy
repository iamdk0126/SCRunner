package custom

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows

import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement



class dbHelper {
	static String domain = GlobalVariable.DOMAIN
	// 資料庫連線設定
	static String url = "jdbc:mysql://${GlobalVariable.OpenMAM_ip}:3306/${GlobalVariable.mysqldbname}?serverTimezone=UTC&useSSL=false"
	static String user = GlobalVariable.mysqluser
	static String getPassword() {
		switch(GlobalVariable.DOMAIN) {
			case "OPEN":
				return "tvbs2022"
			case "PTS":
				return "open2025"
			default:
				return "" // 預設值
		}
	}
	static String password = getPassword()

	/**
	 * 從 assets 資料表，根據 status，隨機取出一筆 asset_name
	 * @param status (0=未公開, 1=公開)
	 * trash_t (0=不在垃圾桶)
	 * asset_name 排除 新聞名稱必填
	 * asset_name 在資料庫裡出現超過 1 次，就不要選
	 * @return 隨機 asset_name (String)，如果沒有資料會回傳 null
	 */
	@Keyword
	static String getRandomAssetName(int status,int resourceteam) {
		String result = null
		Connection conn = null
		Statement stmt = null
		ResultSet rs = null
		try {
			// 載入 MySQL Driver
			Class.forName("com.mysql.cj.jdbc.Driver")
			conn = DriverManager.getConnection(url, user, password)
			stmt = conn.createStatement()

			String query = """
                SELECT asset_name 
                FROM assets 
                WHERE status = ${status} 
                  AND trash_t = 0
				  AND res_group_id = ${resourceteam}  
				  AND asset_name NOT IN ('新聞名稱必填') 
				GROUP BY asset_name
				HAVING COUNT(*) = 1
                ORDER BY RAND() 
                LIMIT 1
            """
			rs = stmt.executeQuery(query)

			if (rs.next()) {
				result = rs.getString("asset_name")
			}
		} catch (Exception e) {
			e.printStackTrace()
		} finally {
			if (rs != null) rs.close()
			if (stmt != null) stmt.close()
			if (conn != null) conn.close()
		}
		return result
	}

	/**
	 * 從 assets 資料表，取出沒有版權、全表唯一的 asset_name
	 */
	@Keyword
	static String getRandomAssetNameWithoutCopyright(int status,int resourceteam) {
		String assetName = null
		Connection conn = null
		Statement stmt = null
		ResultSet rs = null
		try {
			Class.forName("com.mysql.cj.jdbc.Driver")
			conn = DriverManager.getConnection(url, user, password)
			stmt = conn.createStatement()

			String query = """
                SELECT a.asset_name
                FROM assets a
                WHERE a.status = ${status} 
                  AND a.trash_t = 0
				  AND a.res_group_id = ${resourceteam} 
                  AND a.id NOT IN (SELECT asset_id FROM assets_copyright_info)
				  AND a.asset_name NOT IN ('新聞名稱必填') 
                  AND a.asset_name IN (
                      SELECT asset_name 
                      FROM assets 
                      GROUP BY asset_name 
                      HAVING COUNT(*) = 1
                  )
                ORDER BY RAND()
                LIMIT 1
            """

			rs = stmt.executeQuery(query)

			if (rs.next()) {
				assetName = rs.getString("asset_name")
			}
		} catch (Exception e) {
			e.printStackTrace()
		} finally {
			if (rs != null) rs.close()
			if (stmt != null) stmt.close()
			if (conn != null) conn.close()
		}
		return assetName
	}
	/**
	 * 從 assets 資料表，取出「沒有版權」、「全表唯一」、且「不在回調處理中 (status 1~8)」的 asset_name
	 */
	@Keyword
	static String getRandomAssetNameNotRestoreWithoutCopyright(int status, int resourceteam) {
		String assetName = null
		Connection conn = null
		java.sql.PreparedStatement pstmt = null
		ResultSet rs = null
		try {
			Class.forName("com.mysql.cj.jdbc.Driver")
			conn = DriverManager.getConnection(url, user, password)
	
			// 使用 NOT EXISTS 語法來排除處理中的任務，這在處理「一對多」關聯時比 LEFT JOIN 更嚴謹且效能更好
			String query = """
            SELECT a.asset_name
            FROM assets a
            WHERE a.status = ? 
              AND a.trash_t = 0
              AND a.res_group_id = ? 
              AND a.asset_name NOT IN ('新聞名稱必填')
              -- 條件：確保沒有版權
              AND a.id NOT IN (SELECT asset_id FROM assets_copyright_info)
              -- 條件：排除正在回調中 (1~8) 的媒資
              AND NOT EXISTS (
                  SELECT 1 
                  FROM restored_task rt 
                  WHERE rt.asset_name = a.asset_name 
                    AND rt.status BETWEEN 1 AND 8
              )
              -- 條件：確保 asset_name 在 assets 表中是唯一的
              AND a.asset_name IN (
                  SELECT asset_name 
                  FROM assets 
                  GROUP BY asset_name 
                  HAVING COUNT(*) = 1
              )
            ORDER BY RAND()
            LIMIT 1
        """
	
			pstmt = conn.prepareStatement(query)
			pstmt.setInt(1, status)
			pstmt.setInt(2, resourceteam)
	
			rs = pstmt.executeQuery()
	
			if (rs.next()) {
				assetName = rs.getString("asset_name")
			}
		} catch (Exception e) {
			e.printStackTrace()
		} finally {
			if (rs != null) rs.close()
			if (pstmt != null) pstmt.close()
			if (conn != null) conn.close()
		}
		return assetName
	}
	/**
	 * 從 assets 資料表，取出有版權、全表唯一的 asset_name
	 */
	@Keyword
	static String getRandomAssetNameWithCopyright(int status,int resourceteam) {
		String assetName = null
		Connection conn = null
		Statement stmt = null
		ResultSet rs = null
		try {
			Class.forName("com.mysql.cj.jdbc.Driver")
			conn = DriverManager.getConnection(url, user, password)
			stmt = conn.createStatement()

			String query = """
                SELECT a.asset_name
                FROM assets a
                WHERE a.status = ${status} 
                  AND a.trash_t = 0
				  AND a.res_group_id = ${resourceteam} 
                  AND a.id IN (SELECT asset_id FROM assets_copyright_info)
				  AND a.asset_name NOT IN ('新聞名稱必填')
                  AND a.asset_name IN (
                      SELECT asset_name 
                      FROM assets 
                      GROUP BY asset_name 
                      HAVING COUNT(*) = 1
                  )
                ORDER BY RAND()
                LIMIT 1
            """

			rs = stmt.executeQuery(query)

			if (rs.next()) {
				assetName = rs.getString("asset_name")
			}
		} catch (Exception e) {
			e.printStackTrace()
		} finally {
			if (rs != null) rs.close()
			if (stmt != null) stmt.close()
			if (conn != null) conn.close()
		}
		return assetName
	}
	
	/**
	 * 從 assets 資料表，取出「有版權」、「全表唯一」、且「不在回調處理中 (status 1~8)」的 asset_name
	 */
	@Keyword
	static String getRandomAssetNameNotRestoreWithCopyright(int status, int resourceteam) {
		String assetName = null
		Connection conn = null
		java.sql.PreparedStatement pstmt = null
		ResultSet rs = null
		try {
			Class.forName("com.mysql.cj.jdbc.Driver")
			conn = DriverManager.getConnection(url, user, password)
	
			// SQL 邏輯說明：
			// 1. JOIN assets_copyright_info：確保是有版權的媒資
			// 2. LEFT JOIN restored_task：用來檢查任務狀態
			// 3. WHERE 條件排除 status 1~8 (處理中)
			String query = """
            SELECT a.asset_name
            FROM assets a
            INNER JOIN assets_copyright_info aci ON a.id = aci.asset_id
            LEFT JOIN restored_task rt ON a.asset_name = rt.asset_name
            WHERE a.status = ? 
              AND a.trash_t = 0
              AND a.res_group_id = ? 
              AND a.asset_name NOT IN ('新聞名稱必填')
              -- 排除處理中 (1~8)，允許不存在 (NULL) 或已完成 (9, 10)
              AND (rt.status IS NULL OR rt.status NOT BETWEEN 1 AND 8)
              -- 確保 asset_name 全表唯一
              AND a.asset_name IN (
                  SELECT asset_name 
                  FROM assets 
                  GROUP BY asset_name 
                  HAVING COUNT(*) = 1
              )
            ORDER BY RAND()
            LIMIT 1
        """
	
			pstmt = conn.prepareStatement(query)
			pstmt.setInt(1, status)
			pstmt.setInt(2, resourceteam)
	
			rs = pstmt.executeQuery()
	
			if (rs.next()) {
				assetName = rs.getString("asset_name")
			}
		} catch (Exception e) {
			e.printStackTrace()
		} finally {
			if (rs != null) rs.close()
			if (pstmt != null) pstmt.close()
			if (conn != null) conn.close()
		}
		return assetName
	}

	/**
	 * 檢查指定的 asset_name 是否正處於回調處理中 (status 1~8)
	 * @param assetName 媒資名稱
	 * @return boolean (true = 處理中，不可按；false = 可按)
	 */
	@Keyword
	static boolean isAssetInRestoringStatus(String assetName) {
		boolean isRestoring = false
		Connection conn = null
		java.sql.PreparedStatement pstmt = null
		ResultSet rs = null
		try {
			Class.forName("com.mysql.cj.jdbc.Driver")
			conn = DriverManager.getConnection(url, user, password)
	
			// 查詢該名稱在 restored_task 中是否存在 status 在 1~8 之間的紀錄
			String query = """
            SELECT 1 
            FROM restoring_task 
            WHERE asset_name = ?
            LIMIT 1
        """
	
			pstmt = conn.prepareStatement(query)
			pstmt.setString(1, assetName)
	
			rs = pstmt.executeQuery()
	
			// 如果有查到任何一筆，表示正在處理中
			if (rs.next()) {
				isRestoring = true
			}
		} catch (Exception e) {
			e.printStackTrace()
		} finally {
			if (rs != null) rs.close()
			if (pstmt != null) pstmt.close()
			if (conn != null) conn.close()
		}
		return isRestoring
	}
	@Keyword
	static boolean isTitleInAssets(String title) {
		boolean exists = false
		Connection conn = null
		Statement stmt = null
		ResultSet rs = null
		try {
			Class.forName("com.mysql.cj.jdbc.Driver")
			conn = DriverManager.getConnection(url, user, password)
			stmt = conn.createStatement()
			String query = "SELECT 1 FROM assets WHERE asset_name = '${title}' LIMIT 1"
			rs = stmt.executeQuery(query)
			exists = rs.next()
		} finally {
			if (rs != null) rs.close()
			if (stmt != null) stmt.close()
			if (conn != null) conn.close()
		}
		return exists
	}

	@Keyword
	static String getDisplayNameByName(String name) {
		String displayName = null
		Connection conn = null
		Statement stmt = null
		ResultSet rs = null
		try {
			Class.forName("com.mysql.cj.jdbc.Driver")
			conn = DriverManager.getConnection(url, user, password)
			stmt = conn.createStatement()

			String query = """
            SELECT displayname 
            FROM user 
            WHERE name = '${name}'
            LIMIT 1
        """
			rs = stmt.executeQuery(query)

			if (rs.next()) {
				displayName = rs.getString("displayname")
			}
		} catch (Exception e) {
			e.printStackTrace()
		} finally {
			if (rs != null) rs.close()
			if (stmt != null) stmt.close()
			if (conn != null) conn.close()
		}
		return displayName
	}

	@Keyword
	static String getNameByDisplayName(String displayName) {
		String name = null
		Connection conn = null
		Statement stmt = null
		ResultSet rs = null
		try {
			Class.forName("com.mysql.cj.jdbc.Driver")
			conn = DriverManager.getConnection(url, user, password)
			stmt = conn.createStatement()

			String query = """
            SELECT name 
            FROM user 
            WHERE displayname = '${displayName}'
            LIMIT 1
        """
			rs = stmt.executeQuery(query)

			if (rs.next()) {
				name = rs.getString("name")
			}
		} catch (Exception e) {
			e.printStackTrace()
		} finally {
			if (rs != null) rs.close()
			if (stmt != null) stmt.close()
			if (conn != null) conn.close()
		}
		return name
	}
}
