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

String Team = teamParam

String RS_Name = Team + 'RestoreSamba_Name'

String RS_ip = Team + 'RestoreSamba_ip'

String RS_account = Team + 'RestoreSamba_account'

String RS_pw = Team + 'RestoreSamba_pw'

String RS_folder = Team + 'RestoreSamba_folder'

String RS_description = Team + 'RestoreSamba_description'

String TeamSamba_Name = GlobalVariable.metaClass.getProperty(GlobalVariable, RS_Name)

String TeamSamba_ip = GlobalVariable.metaClass.getProperty(GlobalVariable, RS_ip)

String TeamSamba_account = GlobalVariable.metaClass.getProperty(GlobalVariable, RS_account)

String TeamSamba_pw = GlobalVariable.metaClass.getProperty(GlobalVariable, RS_pw)

String TeamSamba_folder = GlobalVariable.metaClass.getProperty(GlobalVariable, RS_folder)

String TeamSamba_description = GlobalVariable.metaClass.getProperty(GlobalVariable, RS_description)

String RF_Name = Team + 'RestoreFtp_Name'

String RF_ip = Team + 'RestoreFtp_ip'

String RF_port = Team + 'RestoreFtp_port'

String RF_account = Team + 'RestoreFtp_account'

String RF_pw = Team + 'RestoreFtp_pw'

String RF_folder = Team + 'RestoreFtp_folder'

String RF_description = Team + 'RestoreFtp_description'

String TeamFtp_Name = GlobalVariable.metaClass.getProperty(GlobalVariable, RF_Name)

String TeamFtp_ip = GlobalVariable.metaClass.getProperty(GlobalVariable, RF_ip)

String TeamFtp_port = GlobalVariable.metaClass.getProperty(GlobalVariable, RF_port)

String TeamFtp_account = GlobalVariable.metaClass.getProperty(GlobalVariable, RF_account)

String TeamFtp_pw = GlobalVariable.metaClass.getProperty(GlobalVariable, RF_pw)

String TeamFtp_folder = GlobalVariable.metaClass.getProperty(GlobalVariable, RF_folder)

String TeamFtp_description = GlobalVariable.metaClass.getProperty(GlobalVariable, RF_description)

if (!(WebUI.waitForElementVisible(findTestObject('系統設定/使用者管理'), 2, FailureHandling.OPTIONAL))) {
    WebUI.callTestCase(findTestCase('05BaseTest/系統設定'), [:], FailureHandling.CONTINUE_ON_FAILURE)
}

//WebUI.click(findTestObject('系統設定/回調送播'))
if (!(WebUI.waitForElementVisible(findTestObject('系統設定/回調設定'), 2, FailureHandling.OPTIONAL))) {
    WebUI.click(findTestObject('系統設定/回調送播'))
}

WebUI.click(findTestObject('系統設定/回調設定'))

WebUI.delay(2)

//WebUI.click(findTestObject('ResourceTeam/Team1'))
'指向第1個資源組別'
CustomKeywords.'custom.TeamKeywords.clickTeam'(Team)

WebUI.click(findTestObject('系統設定/回調位置頁/NewConnection'))

WebUI.setText(findTestObject('系統設定/回調位置頁/input__name'), TeamSamba_Name)

WebUI.click(findTestObject('系統設定/回調位置頁/selecttype_drop'))

WebUI.click(findTestObject('系統設定/回調位置頁/div_Samba'))

WebUI.setText(findTestObject('系統設定/回調位置頁/input__hostip'), TeamSamba_ip)

WebUI.setText(findTestObject('系統設定/回調位置頁/input__account'), TeamSamba_account)

WebUI.setText(findTestObject('系統設定/回調位置頁/input__password'), TeamSamba_pw)

WebUI.setText(findTestObject('系統設定/回調位置頁/input__subPath'), TeamSamba_folder)

WebUI.setText(findTestObject('系統設定/回調位置頁/input__description'), TeamSamba_description)

WebUI.click(findTestObject('系統設定/回調位置頁/NewLocation_OK'))

WebUI.delay(2)

WebUI.click(findTestObject('系統設定/回調位置頁/NewConnection'))

WebUI.setText(findTestObject('系統設定/回調位置頁/input__name'), TeamFtp_Name)

WebUI.click(findTestObject('系統設定/回調位置頁/selecttype_drop'))

WebUI.click(findTestObject('系統設定/回調位置頁/div_FTP'))

WebUI.setText(findTestObject('系統設定/回調位置頁/input__hostip'), TeamFtp_ip)

WebUI.setText(findTestObject('系統設定/回調位置頁/input__port'), TeamFtp_port)

WebUI.setText(findTestObject('系統設定/回調位置頁/input__account'), TeamFtp_account)

WebUI.setText(findTestObject('系統設定/回調位置頁/input__password'), TeamFtp_pw)

WebUI.setText(findTestObject('系統設定/回調位置頁/input__subPath'), TeamFtp_folder)

WebUI.setText(findTestObject('系統設定/回調位置頁/input__description'), TeamFtp_description)

WebUI.click(findTestObject('系統設定/回調位置頁/NewLocation_OK'))

WebUI.callTestCase(findTestCase('05BaseTest/上傳浮水印'), [:], FailureHandling.CONTINUE_ON_FAILURE)

