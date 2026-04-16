import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import java.util.ArrayList as ArrayList

String Team = teamParam
/*
String BossList = Team + "Boss"
String LeaderList = Team + "Leader"
String UserList = Team + "User"
*/

// 1. 獲取 MediaType List
List CharNameList = GlobalVariable.CharName

int count = CharNameList.size()
/*
List Team1BossList = GlobalVariable.metaClass.getProperty(GlobalVariable, BossList)

int Bosscount = Team1BossList.size()

List Team1LeaderList = GlobalVariable.metaClass.getProperty(GlobalVariable, LeaderList)

int Leadercount = Team1LeaderList.size()

List Team1UserList = GlobalVariable.metaClass.getProperty(GlobalVariable, UserList)

int Usercount = Team1UserList.size()
*/
println('Total items in CharName: ' + count)

if (!(WebUI.waitForElementVisible(findTestObject('系統設定/使用者管理'), 2, FailureHandling.OPTIONAL))) {
	
	WebUI.callTestCase(findTestCase('05BaseTest/系統設定'), [:], FailureHandling.CONTINUE_ON_FAILURE)

}

if (!WebUI.waitForElementVisible(findTestObject('系統設定/角色管理'), 2, FailureHandling.OPTIONAL)) {
	
		WebUI.click(findTestObject('系統設定/使用者設定'))
	}


//WebUI.click(findTestObject('系統設定/使用者設定'))
WebUI.click(findTestObject('系統設定/審核流程'))

WebUI.delay(2)

'指向第1個資源組別'
//WebUI.click(findTestObject('ResourceTeam/Team1'))
CustomKeywords.'custom.TeamKeywords.clickTeam'(Team)

WebUI.delay(2)

WebUI.callTestCase(findTestCase('04初始化設定/新增審核流程入庫'), [:], FailureHandling.CONTINUE_ON_FAILURE)

WebUI.callTestCase(findTestCase('04初始化設定/新增審核流程回調'), [:], FailureHandling.CONTINUE_ON_FAILURE)

WebUI.callTestCase(findTestCase('04初始化設定/新增審核流程送播'), [:], FailureHandling.CONTINUE_ON_FAILURE)
println('Loop finished.')

