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

// --- 1. 設定迴圈次數 ---
int loopsToRun = RunloopParam

// --- 2. 準備日期與檔案路徑 ---
LocalDate today = LocalDate.now()

DateTimeFormatter formatter = DateTimeFormatter.ofPattern('yyyyMMdd')

String formattedDate = today.format(formatter)

String videoIdFilePath = ('S:\\' + formattedDate) + '.txt'

File videoIdFile = new File(videoIdFilePath)

// --- 3. 取得所有可用的 MXF 影片檔案 ---
WebUI.comment('正在讀取影片檔案目錄...')

File videoDir = new File('D:\\20Clips')

List<File> mxfFiles = videoDir.listFiles((({ def file ->
            file.isFile() && file.name.toLowerCase().endsWith('.mxf')
        }) as FileFilter)).toList()

if (mxfFiles.isEmpty()) {
    KeywordUtil.markFailedAndStop('錯誤: 在目錄 D:\\20Clips\\ 中找不到任何 .MXF 檔案!')
}

WebUI.comment(('成功找到 ' + mxfFiles.size()) + ' 個 .MXF 檔案。')

// --- 4. 讀取並準備 Video ID ---
WebUI.comment('正在嘗試讀取今日的 Video ID 檔案: ' + videoIdFilePath)

if (!(videoIdFile.exists())) {
    KeywordUtil.markFailedAndStop('錯誤: 找不到 Video ID 檔案: ' + videoIdFilePath)
}

List<String> allVideoIds = videoIdFile.readLines('UTF-8')

WebUI.comment(('成功讀取檔案，共有 ' + allVideoIds.size()) + ' 個 ID。')

if (allVideoIds.size() < loopsToRun) {
    KeywordUtil.markFailedAndStop(('錯誤: Video ID 檔案中的 ID 數量不足 ' + loopsToRun) + ' 個，無法繼續執行！')
}

List<String> idsToProcess = allVideoIds.take(loopsToRun)

List<String> remainingIds = allVideoIds.drop(loopsToRun)

// 建立一個隨機數產生器
Random randomGenerator = new Random()

// --- 5. 主要迴圈 (使用傳統 for 迴圈) ---
for (int i = 0; i < loopsToRun; i++) {
    // 為了日誌顯示，將索引 i (從0開始) 轉為從 1 開始的計數
    int currentIteration = i + 1

    // 從準備好的列表中，依序取出當前的 videoId
    String videoId = idsToProcess[i]

    // 隨機選取一個 MXF 檔案
    File randomMxfFile = mxfFiles[randomGenerator.nextInt(mxfFiles.size())]

    String currentFilePath = randomMxfFile.getAbsolutePath()

    WebUI.comment((((('準備上傳檔案 (' + currentIteration) + '/') + loopsToRun) + '): ') + currentFilePath)

    '點新增媒資'
    WebUI.click(findTestObject('主畫面/新增媒資'))

    WebUI.click(findTestObject('主畫面/新增媒資頁/建立媒資'))

    '點模板'
    WebUI.click(findTestObject('主畫面/新增媒資頁/模板1'))

    WebUI.delay(2)

    '點建立'
    WebUI.click(findTestObject('主畫面/新增媒資頁/建立'))

    // 執行操作
    WebUI.setText(findTestObject('主畫面/編輯媒資頁/item1_videoid'), videoId.trim())

    WebUI.delay(2)

    '點同步文稿'
    WebUI.click(findTestObject('主畫面/編輯媒資頁/同步文稿'))

    WebUI.delay(2)

    '執行上傳動作'
    WebUI.uploadFile(findTestObject('主畫面/新增媒資頁/上傳影片'), currentFilePath)

    WebUI.waitForElementVisible(findTestObject('主畫面/編輯媒資頁/主檔刪除'), 60, FailureHandling.OPTIONAL)

    '點儲存'
    WebUI.click(findTestObject('主畫面/編輯媒資頁/儲存'))

    WebUI.delay(2)
}

// --- 6. 所有迴圈成功執行完畢後，更新 Video ID 檔案 ---
WebUI.comment(loopsToRun + ' 個檔案已全部上傳成功，正在更新 Video ID 檔案...')

videoIdFile.write(remainingIds.join('\n'), 'UTF-8')

WebUI.comment(('Video ID 檔案更新完畢，已成功移除最前面的 ' + loopsToRun) + ' 筆 ID。')

println(('已完成 ' + loopsToRun) + ' 次隨機上傳，並更新來源資料檔。')

