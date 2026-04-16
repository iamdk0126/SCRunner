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
String UserNo = GlobalVariable.TestUser30
String specificAccount = ""
List<String> L1AccountList =[]
List<String> L2AccountList =[]
if (GlobalVariable.metateam == 1) {
	specificAccount = "test1"+UserNo
	L1AccountList = GlobalVariable.Team1Leader
	L2AccountList = GlobalVariable.Team1Boss
	} else if (GlobalVariable.metateam == 2) {
	specificAccount = "test2"+UserNo
	L1AccountList = GlobalVariable.Team2Leader
	L2AccountList = GlobalVariable.Team2Boss
	}

String specificDomain = GlobalVariable.DOMAIN

List<String> nodes = GlobalVariable.nodes
String node = nodes[new Random().nextInt(nodes.size())]

//List<String> L1AccountList = GlobalVariable.Team1Leader

Random randomL1 = new Random()

int randomIndex1 = randomL1.nextInt(L1AccountList.size())

String L1DisplayName = L1AccountList.get(randomIndex1)

String L1Account = CustomKeywords.'custom.dbHelper.getNameByDisplayName'(L1DisplayName)

WebUI.comment("查到的 L1 name = " + L1Account)

//List<String> L2AccountList = GlobalVariable.Team1Boss

Random randomL2 = new Random()

int randomIndex2 = randomL2.nextInt(L2AccountList.size())

String L2DisplayName = L2AccountList.get(randomIndex2)

String L2Account = CustomKeywords.'custom.dbHelper.getNameByDisplayName'(L2DisplayName)

WebUI.comment("查到的 L2 name = " + L2Account)

// 請替換為 user_abc 的實際加密密碼
String specificEncryptedPassword = GlobalVariable.TestPassword

//def specifickeyword = WebUI.callTestCase(findTestCase('05BaseTest/選擇公開無列管關鍵字'), [:], FailureHandling.CONTINUE_ON_FAILURE)

