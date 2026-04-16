import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
//import com.kms.katalon.core.cucumber.keyword.CucumberBuiltInKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
//import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import com.kms.katalon.core.util.KeywordUtil as KeywordUtil
import internal.GlobalVariable as GlobalVariable
import java.time.LocalDate as LocalDate
import java.time.format.DateTimeFormatter as DateTimeFormatter
import org.openqa.selenium.Keys as Keys
import java.io.File as File
import java.io.IOException as IOException
import java.util.Random as Random
//import java.io.FileFilter as FileFilter // 不再需要明確導入 FileFilter
import java.util.Map as Map
import java.util.HashMap as HashMap
import java.util.ArrayList as ArrayList
import java.util.List
import java.io.FileWriter
import java.io.BufferedWriter


// --- 定義常數，提高可維護性 ---
// 檔案路徑
final String BASE_DRIVE_S = "S:\\"
final String VIDEO_DIR_D = "D:\\20Clips"
//final String UNPUBLIC_LIST_FILE = BASE_DRIVE_S + "UnpublicList.txt" // 用於記錄已處理的 Title

// 時間相關
final long ONE_HOUR_IN_SECONDS = 3600
final long RETRY_DELAY_MS = 1000 // 1 秒
final int MAX_RETRIES = 5

// --- 1. 從外部參數設定總執行次數 ---
int totalLoopsToRun = indexParam
if (totalLoopsToRun <= 0) {
	KeywordUtil.markFailedAndStop("錯誤: 傳入的執行次數 (indexParam) 必須大於 0。")
}
WebUI.comment("--- 自動化腳本已啟動，目標執行次數: " + totalLoopsToRun + " ---")

// --- 2. 初始化計數器與隨機數產生器 ---
int executedLoops = 0
Random randomGenerator = new Random()

// --- 確認並初始化 GlobalVariable.processedTitles ---
// 確保 GlobalVariable.processedTitles 是一個 List<String> 類型
if (GlobalVariable.processedTitles == null || !(GlobalVariable.processedTitles instanceof List)) {
	GlobalVariable.processedTitles = new ArrayList<String>()
	WebUI.comment("GlobalVariable.processedTitles 已初始化為空的 ArrayList。")
}


