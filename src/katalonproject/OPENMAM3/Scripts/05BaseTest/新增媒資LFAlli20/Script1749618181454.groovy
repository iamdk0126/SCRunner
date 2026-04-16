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

// --- 日期準備工作 ---
LocalDate today = LocalDate.now()

DateTimeFormatter formatter = DateTimeFormatter.ofPattern('yyyyMMdd')

String formattedDate = today.format(formatter)

def filePath = ('S:\\' + formattedDate) + '.txt'

File dataFile = new File(filePath)

WebUI.comment('正在嘗試讀取今日的資料檔案: ' + filePath)

// 4. 將檔案內容一行一行讀取到一個 List 中
List<String> videoIdList = dataFile.readLines('UTF-8')

WebUI.comment(('成功讀取檔案，共 ' + videoIdList.size()) + ' 行資料。')

for (int i = 1; i <= 20; i++) {
    // 2. 將迴圈的數字 i 格式化成四位數，不足的前面補 0 (例如 2 -> "0002")
    String formattedNumber = String.format('%04d', i)

    // 3. 組合出當前迴圈的完整檔案路徑
    String currentFilePath = ('D:\\20Clips\\C' + formattedNumber) + '.MXF'

    // (可選) 在日誌中印出正在上傳哪個檔案，方便追蹤進度
    WebUI.comment((('準備上傳檔案 (' + i) + '/20): ') + currentFilePath)

    '點新增媒資'
    WebUI.click(findTestObject('主畫面/新增媒資'))

    WebUI.click(findTestObject('主畫面/新增媒資頁/建立媒資'))

    '點模板'
    WebUI.click(findTestObject('主畫面/新增媒資頁/模板1'))

    WebUI.delay(2)

    '點建立'
    WebUI.click(findTestObject('主畫面/新增媒資頁/建立'))

    String videoId = (videoIdList[(i - 1)]).trim()

    // 4. 執行操作
    WebUI.setText(findTestObject('主畫面/編輯媒資頁/item1_videoid'), videoId)

    WebUI.delay(2)

    '點同步文稿'
    WebUI.click(findTestObject('主畫面/編輯媒資頁/同步文稿'))

    WebUI.delay(2)

    // 4. 執行上傳動作
    //    請將 'Page_YourPage/input_fileUpload' 換成您自己的測試物件
    WebUI.uploadFile(findTestObject('主畫面/新增媒資頁/上傳影片'), currentFilePath)

    WebUI.waitForElementNotVisible(findTestObject('主畫面/編輯媒資頁/上傳中取消'), 60, FailureHandling.OPTIONAL)

    '點儲存'
    WebUI.click(findTestObject('主畫面/編輯媒資頁/儲存'))

    WebUI.delay(2)
}

println('已完成呼叫 新增Local All 20 Clips媒資。')
WebUI.comment('已完成呼叫 新增Local All 20 Clips媒資。')
