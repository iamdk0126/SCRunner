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

WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/搜尋媒資'))

//WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/輸入關鍵字'))
/*
WebUIExtensions.setTextByLabel('關鍵字',keywordStr)

//WebUIExtensions.retrySetText(findTestObject('主畫面/未公開媒資頁/輸入關鍵字'),keywordStr)

'search'
WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/搜尋'))
*/
if (keywordStr != null && keywordStr.length() > 0) {
	
	WebUIExtensions.retrySetText(findTestObject('主畫面/未公開媒資頁/輸入關鍵字'), keywordStr)
	
	'搜尋'
	WebUI.waitForElementClickable(findTestObject('主畫面/未公開媒資頁/搜尋'), 30)
	
	WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/搜尋'))
	} else {
		println "Keyword 為空，跳過輸入步驟。"
	}


println('已完成呼叫 未公開搜尋媒資。')