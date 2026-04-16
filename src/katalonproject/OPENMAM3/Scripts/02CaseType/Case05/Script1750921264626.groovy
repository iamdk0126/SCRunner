import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import internal.GlobalVariable as GlobalVariable

/*
// --- 定義此特定執行要使用的帳號密碼 ---
String specificAccount = GlobalVariable.TestUser1

// 請替換為 user_abc 的實際加密密碼
String specificEncryptedPassword = GlobalVariable.TestPassword
*/
// 您想測試的特定字串
//使用無列管未公開媒資全域參數 List
String specifickeyword = keywordParam

//String account = accountParam
/*
// --- 呼叫登入測試案例 ---
println('準備呼叫 TC_Login，使用帳號：' + specificAccount)

WebUI.openBrowser('')

// 呼叫 TC_Login 並傳遞參數
// Map 中的鍵 ('accountParam', 'passwordParam') 必須與 TC_Login 中定義的變數名稱完全相符
WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam') : specificAccount, ('passwordParam') : specificEncryptedPassword], 
    FailureHandling.STOP_ON_FAILURE)
*/
//WebUI.waitForElementClickable(findTestObject('主畫面/未公開媒資'), 30)
//WebUI.click(findTestObject('主畫面/未公開媒資'))
WebUI.callTestCase(findTestCase('05BaseTest/未公開搜尋媒資點同步文稿'), [('keywordParam') : specifickeyword], FailureHandling.OPTIONAL)

/*
WebUI.callTestCase(findTestCase('05BaseTest/登出'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.closeBrowser()
*/
println('已完成 Case5 - 未公開媒資(編輯-同步文稿)')
WebUI.comment('已完成 Case5 - 未公開媒資(編輯-同步文稿)')
