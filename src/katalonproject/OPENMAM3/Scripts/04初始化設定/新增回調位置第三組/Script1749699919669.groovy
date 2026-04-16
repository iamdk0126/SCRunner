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

WebUI.click(findTestObject('Header/個人頭像'))

WebUI.click(findTestObject('Header/個人頭像下拉選單/系統設定'), FailureHandling.OPTIONAL)

//WebUI.click(findTestObject('系統設定/回調送播'))
if (!(WebUI.waitForElementVisible(findTestObject('系統設定/回調設定'), 2, FailureHandling.OPTIONAL))) {
    WebUI.click(findTestObject('系統設定/回調送播'))
}

WebUI.click(findTestObject('系統設定/回調設定'))

WebUI.delay(2)

'指向第3個資源組別'
WebUI.click(findTestObject('ResourceTeam/Team3'))

WebUI.click(findTestObject('系統設定/回調位置頁/NewConnection'))

WebUI.setText(findTestObject('系統設定/回調位置頁/input__name'), GlobalVariable.Team3RestoreSamba_Name)

WebUI.click(findTestObject('系統設定/回調位置頁/selecttype_drop'))

WebUI.click(findTestObject('系統設定/回調位置頁/div_Samba'))

WebUI.setText(findTestObject('系統設定/回調位置頁/input__hostip'), GlobalVariable.Team3RestoreSamba_ip)

WebUI.setText(findTestObject('系統設定/回調位置頁/input__account'), GlobalVariable.Team3RestoreSamba_account)

WebUI.setText(findTestObject('系統設定/回調位置頁/input__password'), GlobalVariable.Team3RestoreSamba_pw)

WebUI.setText(findTestObject('系統設定/回調位置頁/input__subPath'), GlobalVariable.Team3RestoreSamba_folder)

WebUI.setText(findTestObject('系統設定/回調位置頁/input__description'), GlobalVariable.Team3RestoreSamba_description)

WebUI.click(findTestObject('系統設定/回調位置頁/NewLocation_OK'))

WebUI.delay(2)

WebUI.click(findTestObject('系統設定/回調位置頁/NewConnection'))

WebUI.setText(findTestObject('系統設定/回調位置頁/input__name'), GlobalVariable.Team3RestoreFtp_Name)

WebUI.click(findTestObject('系統設定/回調位置頁/selecttype_drop'))

WebUI.click(findTestObject('系統設定/回調位置頁/div_FTP'))

WebUI.setText(findTestObject('系統設定/回調位置頁/input__hostip'), GlobalVariable.Team3RestoreFtp_ip)

WebUI.setText(findTestObject('系統設定/回調位置頁/input__port'), GlobalVariable.Team3RestoreFtp_port)

WebUI.setText(findTestObject('系統設定/回調位置頁/input__account'), GlobalVariable.Team3RestoreFtp_account)

WebUI.setText(findTestObject('系統設定/回調位置頁/input__password'), GlobalVariable.Team3RestoreFtp_pw)

WebUI.setText(findTestObject('系統設定/回調位置頁/input__subPath'), GlobalVariable.Team3RestoreFtp_folder)

WebUI.setText(findTestObject('系統設定/回調位置頁/input__description'), GlobalVariable.Team3RestoreFtp_description)

WebUI.click(findTestObject('系統設定/回調位置頁/NewLocation_OK'))

