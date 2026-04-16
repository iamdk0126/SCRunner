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

'點新增媒資'
WebUI.click(findTestObject('主畫面/新增媒資'))

WebUI.click(findTestObject('主畫面/新增媒資頁/OpenShare匯入'))

WebUI.delay(15)

'搜尋2100'
WebUI.setText(findTestObject('主畫面/新增媒資頁/OpenShare匯入頁/openshareIn_inputName'), '2100')

WebUI.delay(2)

'點搜尋'
WebUI.click(findTestObject('主畫面/新增媒資頁/OpenShare匯入頁/openshareIn_search_button'))

WebUI.delay(15)

WebUI.click(findTestObject('主畫面/新增媒資頁/OpenShare匯入頁/select_checkbox/openshareIn_select_1'))

WebUI.click(findTestObject('主畫面/新增媒資頁/OpenShare匯入頁/openshareIn_selectMediaType'))

WebUI.click(findTestObject('主畫面/新增媒資頁/OpenShare匯入頁/Page_/div_L'))

'點儲存'
WebUI.click(findTestObject('主畫面/新增媒資頁/OpenShare匯入頁/openshareIn_OK'))

WebUI.delay(2)

println('已完成呼叫 新增媒資-OpenShare匯入。')

