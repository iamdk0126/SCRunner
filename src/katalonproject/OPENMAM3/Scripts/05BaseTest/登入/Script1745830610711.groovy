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
import custom.WebUIExtensions
import custom.DropdownHelper
import custom.AuthKeywords

// 使用傳入此測試案例的變數
String account = accountParam

//String domain = domaintParam
String domain = domainParam

String node = nodeParam

String encryptedPassword = passwordParam

// 如果 URL 可能變動，您也可以考慮將其設為變數或全域變數// 或者使用 GlobalVariable.URL
//String url = GlobalVariable.OpenMAM_url
String url = "https://${GlobalVariable[node]}:$GlobalVariable.OpenMAM_port/"

WebUI.comment("url = ${url}")
//String url = "https://$GlobalVariable.OpenMAM_ip:$GlobalVariable.OpenMAM_port/"
//String url = urlParam

// --- 測試步驟 ---
//WebUI.openBrowser('')
WebUI.setViewPortSize(1920, 1080,FailureHandling.OPTIONAL)

WebUI.maximizeWindow(FailureHandling.OPTIONAL)



int maxRetry = 3

boolean loginSuccess = false

for (int i = 1; i <= maxRetry; i++) {
	println("第 ${i} 次嘗試登入")
	// 確保每次重試都回到登入頁
	
	AuthKeywords.ensureLogin(account, encryptedPassword, domain,url, true)
	

	 loginSuccess = WebUI.verifyElementPresent(findTestObject('Header/OPENMAM'), 5, FailureHandling.OPTIONAL)
	 
	 if (loginSuccess) {
		 println("第 ${i} 次登入成功")
		 WebUI.comment("第 ${i} 次登入成功")
		 break
	 } else {
		 println("第 ${i} 次登入失敗，準備重試")
		 WebUI.comment("第 ${i} 次登入失敗，準備重試")
	 }
}

// 如果仍然登入失敗，結束測試
if (!loginSuccess) {
	WebUI.comment("登入失敗，已重試 ${maxRetry} 次仍未成功。")
	WebUI.takeScreenshot()
	WebUI.closeBrowser()
	assert false : "登入失敗"
}

WebUI.comment('已完成呼叫 登入。')
// 成功登入後，進行公告處理
WebUI.callTestCase(findTestCase('05BaseTest/公告'), [:], FailureHandling.CONTINUE_ON_FAILURE)


