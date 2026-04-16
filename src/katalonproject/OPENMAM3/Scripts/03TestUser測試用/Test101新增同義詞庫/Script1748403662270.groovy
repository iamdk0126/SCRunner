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

String specificAccount = 'root'

// 請替換為 user_abc 的實際加密密碼
//String specificEncryptedPassword = GlobalVariable.TestPassword
String specificEncryptedPassword = 'root'

String specificDomain = '請選擇'

String node = 'OpenMAM_ip'

String Team = 'Team1'

// --- 呼叫登入測試案例 ---
println('準備呼叫 TC_Login，使用帳號：' + specificAccount)

WebUI.openBrowser('')

// 呼叫 TC_Login 並傳遞參數
// Map 中的鍵 ('accountParam', 'passwordParam') 必須與 TC_Login 中定義的變數名稱完全相符
WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam') : specificAccount, ('passwordParam') : specificEncryptedPassword,('domainParam') : specificDomain,('nodeParam') : node], FailureHandling.STOP_ON_FAILURE)

//WebUI.callTestCase(findTestCase('04初始化設定/新增媒資分類'), [('teamParam') : Team], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('04初始化設定/新增同義詞庫'), [('teamParam') : Team], FailureHandling.STOP_ON_FAILURE)

println('已完成 新增預設模板。')