// --- 3. 建立迴圈，直到完成指定的執行次數 ---
while (executedLoops < totalLoopsToRun) {
	// --- 準備當天的日期與檔案路徑 ---
	LocalDate today = LocalDate.now()
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern('yyyyMMdd')
	String formattedDate = today.format(formatter)
	String allInfoFilePath = BASE_DRIVE_S + formattedDate + "-air.all.txt" // Video ID 完整資訊檔案
	//String usedVideoIdFilePath = UNPUBLIC_LIST_FILE // 使用常數

	File allInfoFile = new File(allInfoFilePath) // 創建 File 物件
	//File usedVideoIdFile = new File(usedVideoIdFilePath)

	WebUI.comment("正在檢查今日的 Video ID 資訊檔案: " + allInfoFilePath)

	// --- 檢查主要 Video ID 資訊檔案是否存在且有內容 ---
	if (!allInfoFile.exists() || allInfoFile.length() == 0) {
		WebUI.comment("今日無可用 ID 資訊或檔案不存在。腳本將在一小時後重試。")
		WebUI.delay(ONE_HOUR_IN_SECONDS) // 使用常數
		continue // 繼續 while 迴圈的下一次迭代
	}

	// --- 讀取 Video ID 和 Title 的對應檔案，並直接獲取第一筆 ---
	Map<String, String> videoIdToTitleMap = new HashMap<String, String>()
	List<String> rawAllInfoFileLines = new ArrayList<String>() // 儲存原始行，用於後續移除和寫回
	String specificVideoID = null // 用於儲存第一筆有效的 Video ID
	String correspondingTitle = null // 用於儲存第一筆有效的 Title

	try {
		allInfoFile.readLines('UTF-8').each { line ->
			rawAllInfoFileLines.add(line) // 儲存原始行
			def trimmedLine = line.trim()
			if (!trimmedLine.isEmpty()) {
				def parts = trimmedLine.split('\t', 2)
				if (parts.length >= 1) { // 至少有一個部分，即 Video ID
					String currentVideoId = parts[0].trim()
					String currentTitle = (parts.length == 2) ? parts[1].trim() : ""

					// 只有在還沒有找到第一筆時才儲存這個 ID 和 Title
					if (specificVideoID == null) {
						specificVideoID = currentVideoId
						// 如果 Title 為空字串，將其設為 "N/A"，否則使用解析出的 Title
						correspondingTitle = currentTitle.isEmpty() ? "N/A" : currentTitle
					}
					// 將所有有效的 ID-Title 對放入 Map，以便後續刪除行時進行比對（可選，但保持原邏輯）
					videoIdToTitleMap.put(currentVideoId, currentTitle)
				}
			}
		}
		WebUI.comment("已成功讀取 Video ID 資訊檔案: " + allInfoFilePath + "，共 " + rawAllInfoFileLines.size() + " 行。")
	} catch (IOException e) {
		KeywordUtil.markWarning("讀取 Video ID 資訊檔案 '" + allInfoFilePath + "' 時發生錯誤: " + e.getMessage() + "。將在一小時後重試。")
		WebUI.delay(ONE_HOUR_IN_SECONDS)
		continue
	}

	// --- 檢查是否成功找到要處理的 Video ID ---
	if (specificVideoID == null) {
		WebUI.comment("Video ID 資訊檔案內容解析後為空，或沒有找到有效的 Video ID，將在一小時後重試。")
		WebUI.delay(ONE_HOUR_IN_SECONDS) // 使用常數
		continue
	}

	// --- 新增的診斷日誌 ---
	WebUI.comment("【診斷】迴圈開始，準備處理的 ID: " + specificVideoID + ", 對應 Title: " + correspondingTitle)
	
	// --- 取得影片檔案 ---
	File videoDir = new File(VIDEO_DIR_D) // 使用常數
	// 使用更簡潔的 Lambda 語法過濾 MXF 檔案
	List<File> mxfFiles = videoDir.listFiles((FileFilter) { file -> // <--- 修正點
		file.isFile() && file.name.toLowerCase().endsWith('.mxf')
	}).toList()

	if (mxfFiles.isEmpty()) {
		KeywordUtil.markWarning('警告: 在目錄 ' + VIDEO_DIR_D + ' 中找不到任何 .MXF 檔案! 流程將暫停一小時。') // 使用常數
		WebUI.delay(ONE_HOUR_IN_SECONDS) // 使用常數
		continue
	}

	// 隨機選取一個 MXF 檔案
	File randomMxfFile = mxfFiles[randomGenerator.nextInt(mxfFiles.size())]
	String currentFilePath = randomMxfFile.getAbsolutePath()
	
	WebUI.comment("準備處理第 " + (executedLoops + 1) + " 筆任務。")
	WebUI.comment("使用 Video ID: " + specificVideoID)
	WebUI.comment("對應 Title: " + correspondingTitle)
	WebUI.comment("隨機選擇檔案: " + currentFilePath)

	boolean isSuccess = false
	try {
		// --- UI 操作 ---
		WebUI.click(findTestObject('主畫面/新增媒資'))
		// 建議: 這裡可以加入 WebUI.waitForElementVisible 或 WebUI.waitForElementClickable
		WebUI.click(findTestObject('主畫面/新增媒資頁/建立媒資'))
		// 建議: 這裡可以加入 WebUI.waitForElementVisible 或 WebUI.waitForElementClickable
		WebUI.click(findTestObject('主畫面/新增媒資頁/模板1'))
		WebUI.delay(2) // 考慮增加等待時間或使用 waitForElementVisible
		WebUI.click(findTestObject('主畫面/新增媒資頁/建立'))
		WebUI.setText(findTestObject('主畫面/編輯媒資頁/item1_videoid'), specificVideoID)
		// 呼叫子測試案例，並傳遞 VideoIDParam
		WebUI.callTestCase(findTestCase('05BaseTest/編輯媒資分析VideoID選擇媒資分類'), [('VideoIDParam') : specificVideoID], FailureHandling.CONTINUE_ON_FAILURE)
		WebUI.delay(2) // 考慮增加等待時間或使用 waitForElementVisible
		WebUI.click(findTestObject('主畫面/編輯媒資頁/同步文稿'))
		WebUI.delay(2) // 考慮增加等待時間或使用 waitForElementVisible
		WebUI.uploadFile(findTestObject('主畫面/新增媒資頁/上傳影片'), currentFilePath)
		// 等待上傳完成的標誌性元素出現
		WebUI.waitForElementVisible(findTestObject('主畫面/編輯媒資頁/主檔刪除'), 60, FailureHandling.OPTIONAL)
		WebUI.click(findTestObject('主畫面/編輯媒資頁/儲存'))
		WebUI.delay(2) // 考慮增加等待時間或使用 waitForElementVisible
		if (WebUI.verifyElementClickable(findTestObject('主畫面/編輯媒資頁/儲存'), FailureHandling.OPTIONAL)) {
			WebUI.click(findTestObject('主畫面/編輯媒資頁/儲存'))
		}
		
		WebUI.comment("ID: " + specificVideoID + " 上傳成功。")
		isSuccess = true

	} catch (Exception e) {
		// 捕獲所有異常並記錄警告
		KeywordUtil.markWarning("處理 ID: " + specificVideoID + " 時發生錯誤: " + e.getMessage())
		// 可以選擇在這裡添加更詳細的日誌，例如堆棧追蹤
		// KeywordUtil.markWarning("StackTrace: " + e.printStackTrace())
	}

	// --- 5. 如果上傳成功，則更新計數器和 ID 檔案 ---
	if (isSuccess) {
		executedLoops++ // 成功完成一次，計數器加 1
		
		// --- 將已使用的 ID 和 Title 從 allInfoFile 中刪除並寫回的重試邏輯 ---
		// 找到要移除的行
		String lineToRemove = null
		for (String line : rawAllInfoFileLines) {
			// 檢查行是否以 specificVideoID 開頭，或者就是 specificVideoID 本身
			if (line.trim().startsWith(specificVideoID + "\t") || line.trim().equals(specificVideoID)) {
				lineToRemove = line
				break
			}
		}

		if (lineToRemove != null) {
			rawAllInfoFileLines.remove(lineToRemove)
			WebUI.comment("已從記憶體中的 allInfoFile 內容移除 ID: " + specificVideoID)
		} else {
			KeywordUtil.markWarning("警告: 未能在 allInfoFile 的記憶體內容中找到並移除 ID: " + specificVideoID + "。這可能導致重複處理。")
		}

		boolean allInfoFileWriteSuccessful = false
		for (int i = 0; i < MAX_RETRIES; i++) {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(allInfoFile, false)) // 覆蓋寫入
				// 只有當 rawAllInfoFileLines 不為空時才寫入，避免寫入空行
				if (!rawAllInfoFileLines.isEmpty()) {
					writer.write(rawAllInfoFileLines.join('\n'))
				}
				writer.close()
				WebUI.comment("已成功更新 Video ID 完整資訊檔案: " + allInfoFilePath)
				allInfoFileWriteSuccessful = true
				break
			} catch (IOException e) {
				KeywordUtil.markWarning("更新 Video ID 完整資訊檔案 '" + allInfoFilePath + "' 失敗 (嘗試 ${i + 1}/${MAX_RETRIES}): " + e.getMessage())
				if (i < MAX_RETRIES - 1) {
					WebUI.delay(RETRY_DELAY_MS / 1000.0) // 使用常數
					WebUI.comment("等待 ${RETRY_DELAY_MS} 毫秒後重試更新 Video ID 完整資訊檔案...")
				}
			}
		}

		if (!allInfoFileWriteSuccessful) {
			KeywordUtil.markWarning("【最終錯誤】在多次重試後，仍未能更新 Video ID 完整資訊檔案: " + allInfoFilePath)
		}
		// --- allInfoFile 寫入重試邏輯結束 ---

        /*
		// --- 將已使用的 ID 和 Title 寫入 _used.txt 檔案 (usedVideoIdFile) 的重試邏輯 ---
		boolean usedFileWriteSuccessful = false

		for (int i = 0; i < MAX_RETRIES; i++) { // 使用常數
			try {
				// 追加寫入檔案，將已使用的 Title 記錄下來
				BufferedWriter writer = new BufferedWriter(new FileWriter(usedVideoIdFile, true)) // true 表示追加
				writer.append(correspondingTitle)
				writer.newLine()
				writer.close()
				WebUI.comment("已將 Title: " + correspondingTitle + " 記錄到 " + usedVideoIdFilePath)
				usedFileWriteSuccessful = true
				break
			} catch (IOException e) {
				KeywordUtil.markWarning("寫入已使用 ID 檔案 '" + usedVideoIdFilePath + "' 失敗 (嘗試 ${i + 1}/${MAX_RETRIES}): " + e.getMessage()) // 使用常數
				KeywordUtil.markWarning("將 Title 寫入資料庫失敗: " + e.getMessage())
				if (i < MAX_RETRIES - 1) { // 使用常數
					WebUI.delay(RETRY_DELAY_MS / 1000.0) // 使用常數
					WebUI.comment("等待 ${RETRY_DELAY_MS} 毫秒後重試寫入已使用 ID 檔案...") // 使用常數
				}
				}
			}

		if (!usedFileWriteSuccessful) {
			KeywordUtil.markWarning("【最終錯誤】在多次重試後，仍未能將 Title 寫入已使用 ID 檔案: " + usedVideoIdFilePath)
		}
		// --- usedVideoIdFile 寫入重試邏輯結束 ---
		*/
		// 將對應的 Title 加入到全域變數 List 中
		GlobalVariable.processedTitles.add(correspondingTitle)
		WebUI.comment("Title '" + correspondingTitle + "' 已加入到 GlobalVariable.processedTitles。目前列表大小: " + GlobalVariable.processedTitles.size())
	}
} // --- while 迴圈結束 ---

WebUI.comment("--- 自動化腳本執行完畢 ---")
println("已成功完成指定的 " + totalLoopsToRun + " 次上傳任務。")
println("所有已處理的 Title 列表: " + GlobalVariable.processedTitles)