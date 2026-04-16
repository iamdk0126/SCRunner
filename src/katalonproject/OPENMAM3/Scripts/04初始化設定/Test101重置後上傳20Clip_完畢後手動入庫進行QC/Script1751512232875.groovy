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
import custom.TimeControl as TimeControl
import custom.AuthKeywords
import custom.DropdownHelper
import custom.TimeControl

//int runloop = GlobalVariable.runloop
// --- 定義此特定執行要使用的帳號密碼 ---
String specificAccount = 'test101'
String specificDomain = 'OPEN'
String node = 'OpenMAM_ip'
String Title = "看公視說英語"

String VideoID = "0000082"

String progtype = "節目"

int iterationCount = 20

// 請替換為 user_abc 的實際加密密碼
String specificEncryptedPassword = GlobalVariable.TestPassword

String url = "https://$GlobalVariable.OpenMAM_ip:$GlobalVariable.OpenMAM_port/"

// --- 呼叫登入測試案例 ---
println('準備呼叫 TC_Login，使用帳號：' + specificAccount)

WebUI.openBrowser('')

//for (def index : (0..runloop - 1)) {
//  println('Loop iteration (1-indexed): ' + (index + 1))
//  WebUI.comment(('現在是第 ' + (index + 1)) + ' 次迴圈執行')
WebUI.callTestCase(findTestCase('05BaseTest/登入'), [('accountParam') : specificAccount, ('passwordParam') : specificEncryptedPassword,('domainParam') : specificDomain,('nodeParam') : node], FailureHandling.STOP_ON_FAILURE)
				
WebUI.callTestCase(findTestCase('05BaseTest/新增媒資上傳20'), [:], FailureHandling.STOP_ON_FAILURE)
//WebUI.callTestCase(findTestCase('05BaseTest/新增媒資看公視'), [('titleParam') : Title, ('videoParam') : VideoID,('progParam') : progtype, ('indexParam') : iterationCount], FailureHandling.STOP_ON_FAILURE)


