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
import custom.DropdownHelper

String account = accountParam

String specifickeyword = keywordParam

WebUIExtensions.retryClick(findTestObject('主畫面/公開媒資'))

WebUI.callTestCase(findTestCase('05BaseTest/公開搜尋媒資'), [('keywordParam') : specifickeyword], FailureHandling.STOP_ON_FAILURE)

WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/Page_/item1svg'))

if (WebUI.verifyElementClickable(findTestObject('主畫面/媒資資訊頁/公開媒資回調送播'), FailureHandling.OPTIONAL)) {
	

	//WebUIExtensions.retryClick(findTestObject('主畫面/媒資資訊頁/公開媒資回調送播'))

'點匯出媒資'
	WebUIExtensions.retryClick(findTestObject('主畫面/公開媒資頁/匯出媒資'))

	//DropdownHelper.selectDropdownOptionByLabel('匯出媒資至OpenMAM_TW+','午間新聞')
	
	WebUI.delay(2)
	
	WebUI.click(findTestObject('主畫面/公開媒資頁/匯出媒資頁/匯出媒資下拉'))
	DropdownHelper.selectVisibleDropdownOptionScroll("午間新聞")
	WebUIExtensions.retryClick(findTestObject('主畫面/公開媒資頁/匯出媒資頁/匯出'))
	

} 


if (WebUI.verifyElementClickable(findTestObject('主畫面/媒資資訊頁/收合'), FailureHandling.OPTIONAL)) {
	// 如果物件存在，才執行以下這行
	WebUIExtensions.retryClick(findTestObject('主畫面/媒資資訊頁/收合'))
}

println('已完成呼叫 一鍵回調')
