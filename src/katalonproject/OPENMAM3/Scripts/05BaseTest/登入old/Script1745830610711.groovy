import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
//import com.kms.katalon.core.windows.keyword.WindowsBuiltInKeywords as Windows
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows

// 使用傳入此測試案例的變數
String account = accountParam

String encryptedPassword = passwordParam

// 如果 URL 可能變動，您也可以考慮將其設為變數或全域變數// 或者使用 GlobalVariable.URL
//String url = GlobalVariable.OpenMAM_url
String url = "http://$GlobalVariable.OpenMAM_ip:$GlobalVariable.OpenMAM_port/"

// --- 測試步驟 ---
//WebUI.openBrowser('')
WebUI.maximizeWindow(FailureHandling.OPTIONAL)

WebUI.navigateToUrl(url)

// 使用 account 變數填入帳號
WebUI.setText(findTestObject('登入頁/輸入帳號'), account)

// 使用 encryptedPassword 變數填入加密密碼
WebUI.setText(findTestObject('登入頁/輸入密碼'), encryptedPassword)

// 根據 account 變數的值判斷是否勾選「使用網域帳號登入」
if (account.equalsIgnoreCase('root')) {
    // 如果 account 是 "root"，確保「使用網域帳號登入」是未選取狀態
    WebUI.uncheck(findTestObject('登入頁/使用網域帳號登入'))

    println('帳號為 root，已確保 \'使用網域帳號登入\' 為未選取。')

    WebUI.comment('帳號為 root，已確保 \'使用網域帳號登入\' 為未選取。' // 如果 account 是其他帳號，確保「使用網域帳號登入」是選取狀態
        )
} else {
    WebUI.check(findTestObject('登入頁/使用網域帳號登入'))

    println('帳號非 root，已確保 \'使用網域帳號登入\' 為選取。')

    WebUI.comment('帳號非 root，已確保 \'使用網域帳號登入\' 為選取。')
}

WebUI.waitForElementClickable(findTestObject('登入頁/登入'), 30)

WebUI.click(findTestObject('登入頁/登入'))

println('已完成呼叫 登入。')

WebUI.comment('已完成呼叫 登入。')

WebUI.callTestCase(findTestCase('05BaseTest/公告'), [:], FailureHandling.CONTINUE_ON_FAILURE)

