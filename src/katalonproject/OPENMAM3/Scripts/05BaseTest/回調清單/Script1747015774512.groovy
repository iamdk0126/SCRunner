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

//WebUI.callTestCase(findTestCase('05BaseTest/填寫回調原因'), [:], FailureHandling.STOP_ON_FAILURE)

//if (WebUI.verifyElementPresent(findTestObject('主畫面/媒資資訊頁/收合'), 5, FailureHandling.OPTIONAL)) {
    // 如果物件存在，才執行以下這行
//    WebUI.click(findTestObject('主畫面/媒資資訊頁/收合'))

//    WebUI.delay(2)
//}


'點Header回調清單'
WebUIExtensions.retryClick(findTestObject('Header/回調清單'))

WebUI.waitForElementVisible(findTestObject('回調清單頁/申請回調'), 3)

TestObject btnConfirm = findTestObject('回調清單頁/申請回調')
TestObject hideButton = findTestObject('回調清單頁/關閉回調清單')

// 呼叫方式：把 A(要點擊的) 和 B(要檢查消失的) 傳進去
WebUIExtensions.clickUntilTargetHides(btnConfirm, hideButton,5 ,5)

println('已完成呼叫 回調清單')

