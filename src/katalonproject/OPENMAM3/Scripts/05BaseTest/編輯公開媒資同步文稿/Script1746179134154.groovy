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
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import java.time.LocalDate as LocalDate
import java.time.format.DateTimeFormatter as DateTimeFormatter
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

int specificindex = indexParam

String videoId = (videoIdList[specificindex]).trim()

//'點未公開媒資'
//WebUI.click(findTestObject('主畫面/公開媒資'))
'編輯媒資1'
WebUI.waitForElementClickable(findTestObject('主畫面/未公開媒資頁/編輯媒資1'), 30)

WebUI.click(findTestObject('主畫面/未公開媒資頁/編輯媒資1'))

WebUI.delay(1)

WebUI.waitForElementClickable(findTestObject('主畫面/編輯媒資頁/item1_videoid'), 30)

WebUI.click(findTestObject('主畫面/編輯媒資頁/item1_videoid'))

WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/item1_videoid'), Keys.chord(Keys.CONTROL, 'a'))

WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/item1_videoid'), Keys.chord(Keys.BACK_SPACE))

WebUI.delay(2)

//WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/Page_/textarea_VIDEO ID_ant-input ant-input-status-success'), videoid)
WebUI.setText(findTestObject('主畫面/編輯媒資頁/item1_videoid'), videoId)

'同步文稿'
WebUI.waitForElementClickable(findTestObject('主畫面/編輯媒資頁/同步文稿'), 30)

WebUI.click(findTestObject('主畫面/編輯媒資頁/同步文稿'))

'儲存'
WebUI.waitForElementClickable(findTestObject('主畫面/編輯媒資頁/儲存'), 30)

WebUI.click(findTestObject('主畫面/編輯媒資頁/儲存'))

WebUI.delay(1)

println('已完成呼叫 編輯媒資同步文稿。')

