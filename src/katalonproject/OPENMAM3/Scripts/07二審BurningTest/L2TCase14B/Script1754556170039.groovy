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

int runloop = GlobalVariable.runloop
// 請在 GlobalVariable 設定您希望執行的時間（分鐘）
// 如果執行時間為 0，則以 runloop 次數為準
int executionTimeInMinutes = GlobalVariable.executionTimeInhours * 60

// --- 定義此特定執行要使用的帳號密碼 ---
String specificAccount = 'test264'
String specificDomain = 'TVBS'

// 請替換為 user_abc 的實際加密密碼
String specificEncryptedPassword = GlobalVariable.TestPassword

// --- 呼叫登入測試案例 ---
println('準備呼叫 TC_Login，使用帳號：' + specificAccount)

WebUI.openBrowser('')

WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam') : specificAccount, ('passwordParam') : specificEncryptedPassword,('domainParam') : specificDomain], FailureHandling.STOP_ON_FAILURE)
		
if (executionTimeInMinutes > 0) {
	// 依執行時間執行
	println('腳本將依據設定的執行時間 (' + executionTimeInMinutes + ' 分鐘) 執行。')
	def startTime = System.currentTimeMillis()
	def endTime = startTime + (executionTimeInMinutes * 60 * 1000)
	int iterationCount = 0

	while (System.currentTimeMillis() < endTime) {
		iterationCount++
		println('Loop iteration (1-indexed): ' + iterationCount)
		WebUI.comment(('現在是第 ' + iterationCount) + ' 次迴圈執行')
		WebUI.callTestCase(findTestCase('06二審CaseType/L2一鍵回調'), [('accountParam') : specificAccount], FailureHandling.STOP_ON_FAILURE)
	}

	println('已達到設定的執行時間，迴圈結束。總共執行 ' + iterationCount + ' 次。')
} else {
	// 依 runloop 次數執行
	println('腳本將依據設定的迴圈次數 (' + runloop + ' 次) 執行。')
	for (def index : (0..runloop - 1)) {
		println('Loop iteration (1-indexed): ' + (index + 1))
		WebUI.comment(('現在是第 ' + (index + 1)) + ' 次迴圈執行')
		WebUI.callTestCase(findTestCase('06二審CaseType/L2一鍵回調'), [('accountParam') : specificAccount], FailureHandling.STOP_ON_FAILURE)
	}
}

WebUI.callTestCase(findTestCase('05BaseTest/登出'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.closeBrowser()