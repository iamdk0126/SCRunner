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
import java.util.ArrayList as ArrayList
import java.util.List as List
import custom.AuthKeywords
import custom.WebUIExtensions as WebUIExtensions

// --- [工具函式] 顯示剩餘時間 (解決 BigDecimal 與 IDE 解析問題) ---
def logRemainingTime(long targetEndTime, String stageName) {
    long remaining = targetEndTime - System.currentTimeMillis()
    if (remaining < 0) remaining = 0
    
    // 使用 .toLong() 提升 IDE 相容性，解決 ExpressionWrapper null 錯誤
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

// --- 定義此特定執行要使用的帳號密碼 ---
String UserNo = GlobalVariable.TestUser9
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

// 隨機抽選 L1/L2
String L1DisplayName = L1AccountList.get(new Random().nextInt(L1AccountList.size()))
String L1Account = CustomKeywords.'custom.dbHelper.getNameByDisplayName'(L1DisplayName)
String L2DisplayName = L2AccountList.get(new Random().nextInt(L2AccountList.size()))
String L2Account = CustomKeywords.'custom.dbHelper.getNameByDisplayName'(L2DisplayName)
String specificEncryptedPassword = GlobalVariable.TestPassword

// 確保 10L 是 Long 型別，用於計次模式
long infiniteEndTime = System.currentTimeMillis() + (10L * 365 * 24 * 60 * 60 * 1000)

if (executionTimeInMinutes > 0) {
    // --- [計時模式] ---
    WebUI.comment('腳本將依據設定的執行時間 (' + executionTimeInMinutes + ' 分鐘) 執行。')
    def startTime = System.currentTimeMillis()
    def endTime = startTime + (executionTimeInMinutes * 60 * 1000)
    int iterationCount = 0

    while (System.currentTimeMillis() < endTime) {
        iterationCount++
        logRemainingTime(endTime, "第 ${iterationCount} 圈開始")
        
        // 1. 登入申請人
        WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam') : specificAccount, ('passwordParam') : specificEncryptedPassword, ('domainParam') : specificDomain, ('nodeParam') : node], FailureHandling.STOP_ON_FAILURE)
        
        // 2. 執行歸檔/回調 (取決於您的 Test Case 路徑，請根據腳本性質調整 findTestCase 名稱)
        // 這裡以歸檔為例，如果是回調腳本請自行更換路徑
        def specifickeyword = WebUI.callTestCase(findTestCase('06二審CaseType/L2媒資歸檔'), [('accountParam') : specificAccount, ('endTimeParam') : endTime], FailureHandling.STOP_ON_FAILURE)
        
        if (specifickeyword != null) {
            WebUI.callTestCase(findTestCase('05BaseTest/登出'), [:], FailureHandling.STOP_ON_FAILURE)
            
            // 3. L1 審核
            logRemainingTime(endTime, "第 ${iterationCount} 圈 - L1 審核階段")
            WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam') : L1Account, ('passwordParam') : specificEncryptedPassword, ('domainParam') : specificDomain, ('nodeParam') : node], FailureHandling.STOP_ON_FAILURE)
            def l1Result = WebUI.callTestCase(findTestCase('05BaseTest/歸檔送出核准'), [('accountParam') : specificAccount, ('keywordParam') : specifickeyword, ('endTimeParam') : endTime], FailureHandling.STOP_ON_FAILURE)
            WebUI.callTestCase(findTestCase('05BaseTest/登出'), [:], FailureHandling.STOP_ON_FAILURE)
            
            if (l1Result != null) {
                // 4. L2 審核
                logRemainingTime(endTime, "第 ${iterationCount} 圈 - L2 審核階段")
                WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam') : L2Account, ('passwordParam') : specificEncryptedPassword, ('domainParam') : specificDomain, ('nodeParam') : node], FailureHandling.STOP_ON_FAILURE)
                WebUI.callTestCase(findTestCase('05BaseTest/歸檔送出核准'), [('accountParam') : specificAccount, ('keywordParam') : specifickeyword, ('endTimeParam') : endTime], FailureHandling.STOP_ON_FAILURE)
                WebUI.callTestCase(findTestCase('05BaseTest/登出'), [:], FailureHandling.STOP_ON_FAILURE)
                WebUI.comment('已完成: 全流程 (申請->L1->L2)')
            } else {
                WebUI.comment("【中斷】L1 審核超時或失敗，跳過 L2 並結束任務。")
                break 
            }
        } else {
            WebUI.comment("【中斷】歸檔申請超時(可能QC中)，結束任務。")
            break 
        }
        WebUIExtensions.checkAndResetBrowser(iterationCount, 0)
    }
} else {
    // --- [計次模式] ---
    println('腳本將依據設定的迴圈次數 (' + runloop + ' 次) 執行。')
    for (def index : (0..runloop - 1)) {
        int iterationCount = index + 1
        WebUI.comment("第 ${iterationCount} 次迴圈執行")
        
        WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam') : specificAccount, ('passwordParam') : specificEncryptedPassword, ('domainParam') : specificDomain, ('nodeParam') : node], FailureHandling.STOP_ON_FAILURE)
        def specifickeyword = WebUI.callTestCase(findTestCase('06二審CaseType/L2媒資歸檔'), [('accountParam') : specificAccount, ('endTimeParam') : infiniteEndTime], FailureHandling.STOP_ON_FAILURE)
        
        if (specifickeyword != null) {
            WebUI.callTestCase(findTestCase('05BaseTest/登出'), [:], FailureHandling.STOP_ON_FAILURE)
            WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam') : L1Account, ('passwordParam') : specificEncryptedPassword, ('domainParam') : specificDomain, ('nodeParam') : node], FailureHandling.STOP_ON_FAILURE)
            WebUI.callTestCase(findTestCase('05BaseTest/歸檔送出核准'), [('accountParam') : specificAccount, ('keywordParam') : specifickeyword, ('endTimeParam') : infiniteEndTime], FailureHandling.STOP_ON_FAILURE)
            WebUI.callTestCase(findTestCase('05BaseTest/登出'), [:], FailureHandling.STOP_ON_FAILURE)
            
            WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam') : L2Account, ('passwordParam') : specificEncryptedPassword, ('domainParam') : specificDomain, ('nodeParam') : node], FailureHandling.STOP_ON_FAILURE)
            WebUI.callTestCase(findTestCase('05BaseTest/歸檔送出核准'), [('accountParam') : specificAccount, ('keywordParam') : specifickeyword, ('endTimeParam') : infiniteEndTime], FailureHandling.STOP_ON_FAILURE)
            WebUI.callTestCase(findTestCase('05BaseTest/登出'), [:], FailureHandling.STOP_ON_FAILURE)
        }
        WebUIExtensions.checkAndResetBrowser(iterationCount, runloop)
    }
}

if (GlobalVariable.CloseBrowser) { WebUI.closeBrowser() }