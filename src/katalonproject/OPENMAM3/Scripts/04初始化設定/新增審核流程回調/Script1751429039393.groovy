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
import com.kms.katalon.core.testobject.ConditionType as ConditionType
import org.openqa.selenium.Keys as Keys
import java.util.ArrayList as ArrayList

WebUI.click(findTestObject('系統設定/審核流程頁/新增流程'))

WebUI.click(findTestObject('系統設定/審核流程頁/選擇媒資分類'))

WebUI.click(findTestObject('系統設定/審核流程頁/媒資分類所有媒資'))

WebUI.click(findTestObject('系統設定/審核流程頁/div_title'))

WebUI.click(findTestObject('系統設定/審核流程頁/選擇行為'))

WebUI.click(findTestObject('系統設定/審核流程頁/回調行為'))

WebUI.click(findTestObject('系統設定/審核流程頁/申請人選單'))

WebUI.click(findTestObject('系統設定/審核流程頁/所有角色'))

WebUI.click(findTestObject('系統設定/審核流程頁/增加審核層級'))

WebUI.click(findTestObject('系統設定/審核流程頁/審核順位一選單'))

CustomKeywords.'custom.DropdownHelper.selectVisibleDropdownOptionScroll'(GlobalVariable.SignOffOrder[0])

WebUI.click(findTestObject('系統設定/審核流程頁/增加審核層級'))

WebUI.click(findTestObject('系統設定/審核流程頁/審核順位二選單'))

CustomKeywords.'custom.DropdownHelper.selectVisibleDropdownOptionScroll'(GlobalVariable.SignOffOrder[1])

WebUI.click(findTestObject('系統設定/審核流程頁/建立'))

println('新增回調審核流程 Done')

