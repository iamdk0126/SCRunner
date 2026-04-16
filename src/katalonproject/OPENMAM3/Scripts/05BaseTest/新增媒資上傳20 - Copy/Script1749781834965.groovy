import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
//import com.kms.katalon.core.cucumber.keyword.CucumberBuiltInKeywords as CucumberKW // 註釋掉未使用的導入
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
//import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows // 註釋掉未使用的導入
import com.kms.katalon.core.util.KeywordUtil as KeywordUtil
import internal.GlobalVariable as GlobalVariable
import java.time.LocalDate as LocalDate
import java.time.format.DateTimeFormatter as DateTimeFormatter
import org.openqa.selenium.Keys as Keys
import java.io.File as File
import java.io.IOException as IOException // 新增導入
import java.util.Random as Random
import java.io.FileFilter as FileFilter
import java.util.Map as Map // 新增導入
import java.util.HashMap as HashMap // 新增導入
import java.util.ArrayList as ArrayList // 新增導入
import java.util.List // 新增導入
import java.io.FileWriter // 新增導入
import java.io.BufferedWriter // 新增導入
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import custom.TimeControl as TimeControl
import custom.XmlReader as XmlReader
import custom.BrowserHelper
import custom.WebUIExtensions
import custom.TimecodeHelper
import custom.DropdownHelper
import custom.TimeControl

int indexParam = 20  //對應20Clips

String specificVideoID = ""

String correspondingTitle = ""

String currentFilePath = ""

String subtitleFilePath = ""

// --- 1. 從外部參數設定總執行次數 ---
// 假設 indexParam 會從外部傳入，例如作為測試案例變數
int totalLoopsToRun = indexParam
if (totalLoopsToRun <= 0) {
	KeywordUtil.markFailedAndStop("錯誤: 傳入的執行次數 (indexParam) 必須大於 0。")
}
WebUI.comment("--- 自動化腳本已啟動，目標執行次數: " + totalLoopsToRun + " ---")

// --- 2. 初始化計數器與隨機數產生器 (此腳本未使用隨機數，但保留) ---
int executedLoops = 0
Random randomGenerator = new Random() // 雖然此腳本未使用隨機檔案選擇，但保留

// --- 確認並初始化 GlobalVariable.processedTitles ---
// 確保 GlobalVariable.processedTitles 在 Katalon Studio 的 Profiles 中已定義為 List 或 Object 類型
if (GlobalVariable.processedTitles == null) {
	GlobalVariable.processedTitles = new ArrayList<String>()
	WebUI.comment("GlobalVariable.processedTitles 已初始化為空的 ArrayList。")
} else if (!(GlobalVariable.processedTitles instanceof List)) {
	GlobalVariable.processedTitles = new ArrayList<String>()
	WebUI.comment("GlobalVariable.processedTitles 類型不符，已重新初始化為空的 ArrayList。")
}


