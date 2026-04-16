import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.util.KeywordUtil as KeywordUtil
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.testobject.ConditionType as ConditionType
import com.kms.katalon.core.testobject.TestObject as TestObject
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.WebElement as WebElement
import java.time.LocalDate as LocalDate
import java.time.format.DateTimeFormatter as DateTimeFormatter
import java.util.Random as Random
import java.io.File as File
import java.sql.Connection as Connection
import java.sql.DriverManager as DriverManager
import java.sql.ResultSet as ResultSet
import java.sql.Statement as Statement
import java.util.ArrayList as ArrayList
import java.util.List as List
import java.io.FileWriter as FileWriter
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

int totalLoopsToRun = 20  // 讀前 20 筆
// --- 常數設定 ---
String BASE_DRIVE_S = GlobalVariable.BASE_DRIVE_S

String VIDEO_DIR_D = GlobalVariable.LocalFolder

//String specificVideoID = ""

//String correspondingTitle = ""

//String currentFilePath = ""

String subtitleFilePath = ""

String VideoID = ""

String Title =  ""

String progtype = ""



long ONE_HOUR_IN_SECONDS = 3600

Random randomGenerator = new Random()

Random rand = new Random()



LocalDate today = LocalDate.now()

DateTimeFormatter formatter = DateTimeFormatter.ofPattern('yyyyMMdd')

String formattedDate = today.format(formatter)

// 改成 XML 檔案
String xmlFilePath = "${BASE_DRIVE_S}${formattedDate}-air.all.xml"

// 讀 XML 前 20 筆 VideoID + Title
List<Map<String, String>> videoList = XmlReader.getFirstNVideoAndTitle(xmlFilePath, totalLoopsToRun)
if (videoList.isEmpty()) {
    KeywordUtil.markFailedAndStop("XML 檔案無有效 VideoID 資料或路徑錯誤: " + xmlFilePath)
}

// 初始化 GlobalVariable
if (GlobalVariable.processedTitles == null || !(GlobalVariable.processedTitles instanceof List)) {
    GlobalVariable.processedTitles = new ArrayList<String>()
}

// 開始依序處理每筆資料
int executedLoops = 0
videoList.eachWithIndex { item, idx ->

    String specificVideoID = item.videoId
    String correspondingTitle = item.title

    int currentFileNumber = idx + 1
    String formattedNumber = String.format('%04d', currentFileNumber)
    String currentFilePath = ('D:\\20Clips\\C' + formattedNumber) + '.MXF'
    String currentSrtPath = ('D:\\20Clips\\C' + formattedNumber) + '.srt'
	
	subtitleFilePath = currentSrtPath

    File mxfFile = new File(currentFilePath)
    if (!mxfFile.exists()) {
        KeywordUtil.markWarning("找不到 MXF 檔案: " + currentFilePath + "，此次上傳將跳過。")
        return
    }

    WebUI.comment("處理第 ${currentFileNumber} 筆任務")
    WebUI.comment("VideoID: ${specificVideoID}, Title: ${correspondingTitle}")
    WebUI.comment("上傳檔案: ${currentFilePath}, 字幕檔: ${currentSrtPath}")

    try {
        // --- UI 操作示範 ---
        	if (WebUI.verifyElementClickable(findTestObject('主畫面/新增媒資'), FailureHandling.OPTIONAL)) {
				WebUI.click(findTestObject('主畫面/新增媒資'))
			} else {
				return
			}
    
	
			WebUI.delay(2)
	
			if (WebUI.verifyElementClickable(findTestObject('主畫面/新增媒資頁/iNews匯入'), FailureHandling.OPTIONAL)) {
				WebUIExtensions.retryClick(findTestObject('主畫面/新增媒資頁/建立媒資'))
			}
	
        WebUI.click(findTestObject('主畫面/新增媒資頁/模板1'))
        WebUI.delay(2)
        WebUI.click(findTestObject('主畫面/新增媒資頁/建立'))
        WebUIExtensions.setTextByLabel('iNews_VideoID', specificVideoID)
		
		WebUI.callTestCase(findTestCase('05BaseTest/編輯媒資分析VideoID選擇媒資分類'), [('VideoIDParam') : specificVideoID], FailureHandling.CONTINUE_ON_FAILURE)
		
	// 模擬 Ctrl + A + Backspace
		WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/輸入Title'), Keys.chord(Keys.CONTROL,'a'))
		WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/輸入Title'), Keys.chord(Keys.BACK_SPACE))
	
		WebUI.delay(2)
	
	//WebUIExtensions.retrySetText(findTestObject('主畫面/編輯媒資頁/輸入Title'), correspondingTitle)
		WebUIExtensions.setTextByLabel('媒資名稱', correspondingTitle)
	
        //WebUIExtensions.setTextByLabel('節目名稱', correspondingTitle)
		
		//DropdownHelper.selectDropdownOptionByLabel('用途','播出')
		
		//WebUIExtensions.setTextByLabel('音軌設定', 'LRLR----')
		
		
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
				
		
		
        WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/儲存'))
        
		if (Math.random() < 0.5) {   // 50% 機率
			WebUI.callTestCase(findTestCase('05BaseTest/編輯列管'), [:], FailureHandling.CONTINUE_ON_FAILURE)
			WebUI.comment("執行了：編輯列管")
		} else {
			WebUI.comment("略過：編輯列管")
		}

    } catch (Exception e) {
        KeywordUtil.markWarning("處理 VideoID: ${specificVideoID} 時發生錯誤: " + e.getMessage())
    }
}

WebUI.comment("--- 自動化腳本執行完畢 ---")
println("已成功處理 ${executedLoops} 筆資料")
println("所有已處理 Title: " + GlobalVariable.processedTitles)
