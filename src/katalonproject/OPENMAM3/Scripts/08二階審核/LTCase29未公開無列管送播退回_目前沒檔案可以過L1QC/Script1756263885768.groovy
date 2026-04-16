import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import internal.GlobalVariable as GlobalVariable
import java.util.Random as Random
import java.util.List as List
import custom.WebUIExtensions as WebUIExtensions

// --- 定義顯示剩餘時間的方法 (使用 .toLong() 避免 IDE 解析錯誤) ---
def logRemainingTime(long targetEndTime, String stageName) {
	long currentTime = System.currentTimeMillis()
	long remaining = targetEndTime - currentTime
	if (remaining < 0) remaining = 0
	
	// 改用 .toLong() 提升 IDE 相容性，解決 ExpressionWrapper null 錯誤
	long totalSeconds = (remaining / 1000).toLong()
	long minutes = (totalSeconds / 60).toLong()
	long seconds = (totalSeconds % 60).toLong()
	
	String timeStr = String.format("%02d:%02d", minutes, seconds)
	println("【時間檢查】目前階段：${stageName}，剩餘執行時間：${timeStr}")
	WebUI.comment("剩餘時間：${timeStr} (${stageName})")
	return remaining
}

int runloop = GlobalVariable.runloop
int executionTimeInMinutes = GlobalVariable.executionTimeInhours * 60

// --- 帳號與環境定義 ---
String UserNo = GlobalVariable.TestUser29
String specificAccount = ""
List<String> L1AccountList = []
List<String> L2AccountList = []
if (GlobalVariable.metateam == 1) {
	specificAccount = "test1" + UserNo
	L1AccountList = GlobalVariable.Team1Leader
	L2AccountList = GlobalVariable.Team1Boss
} else if (GlobalVariable.metateam == 2) {
	specificAccount = "test2" + UserNo
	L1AccountList = GlobalVariable.Team2Leader
	L2AccountList = GlobalVariable.Team2Boss
}

String specificDomain = GlobalVariable.DOMAIN
List<String> nodes = GlobalVariable.nodes
String node = nodes[new Random().nextInt(nodes.size())]

String L1DisplayName = L1AccountList.get(new Random().nextInt(L1AccountList.size()))
String L1Account = CustomKeywords.'custom.dbHelper.getNameByDisplayName'(L1DisplayName)
String L2DisplayName = L2AccountList.get(new Random().nextInt(L2AccountList.size()))
String L2Account = CustomKeywords.'custom.dbHelper.getNameByDisplayName'(L2DisplayName)
String specificEncryptedPassword = GlobalVariable.TestPassword

// 10L 確保使用 Long 運算，避免溢位
long infiniteEndTime = System.currentTimeMillis() + (10L * 365 * 24 * 60 * 60 * 1000)