// --- 3. 建立迴圈，直到完成指定的執行次數 ---
while (executedLoops < totalLoopsToRun) {
	// --- 準備當天的日期與檔案路徑 ---
	LocalDate today = LocalDate.now()
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern('yyyyMMdd')
	String formattedDate = today.format(formatter)

	// 主要 Video ID 檔案路徑 (與您原腳本的 filePath 相同)
	String videoIdFilePath = ('S:\\' + formattedDate) + '.txt'
	// Video ID 完整資訊檔案路徑
	String allInfoFilePath = ('S:\\' + formattedDate) + '-air.all.txt'
	// 已使用 Video ID Title 記錄檔案路徑
	String usedVideoIdFilePath = ('S:\\UnpublicList.txt')

	File videoIdFile = new File(videoIdFilePath)
	File allInfoFile = new File(allInfoFilePath)
	File usedVideoIdFile = new File(usedVideoIdFilePath)

	WebUI.comment("正在檢查今日的 ID 檔案: " + videoIdFilePath)

	// --- 檢查主要 Video ID 檔案是否存在且有內容 ---
	if (!videoIdFile.exists() || videoIdFile.length() == 0) {
		WebUI.comment("今日無可用 ID 或檔案不存在。腳本將在一小時後重試。")
		WebUI.delay(3600) // 等待一小時
		continue // 繼續 while 迴圈的下一次迭代
	}

	// --- 檢查並讀取 Video ID 和 Title 的對應檔案 ---
	Map<String, String> videoIdToTitleMap = new HashMap<String, String>()
	if (!allInfoFile.exists() || allInfoFile.length() == 0) {
		KeywordUtil.markWarning("警告: 找不到或檔案 '" + allInfoFilePath + "' 為空。將無法記錄 Video ID 對應的 Title。")
	} else {
		try {
			allInfoFile.readLines('UTF-8').each { line ->
				def parts = line.trim().split('\t', 2)
				if (parts.length == 2) {
					videoIdToTitleMap.put(parts[0].trim(), parts[1].trim())
				} else if (parts.length == 1 && !parts[0].trim().isEmpty()) {
					videoIdToTitleMap.put(parts[0].trim(), "") // 如果只有 ID 沒有 Title，則 Title 為空字串
				}
			}
			WebUI.comment("已成功讀取 Video ID 資訊檔案: " + allInfoFilePath)
		} catch (IOException e) {
			KeywordUtil.markWarning("讀取 Video ID 資訊檔案 '" + allInfoFilePath + "' 時發生錯誤: " + e.getMessage() + "。將無法記錄 Video ID 對應的 Title。")
		}
	}


	// --- 讀取待處理 Video ID ---
	List<String> allVideoIds
	try {
		allVideoIds = videoIdFile.readLines('UTF-8').findAll { !it.trim().isEmpty() }
		if (allVideoIds.isEmpty()) {
			WebUI.comment("待處理 ID 檔案內容為空，將在一小時後重試。")
			WebUI.delay(3600)
			continue
		}
	} catch (IOException e) {
		KeywordUtil.logInfo("讀取待處理 ID 檔案時發生錯誤: " + e.getMessage() + "，將在一小時後重試。")
		WebUI.delay(3600)
		continue
	}
	
	// --- 4. 處理檔案中的第一個可用 ID ---
	String videoIdToProcess = allVideoIds.get(0)
	specificVideoID = videoIdToProcess.trim()
	// 取得對應的 Title，如果沒有則為 "N/A"
	correspondingTitle = videoIdToTitleMap.get(specificVideoID) ?: "N/A"

	// 根據當前執行的次數來決定 MXF 和 SRT 檔案的編號
	// 注意: 這裡假設 D:\20Clips 下有 C0001.MXF, C0002.MXF 等檔案
	// 如果 totalLoopsToRun 超過實際存在的 CXXXX.MXF 檔案數量，可能會導致檔案找不到錯誤。
	int currentFileNumber = executedLoops + 1 // 從 1 開始計數
	String formattedNumber = String.format('%04d', currentFileNumber)
	String currentFilePath = ('D:\\20Clips\\C' + formattedNumber) + '.MXF'
	String currentsrtFilePath = ('D:\\20Clips\\C' + formattedNumber) + '.srt'

	// 檢查 MXF 檔案是否存在
	File mxfFile = new File(currentFilePath)
	if (!mxfFile.exists()) {
		KeywordUtil.markWarning("警告: 找不到 MXF 檔案: " + currentFilePath + "。此次上傳將跳過。")
		// 如果找不到檔案，不計入成功次數，直接進入下一次迴圈迭代
		// 但為了避免無限循環，如果檔案不存在，且這是唯一的處理方式，可能需要考慮停止或更換策略。
		// 這裡為了保持與原腳本邏輯一致，將其視為失敗，但不停止腳本。
		// 您可以根據實際需求，決定是跳過、停止還是等待。
		continue
	}

	WebUI.comment("準備處理第 " + (executedLoops + 1) + " 筆任務。")
	WebUI.comment("使用 Video ID: " + specificVideoID)
	WebUI.comment("對應 Title: " + correspondingTitle)
	WebUI.comment("準備上傳檔案: " + currentFilePath)
	WebUI.comment("準備上傳字幕檔案: " + currentsrtFilePath)
	String VideoID = specificVideoID
	
	String Title = correspondingTitle
	
	boolean isSuccess = false
	try {
		// --- UI 操作 ---
		WebUI.click(findTestObject('主畫面/新增媒資'))
		//WebUI.click(findTestObject('主畫面/新增媒資頁/建立媒資'))
		WebUI.click(findTestObject('主畫面/新增媒資頁/模板1'))
		WebUI.delay(2) // 可以考慮替換為顯式等待
		WebUI.click(findTestObject('主畫面/新增媒資頁/建立'))
		WebUIExtensions.setTextByLabel('Video_ID', specificVideoID)
		WebUI.delay(2)
		WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/輸入Title'))
		
		WebUI.delay(2)
		
		// 模擬 Ctrl + A + Backspace
		WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/輸入Title'), Keys.chord(Keys.CONTROL,'a'))
		WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/輸入Title'), Keys.chord(Keys.BACK_SPACE))
		
		WebUI.delay(2)
		
		//WebUIExtensions.retrySetText(findTestObject('主畫面/編輯媒資頁/輸入Title'), correspondingTitle)
		WebUIExtensions.setTextByLabel('名稱', correspondingTitle)
		
		WebUI.delay(2)
		
		WebUIExtensions.setTextByLabel('節目名稱', Title)
		
		WebUI.delay(1)
		
		//DropdownHelper.selectDropdownOptionByLabel('節目名稱',Title+" (ID:"+VideoID+")")
		
	
		WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/儲存'))
		
		WebUI.delay(2)
    WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資'))

	WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/搜尋媒資'))

	WebUIExtensions.setTextByLabel('關鍵字', correspondingTitle)
	
    'search'
    WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/搜尋'))
		
	if (WebUI.verifyElementClickable(findTestObject('主畫面/未公開媒資頁/編輯媒資1'), FailureHandling.OPTIONAL)) {
		WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/編輯媒資1'))
		
		//WebUIExtensions.retrySetText(findTestObject('主畫面/編輯媒資頁/節目名稱'), correspondingTitle)
		//WebUIExtensions.setTextByLabel('節目名稱', correspondingTitle)
		//WebUI.click(findTestObject('主畫面/編輯媒資頁/節目名稱'))
		
		//WebUI.setText(findTestObject('主畫面/編輯媒資頁/節目名稱'),'看公視說英語')
		
		
		WebUIExtensions.retrySetText(findTestObject('主畫面/編輯媒資頁/集數'), "$randomX") //String.valueOf(randomX)
		//WebUIExtensions.setTextByLabel('集數', "10")
		
		//WebUIExtensions.retrySetText(findTestObject('主畫面/編輯媒資頁/音軌設定'), 'LRLR----')
		WebUIExtensions.setTextByLabel('音軌設定', 'LRLR----')
		
		WebUI.comment('specificVideoID=' + specificVideoID)
		if (specificVideoID != '') {
			WebUI.callTestCase(findTestCase('05BaseTest/編輯媒資分析VideoID選擇媒資分類'), [('VideoIDParam') : specificVideoID], FailureHandling.CONTINUE_ON_FAILURE) }
		else {
			WebUI.comment('specificVideoID 為 null，跳過編輯媒資分析測試案例')
		}
		WebUI.delay(2)
		
		//WebUI.click(findTestObject('主畫面/編輯媒資頁/用途下拉'))
		
		//DropdownHelper.selectVisibleDropdownOptionScroll('播放')
		DropdownHelper.selectDropdownOptionByLabel('用途','播放')
		
		
		//WebUI.click(findTestObject('主畫面/編輯媒資頁/類型下拉'))
		
		//DropdownHelper.selectVisibleDropdownOptionScroll('節目')
		//DropdownHelper.selectDropdownOptionByLabel('類型',progtype)
		
		//WebUI.uploadFile(findTestObject('主畫面/編輯媒資頁/上傳字幕'), subtitleFilePath)
		
		WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/video'))

		WebUI.uploadFile(findTestObject('主畫面/編輯媒資頁/upload_video'), currentFilePath)

		WebUI.delay(20)
		
		if (WebUI.waitForElementVisible(findTestObject('主畫面/編輯媒資頁/主檔刪除'), 60, FailureHandling.OPTIONAL)) {
			WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/radio1'))
		}
		WebUI.delay(5)
		
		WebUI.uploadFile(findTestObject('主畫面/編輯媒資頁/上傳字幕'), subtitleFilePath)
		
		//WebUI.waitForElementVisible(findTestObject('主畫面/編輯媒資頁/主檔刪除'), 60, FailureHandling.OPTIONAL)

		//WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/儲存'))
		
		WebUI.click(findTestObject('主畫面/編輯媒資頁/儲存'))

		WebUI.delay(2)

		if (WebUI.verifyElementClickable(findTestObject('主畫面/編輯媒資頁/儲存'), FailureHandling.OPTIONAL)) {
			WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/儲存'))
			//WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/取消'))
		}
	
		WebUI.comment(('ID: ' + specificVideoID) + ' 上傳成功。')
	
		isSuccess = true

	}
	WebUI.delay(2)
	
	if (WebUI.verifyElementClickable(findTestObject('主畫面/未公開媒資頁/編輯媒資1'), FailureHandling.OPTIONAL)) {
		WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/編輯媒資1'))
		
		
		//WebUIExtensions.setTextByLabel('節目名稱', correspondingTitle)
			
		TimecodeHelper.inputTimecode(findTestObject('主畫面/編輯媒資頁/timecode起'), '00:00:00:00')
		
		String tapeValue = TimecodeHelper.getTimecode(findTestObject('主畫面/編輯媒資頁/時長'))
		
		TimecodeHelper.inputTimecode(findTestObject('主畫面/編輯媒資頁/timecode迄'), tapeValue)
		
		WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/儲存'))
		
		WebUI.delay(2)
		
		if (WebUI.verifyElementClickable(findTestObject('主畫面/編輯媒資頁/儲存'), FailureHandling.OPTIONAL)) {
			WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/儲存'))
		}
	}
	
	if (Math.random() < 0.5) {   // 50% 機率
		WebUI.callTestCase(findTestCase('05BaseTest/編輯列管'), [:], FailureHandling.CONTINUE_ON_FAILURE)
		WebUI.comment("執行了：編輯列管")
	} else {
		WebUI.comment("略過：編輯列管")
	}
}
catch (Exception e) {
	KeywordUtil.markWarning((('處理 ID: ' + specificVideoID) + ' 時發生錯誤: ') + e.getMessage())
}

WebUI.comment('--- 自動化腳本執行完畢 ---')

	// --- 5. 如果上傳成功，則更新計數器和 ID 檔案 ---
	if (isSuccess) {
		executedLoops++ // 成功完成一次，計數器加 1
		
		// 從列表中移除已使用的第一個 ID
		List<String> remainingIds = allVideoIds.drop(1)
		
		// --- 將剩下的 ID 寫回原始檔案 (videoIdFile) 的重試邏輯 ---
		int maxRetriesVideoIdFile = 5
		long retryDelayMsVideoIdFile = 1000
		boolean videoIdFileWriteSuccessful = false

		for (int i = 0; i < maxRetriesVideoIdFile; i++) {
			try {
				// 覆蓋寫入檔案，將剩餘的 ID 寫回
				BufferedWriter writer = new BufferedWriter(new FileWriter(videoIdFile, false)) // false 表示覆蓋
				writer.write(remainingIds.join('\n'))
				writer.close()
				WebUI.comment("已成功處理 ID，並更新來源檔案: " + videoIdFilePath)
				videoIdFileWriteSuccessful = true
				break
			} catch (IOException e) {
				KeywordUtil.markWarning("更新來源 ID 檔案 '" + videoIdFilePath + "' 失敗 (嘗試 ${i + 1}/${maxRetriesVideoIdFile}): " + e.getMessage())
				if (i < maxRetriesVideoIdFile - 1) {
					WebUI.delay(retryDelayMsVideoIdFile / 1000.0)
					WebUI.comment("等待 ${retryDelayMsVideoIdFile} 毫秒後重試更新來源 ID 檔案...")
				}
			}
		}

		if (!videoIdFileWriteSuccessful) {
			KeywordUtil.markWarning("【最終錯誤】在多次重試後，仍未能更新來源 ID 檔案: " + videoIdFilePath)
		}
		// --- videoIdFile 寫入重試邏輯結束 ---


		// --- 將已使用的 ID 和 Title 寫入 _used.txt 檔案 (usedVideoIdFile) 的重試邏輯 ---
		int maxRetriesUsedFile = 5
		long retryDelayMsUsedFile = 1000
		boolean usedFileWriteSuccessful = false

		for (int i = 0; i < maxRetriesUsedFile; i++) {
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
				KeywordUtil.markWarning("寫入已使用 ID 檔案 '" + usedVideoIdFilePath + "' 失敗 (嘗試 ${i + 1}/${maxRetriesUsedFile}): " + e.getMessage())
				if (i < maxRetriesUsedFile - 1) {
					WebUI.delay(retryDelayMsUsedFile / 1000.0)
					WebUI.comment("等待 ${retryDelayMsUsedFile} 毫秒後重試寫入已使用 ID 檔案...")
				}
			}
		}

		if (!usedFileWriteSuccessful) {
			KeywordUtil.markWarning("【最終錯誤】在多次重試後，仍未能將 Title 寫入已使用 ID 檔案: " + usedVideoIdFilePath)
		}
		// --- usedVideoIdFile 寫入重試邏輯結束 ---
		
		// 將對應的 Title 加入到全域變數 List 中
		GlobalVariable.processedTitles.add(correspondingTitle)
		WebUI.comment("Title '" + correspondingTitle + "' 已加入到 GlobalVariable.processedTitles。目前列表大小: " + GlobalVariable.processedTitles.size())
	}
} // --- while 迴圈結束 ---

WebUI.comment("--- 自動化腳本執行完畢 ---")
println("已成功完成指定的 " + totalLoopsToRun + " 次上傳任務。")
println("所有已處理的 Title 列表: " + GlobalVariable.processedTitles)