// --- 呼叫登入測試案例 ---
println('準備呼叫 TC_Login，使用帳號：' + specificAccount)


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
		//def specifickeyword = WebUI.callTestCase(findTestCase('05BaseTest/選擇未公開無列管關鍵字'), [:], FailureHandling.CONTINUE_ON_FAILURE)
		//AuthKeywords.ensureLogin(specificAccount, specificEncryptedPassword, specificDomain, true)
		WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam') : specificAccount, ('passwordParam') : specificEncryptedPassword,('domainParam') : specificDomain,('nodeParam') : node], FailureHandling.STOP_ON_FAILURE)
		def specifickeyword = CustomKeywords.'custom.KeywordHelper.getSpecifickeyword'('05BaseTest/選擇未公開無列管關鍵字')
		boolean result = WebUI.callTestCase(findTestCase('06二審CaseType/L2未公開送播'), [('accountParam') : specificAccount, ('keywordParam') : specifickeyword], FailureHandling.STOP_ON_FAILURE)
		WebUI.callTestCase(findTestCase('05BaseTest/登出'), [:], FailureHandling.STOP_ON_FAILURE)
		if (result) {
		//AuthKeywords.ensureLogin(specificAccount, specificEncryptedPassword, specificDomain, true)
		WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam') : L1Account, ('passwordParam') : specificEncryptedPassword,('domainParam') : specificDomain,('nodeParam') : node], FailureHandling.STOP_ON_FAILURE)
		WebUI.callTestCase(findTestCase('05BaseTest/送播審核核准'), [('accountParam') : specificAccount, ('keywordParam') : specifickeyword], FailureHandling.STOP_ON_FAILURE)
		WebUI.callTestCase(findTestCase('05BaseTest/登出'), [:], FailureHandling.STOP_ON_FAILURE)
		//AuthKeywords.ensureLogin(specificAccount, specificEncryptedPassword, specificDomain, true)
		WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam') : L2Account, ('passwordParam') : specificEncryptedPassword,('domainParam') : specificDomain,('nodeParam') : node], FailureHandling.STOP_ON_FAILURE)
		WebUI.callTestCase(findTestCase('05BaseTest/送播審核核准'), [('accountParam') : specificAccount, ('keywordParam') : specifickeyword], FailureHandling.STOP_ON_FAILURE)
		WebUI.callTestCase(findTestCase('05BaseTest/登出'), [:], FailureHandling.STOP_ON_FAILURE)
		WebUI.comment('已完成:未公開媒資(無列管送播-核准)')
		
		} else {
			WebUI.comment('無媒資檔案,略過回調)')
		}
		// 呼叫 Keyword：只有第 10, 20... 圈會執行 closeBrowser
		WebUIExtensions.checkAndResetBrowser(iterationCount, 0)
	}

	WebUI.comment('已達到設定的執行時間，迴圈結束。總共執行 ' + iterationCount + ' 次。')
	
} else {
	// 依 runloop 次數執行
	println('腳本將依據設定的迴圈次數 (' + runloop + ' 次) 執行。')
	for (def index : (0..runloop - 1)) {
		println('Loop iteration (1-indexed): ' + (index + 1))
		WebUI.comment(('現在是第 ' + (index + 1)) + ' 次迴圈執行')
		//def specifickeyword = WebUI.callTestCase(findTestCase('05BaseTest/選擇未公開無列管關鍵字'), [:], FailureHandling.CONTINUE_ON_FAILURE)
		//AuthKeywords.ensureLogin(specificAccount, specificEncryptedPassword, specificDomain, true)
		WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam') : specificAccount, ('passwordParam') : specificEncryptedPassword,('domainParam') : specificDomain,('nodeParam') : node], FailureHandling.STOP_ON_FAILURE)
		def specifickeyword = CustomKeywords.'custom.KeywordHelper.getSpecifickeyword'('05BaseTest/選擇未公開無列管關鍵字')
		
		boolean result = WebUI.callTestCase(findTestCase('06二審CaseType/L2未公開送播'), [('accountParam') : specificAccount, ('keywordParam') : specifickeyword], FailureHandling.STOP_ON_FAILURE)
		WebUI.callTestCase(findTestCase('05BaseTest/登出'), [:], FailureHandling.STOP_ON_FAILURE)
		if (result) {
		//AuthKeywords.ensureLogin(specificAccount, specificEncryptedPassword, specificDomain, true)
		WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam') : L1Account, ('passwordParam') : specificEncryptedPassword,('domainParam') : specificDomain,('nodeParam') : node], FailureHandling.STOP_ON_FAILURE)
		WebUI.callTestCase(findTestCase('05BaseTest/送播審核核准'), [('accountParam') : specificAccount, ('keywordParam') : specifickeyword], FailureHandling.STOP_ON_FAILURE)
		WebUI.callTestCase(findTestCase('05BaseTest/登出'), [:], FailureHandling.STOP_ON_FAILURE)
		//AuthKeywords.ensureLogin(specificAccount, specificEncryptedPassword, specificDomain, true)
		WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam') : L2Account, ('passwordParam') : specificEncryptedPassword,('domainParam') : specificDomain,('nodeParam') : node], FailureHandling.STOP_ON_FAILURE)
		WebUI.callTestCase(findTestCase('05BaseTest/送播審核核准'), [('accountParam') : specificAccount, ('keywordParam') : specifickeyword], FailureHandling.STOP_ON_FAILURE)
		WebUI.callTestCase(findTestCase('05BaseTest/登出'), [:], FailureHandling.STOP_ON_FAILURE)
		WebUI.comment('已完成:未公開媒資(無列管送播-核准)')
		} else {
			WebUI.comment('無媒資檔案,略過回調)')
		}
		// 呼叫 Keyword：只有第 10, 20... 圈會執行 closeBrowser
		WebUIExtensions.checkAndResetBrowser(index + 1, runloop)
	}
}

if (GlobalVariable.CloseBrowser) {WebUI.closeBrowser()}