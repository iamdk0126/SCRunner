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
import custom.WebUIExtensions

// 使用在 'Variables' 頁籤定義的變數
String keywordStr = keywordParam

WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資'))

WebUI.waitForElementClickable(findTestObject('主畫面/未公開媒資頁/搜尋媒資'), 30)

WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/搜尋媒資'))

if (keywordStr != null && keywordStr.length() > 0) {

	WebUIExtensions.retrySetText(findTestObject('主畫面/未公開媒資頁/輸入關鍵字'), keywordStr)

	'搜尋'
	WebUI.waitForElementClickable(findTestObject('主畫面/未公開媒資頁/搜尋'), 30)

	WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/搜尋'))
	} else {
		println "Keyword 為空，跳過輸入步驟。"
	}

'點媒資'
WebUI.waitForElementClickable(findTestObject('主畫面/未公開媒資頁/Page_/item1svg'), 30)

WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/Page_/item1svg'))

WebUI.delay(2)

//WebUIExtensions.retryClick(findTestObject('主畫面/媒資資訊頁/播放中間'))

//WebUI.delay(60)

'點媒資資訊放大X'
WebUI.waitForElementClickable(findTestObject('主畫面/媒資資訊頁/媒資資訊放大'), 30)

WebUIExtensions.retryClick(findTestObject('主畫面/媒資資訊頁/媒資資訊放大'))

WebUI.delay(15)

'點close x'
//WebUI.waitForElementClickable(findTestObject('主畫面/媒資資訊頁/媒資資訊關閉X'), 30)

WebUIExtensions.retryClick(findTestObject('主畫面/媒資資訊頁/媒資資訊關閉X'))


'點媒資資訊媒資檔案'
WebUI.waitForElementClickable(findTestObject('主畫面/媒資資訊頁/媒資檔案'), 30)

WebUIExtensions.retryClick(findTestObject('主畫面/媒資資訊頁/媒資檔案'))

'點媒資資訊列管資訊'
WebUI.waitForElementClickable(findTestObject('主畫面/媒資資訊頁/列管資訊'), 30)

WebUIExtensions.retryClick(findTestObject('主畫面/媒資資訊頁/列管資訊'))

WebUI.delay(15)

WebUIExtensions.retryClick(findTestObject('主畫面/媒資資訊頁/收合'))

WebUI.delay(5)

'點OpenMAM回主頁'
WebUI.waitForElementClickable(findTestObject('Header/OPENMAM'), 30)

WebUIExtensions.retryClick(findTestObject('Header/OPENMAM'))


println('已完成呼叫 未公開媒資(檢視媒資資訊)')

