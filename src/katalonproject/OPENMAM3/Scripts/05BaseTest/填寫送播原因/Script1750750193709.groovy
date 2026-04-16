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

// 檢查「請簡述回調原因」這個輸入框是否存在，並設定超時時間為 5 秒
//if (WebUI.verifyElementPresent(findTestObject('主畫面/未公開媒資頁/送播原因頁/請簡述送播原因'), 1, FailureHandling.OPTIONAL)) {

if (WebUI.waitForElementClickable(findTestObject('主畫面/未公開媒資頁/送播原因頁/請簡述送播原因'), 2,FailureHandling.OPTIONAL)) {
    // 如果物件存在，才執行以下這三行
    //WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/送播原因頁/請簡述送播原因'))

    WebUIExtensions.retrySetText(findTestObject('主畫面/未公開媒資頁/送播原因頁/請簡述送播原因'), '這裡是填寫送播原因')

    WebUI.delay(2)
	
	//if (WebUI.waitForElementClickable(findTestObject('主畫面/未公開媒資頁/送播原因頁/確定'), 5,FailureHandling.OPTIONAL)) {
	WebUIExtensions.retryClickClose(findTestObject('主畫面/未公開媒資頁/送播原因頁/確定'))
	//}
	println('已完成呼叫 填寫回調原因')
	WebUI.delay(2)    
	
}
/*
if (WebUI.waitForElementClickable(findTestObject('主畫面/未公開媒資頁/送播原因頁/確定'), 5,FailureHandling.OPTIONAL)) {
    WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/送播原因頁/確定'))
        
    WebUI.delay(2)
}*/



if (WebUI.waitForElementClickable(findTestObject('主畫面/媒資資訊頁/收合'),5,FailureHandling.OPTIONAL)) {
    // 如果物件存在，才執行以下這行
    //WebUIExtensions.retryClick(findTestObject('主畫面/媒資資訊頁/收合'))
	TestObject btnConfirm = findTestObject('主畫面/媒資資訊頁/收合')
	TestObject hideButton = findTestObject('主畫面/媒資資訊頁/公開媒資回調送播')
	
	// 呼叫方式：把 A(要點擊的) 和 B(要檢查消失的) 傳進去
	WebUIExtensions.clickUntilTargetHides(btnConfirm, hideButton,5 ,5)
    WebUI.delay(2)
}

