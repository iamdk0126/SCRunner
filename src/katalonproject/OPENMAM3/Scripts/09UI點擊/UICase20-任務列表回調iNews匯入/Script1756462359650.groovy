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
import java.util.Random as Random
//import java.io.File as File
import java.sql.Connection as Connection
import java.sql.DriverManager as DriverManager
import java.sql.ResultSet as ResultSet
import java.sql.Statement as Statement
import java.util.ArrayList as ArrayList
import java.util.List as List
//import java.io.FileWriter as FileWriter
import custom.AuthKeywords
import custom.WebUIExtensions as WebUIExtensions


int runloop = GlobalVariable.runloop
// 請在 GlobalVariable 設定您希望執行的時間（分鐘）
// 如果執行時間為 0，則以 runloop 次數為準
int executionTimeInMinutes = GlobalVariable.executionTimeInhours * 60

// --- 定義此特定執行要使用的帳號密碼 ---
List<String> UserNoFree = GlobalVariable.UserNoFree
String UserNo = UserNoFree[new Random().nextInt(UserNoFree.size())]
//String UserNo = GlobalVariable.TestUser3
String specificAccount = ""
if (GlobalVariable.metateam == 1) {
	specificAccount = "test1"+UserNo
	} else if (GlobalVariable.metateam == 2) {
		specificAccount = "test2"+UserNo
	}
String specificDomain = GlobalVariable.DOMAIN
List<String> nodes = GlobalVariable.nodes
String node = nodes[new Random().nextInt(nodes.size())]


// 請替換為 user_abc 的實際加密密碼
String specificEncryptedPassword = GlobalVariable.TestPassword

//def specifickeyword = WebUI.callTestCase(findTestCase('05BaseTest/選擇公開無列管關鍵字'), [:], FailureHandling.CONTINUE_ON_FAILURE)

// --- 呼叫登入測試案例 ---
println('準備呼叫 TC_Login，使用帳號：' + specificAccount)

/*
WebUI.openBrowser('')

WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam') : specificAccount, ('passwordParam') : specificEncryptedPassword], FailureHandling.STOP_ON_FAILURE)
*/


if (executionTimeInMinutes > 0) {
	// 依執行時間執行
	println('腳本將依據設定的執行時間 (' + executionTimeInMinutes + ' 分鐘) 執行。')
	def startTime = System.currentTimeMillis()
	def endTime = startTime + (executionTimeInMinutes * 60 * 1000)
	int iterationCount = 0
	
	WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam') : specificAccount, ('passwordParam') : specificEncryptedPassword,('domainParam') : specificDomain,('nodeParam') : node], FailureHandling.STOP_ON_FAILURE)

	while (System.currentTimeMillis() < endTime) {
		iterationCount++
		println('Loop iteration (1-indexed): ' + iterationCount)
		WebUI.comment(('現在是第 ' + iterationCount) + ' 次迴圈執行')
			
		
		'點任務列表'
		WebUI.waitForElementClickable(findTestObject('Header/任務列表'), 30)
		
		WebUIExtensions.retryClick(findTestObject('Header/任務列表'))
		
		WebUI.delay(5)
		
		'點回調媒資'
		WebUI.waitForElementClickable(findTestObject('任務列表頁/回調媒資'), 30)
		
		WebUIExtensions.retryClick(findTestObject('任務列表頁/回調媒資'))
		
		WebUI.delay(5)

		
		'點iNews匯入'
		WebUI.waitForElementClickable(findTestObject('任務列表頁/iNews匯入'), 30)
		
		WebUIExtensions.retryClick(findTestObject('任務列表頁/iNews匯入'))
		
		WebUI.delay(5)

	}

	WebUI.comment('已達到設定的執行時間，迴圈結束。總共執行 ' + iterationCount + ' 次。')
	
	//  執行登出 (不關閉瀏覽器)
	WebUI.callTestCase(findTestCase('05BaseTest/登出'), [:], FailureHandling.STOP_ON_FAILURE)

	
} else {
	// 依 runloop 次數執行
	println('腳本將依據設定的迴圈次數 (' + runloop + ' 次) 執行。')
	for (def index : (0..runloop - 1)) {
		println('Loop iteration (1-indexed): ' + (index + 1))
		
		WebUI.comment(('現在是第 ' + (index + 1)) + ' 次迴圈執行')
		//AuthKeywords.ensureLogin(specificAccount, specificEncryptedPassword, specificDomain, true)
		WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam') : specificAccount, ('passwordParam') : specificEncryptedPassword,('domainParam') : specificDomain,('nodeParam') : node], FailureHandling.STOP_ON_FAILURE)
		
		WebUI.callTestCase(findTestCase('02CaseType/Case03切換功能表'), [('accountParam') : specificAccount], FailureHandling.STOP_ON_FAILURE)
		
		WebUI.delay(5)
		
		WebUI.callTestCase(findTestCase('02CaseType/Case07未公開PlayVideo'), [('accountParam') : specificAccount], FailureHandling.STOP_ON_FAILURE)

		//  每一圈都執行登出 (不關閉瀏覽器)
		WebUI.callTestCase(findTestCase('05BaseTest/登出'), [:], FailureHandling.STOP_ON_FAILURE)
		// 呼叫 Keyword：只有第 10, 20... 圈會執行 closeBrowser
		WebUIExtensions.checkAndResetBrowser(index + 1, runloop)
	}
}


if (GlobalVariable.CloseBrowser) {WebUI.closeBrowser()}