if (executionTimeInMinutes > 0) {
	println('腳本將依據設定的執行時間 (' + executionTimeInMinutes + ' 分鐘) 執行。')
	def startTime = System.currentTimeMillis()
	def endTime = startTime + (executionTimeInMinutes * 60 * 1000)
	int iterationCount = 0

	while (System.currentTimeMillis() < endTime) {
		iterationCount++
		logRemainingTime(endTime, "第 ${iterationCount} 圈開始")
		
		WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam'):specificAccount, ('passwordParam'):specificEncryptedPassword, ('domainParam'):specificDomain, ('nodeParam'):node], FailureHandling.STOP_ON_FAILURE)
		def specifickeyword = CustomKeywords.'custom.KeywordHelper.getSpecifickeyword'('05BaseTest/選擇未公開無列管關鍵字')
		
		boolean result = WebUI.callTestCase(findTestCase('06二審CaseType/L2未公開送播'), [('accountParam'):specificAccount, ('keywordParam'):specifickeyword, ('endTimeParam'):endTime], FailureHandling.STOP_ON_FAILURE)
		WebUI.callTestCase(findTestCase('05BaseTest/登出'), [:], FailureHandling.STOP_ON_FAILURE)
		
		if (result) {
			logRemainingTime(endTime, "第 ${iterationCount} 圈 - L1 審核")
			WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam'):L1Account, ('passwordParam'):specificEncryptedPassword, ('domainParam'):specificDomain, ('nodeParam'):node], FailureHandling.STOP_ON_FAILURE)
			boolean l1Status = WebUI.callTestCase(findTestCase('05BaseTest/送播審核核准'), [('accountParam'):specificAccount, ('keywordParam'):specifickeyword, ('endTimeParam'):endTime], FailureHandling.STOP_ON_FAILURE)
			WebUI.callTestCase(findTestCase('05BaseTest/登出'), [:], FailureHandling.STOP_ON_FAILURE)

			if (l1Status) {
				logRemainingTime(endTime, "第 ${iterationCount} 圈 - L2 審核")
				WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam'):L2Account, ('passwordParam'):specificEncryptedPassword, ('domainParam'):specificDomain, ('nodeParam'):node], FailureHandling.STOP_ON_FAILURE)
				WebUI.callTestCase(findTestCase('05BaseTest/送播審核退回'), [('accountParam'):specificAccount, ('keywordParam'):specifickeyword, ('endTimeParam'):endTime], FailureHandling.STOP_ON_FAILURE)
				WebUI.callTestCase(findTestCase('05BaseTest/登出'), [:], FailureHandling.STOP_ON_FAILURE)
			} else {
				WebUI.comment('L1 審核失敗或超時，跳過 L2 並結束迴圈')
				//break
			}
		} else {
			WebUI.comment('媒資送播失敗或超時，結束迴圈')
			//break
		}
		WebUIExtensions.checkAndResetBrowser(iterationCount, 0)
	}
} else {
	println('腳本將依據設定的迴圈次數 (' + runloop + ' 次) 執行。')
	for (def index : (0..runloop - 1)) {
		int iterationCount = index + 1
		WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam'):specificAccount, ('passwordParam'):specificEncryptedPassword, ('domainParam'):specificDomain, ('nodeParam'):node], FailureHandling.STOP_ON_FAILURE)
		def specifickeyword = CustomKeywords.'custom.KeywordHelper.getSpecifickeyword'('05BaseTest/選擇未公開無列管關鍵字')
		
		boolean result = WebUI.callTestCase(findTestCase('06二審CaseType/L2未公開送播'), [('accountParam'):specificAccount, ('keywordParam'):specifickeyword, ('endTimeParam'):infiniteEndTime], FailureHandling.STOP_ON_FAILURE)
		WebUI.callTestCase(findTestCase('05BaseTest/登出'), [:], FailureHandling.STOP_ON_FAILURE)
		
		if (result) {
			WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam'):L1Account, ('passwordParam'):specificEncryptedPassword, ('domainParam'):specificDomain, ('nodeParam'):node], FailureHandling.STOP_ON_FAILURE)
			WebUI.callTestCase(findTestCase('05BaseTest/送播審核核准'), [('accountParam'):specificAccount, ('keywordParam'):specifickeyword, ('endTimeParam'):infiniteEndTime], FailureHandling.STOP_ON_FAILURE)
			WebUI.callTestCase(findTestCase('05BaseTest/登出'), [:], FailureHandling.STOP_ON_FAILURE)
			
			WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam'):L2Account, ('passwordParam'):specificEncryptedPassword, ('domainParam'):specificDomain, ('nodeParam'):node], FailureHandling.STOP_ON_FAILURE)
			WebUI.callTestCase(findTestCase('05BaseTest/送播審核退回'), [('accountParam'):specificAccount, ('keywordParam'):specifickeyword, ('endTimeParam'):infiniteEndTime], FailureHandling.STOP_ON_FAILURE)
			WebUI.callTestCase(findTestCase('05BaseTest/登出'), [:], FailureHandling.STOP_ON_FAILURE)
		}
		WebUIExtensions.checkAndResetBrowser(iterationCount, runloop)
	}
}

if (GlobalVariable.CloseBrowser) { WebUI.closeBrowser() }