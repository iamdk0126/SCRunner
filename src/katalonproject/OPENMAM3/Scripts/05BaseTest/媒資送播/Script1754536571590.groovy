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

WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/Page_/item1svg'))

boolean clickResult = false
'點回調送播'
if (WebUI.verifyElementClickable(findTestObject('主畫面/媒資資訊頁/公開媒資回調送播'), FailureHandling.OPTIONAL)) {
	

	WebUIExtensions.retryClick(findTestObject('主畫面/媒資資訊頁/公開媒資回調送播'))

'點媒資送播'
	WebUIExtensions.retryClick(findTestObject('主畫面/公開媒資頁/媒資送播'))

	WebUI.callTestCase(findTestCase('05BaseTest/填寫送播原因'), [:], FailureHandling.OPTIONAL)

	clickResult = true

} else {
	
	clickResult = false
}


if (WebUI.verifyElementClickable(findTestObject('主畫面/媒資資訊頁/收合'), FailureHandling.OPTIONAL)) {
	// 如果物件存在，才執行以下這行
	WebUIExtensions.retryClick(findTestObject('主畫面/媒資資訊頁/收合'))
}
return clickResult


println('已完成呼叫 媒資送播')

