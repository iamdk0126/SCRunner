import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import com.kms.katalon.core.util.KeywordUtil as KeywordUtil
import internal.GlobalVariable as GlobalVariable
import java.time.LocalDate as LocalDate
import java.time.format.DateTimeFormatter as DateTimeFormatter
import org.openqa.selenium.Keys as Keys
import java.io.File as File
import java.util.Random as Random
import java.io.FileFilter as FileFilter

// --- 日期與檔案路徑準備 ---
LocalDate today = LocalDate.now()

DateTimeFormatter formatter = DateTimeFormatter.ofPattern('yyyyMMdd')

String formattedDate = today.format(formatter)

String filePath = ('S:\\' + formattedDate) + '.txt'

File dataFile = new File(filePath)

WebUI.comment('正在嘗試讀取今日的資料檔案: ' + filePath)

// --- 1. 一次性讀取所有 Video ID ---
if (!(dataFile.exists())) {
    KeywordUtil.markFailedAndStop('錯誤: 找不到資料檔案: ' + filePath)
}

List<String> allVideoIds = dataFile.readLines('UTF-8')

WebUI.comment(('成功讀取檔案，共有 ' + allVideoIds.size()) + ' 行資料。')

// --- 2. 檢查 ID 數量是否足夠 ---
int loopsToRun = 20

if (allVideoIds.size() < loopsToRun) {
    KeywordUtil.markFailedAndStop(('錯誤: 資料檔案中的 Video ID 不足 ' + loopsToRun) + ' 個，無法繼續執行！')
}

// --- 3. 將要處理的 ID 和剩餘的 ID 分開 ---
List<String> idsToProcess = allVideoIds.take(loopsToRun)

List<String> remainingIds = allVideoIds.drop(loopsToRun)

// --- 4. 主要迴圈 (使用傳統 for 迴圈) ---
for (int i = 0; i < loopsToRun; i++) {
    // 為了日誌顯示和檔案命名，將索引 i (從0開始) 轉為從 1 開始的計數
    int currentFileNumber = i + 1

    // 從準備好的列表中，依序取出當前的 videoId
    String videoId = idsToProcess[i]
	String specificVideoID = videoId.trim()
    // 將檔案編號格式化成四位數
    String formattedNumber = String.format('%04d', currentFileNumber)

    // 組合出當前迴圈的完整檔案路徑
    String currentFilePath = ('D:\\20Clips\\C' + formattedNumber) + '.MXF'

    String currentsrtFilePath = ('D:\\20Clips\\C' + formattedNumber) + '.srt'

    // 在日誌中印出正在上傳哪個檔案
    WebUI.comment((((('準備上傳檔案 (' + currentFileNumber) + '/') + loopsToRun) + '): ') + currentFilePath)

    '點新增媒資'
    WebUI.click(findTestObject('主畫面/新增媒資'))

    WebUI.click(findTestObject('主畫面/新增媒資頁/建立媒資'))

    '點模板'
    WebUI.click(findTestObject('主畫面/新增媒資頁/模板1'))

    WebUI.delay(2)

    '點建立'
    WebUI.click(findTestObject('主畫面/新增媒資頁/建立'))

    // 使用從列表中取出的 videoId 並依據videoID選擇媒資分類
    WebUI.setText(findTestObject('主畫面/編輯媒資頁/item1_videoid'), specificVideoID)
    WebUI.callTestCase(findTestCase('05BaseTest/編輯媒資分析VideoID選擇媒資分類'), [('VideoIDParam') : specificVideoID], FailureHandling.CONTINUE_ON_FAILURE)
    WebUI.delay(2)

    '點同步文稿'
    WebUI.click(findTestObject('主畫面/編輯媒資頁/同步文稿'))

    WebUI.delay(2)

    '執行上傳動作'
    WebUI.uploadFile(findTestObject('主畫面/新增媒資頁/上傳影片'), currentFilePath)

    WebUI.waitForElementVisible(findTestObject('主畫面/編輯媒資頁/主檔刪除'), 60, FailureHandling.STOP_ON_FAILURE)

    WebUI.uploadFile(findTestObject('主畫面/編輯媒資頁/上傳字幕'), currentsrtFilePath)

    '點儲存'
    WebUI.click(findTestObject('主畫面/編輯媒資頁/儲存'))

    WebUI.delay(2)
}

// --- 5. 所有迴圈成功執行完畢後，將剩餘的 ID 寫回原檔案 ---
WebUI.comment(loopsToRun + ' 個檔案已全部上傳成功，正在更新資料檔案...')

// 使用 `remainingIds` 列表的內容覆寫整個文字檔
dataFile.write(remainingIds.join('\n'), 'UTF-8')

WebUI.comment(('資料檔案更新完畢，已成功移除最前面的 ' + loopsToRun) + ' 筆 ID。')

println('已完成呼叫 新增20Clip媒資，並更新來源資料檔。')
WebUI.comment('已完成呼叫 新增20Clip媒資，並更新來源資料檔。')

