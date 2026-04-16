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

WebUI.click(findTestObject('主畫面/新增媒資頁/建立媒資'))

'點模板'
WebUI.click(findTestObject('主畫面/新增媒資頁/模板1'))

WebUI.delay(2)

'點建立'
WebUI.click(findTestObject('主畫面/新增媒資頁/建立'))

WebUI.delay(2)

WebUI.setText(findTestObject('主畫面/編輯媒資頁/名稱_請輸入內容'), '名稱必填V230')

WebUI.uploadFile(findTestObject('主畫面/新增媒資頁/上傳影片'), 'D:\\20Clips\\C0002.MXF')

WebUI.waitForElementNotVisible(findTestObject('主畫面/編輯媒資頁/上傳中取消'), 2)

'點儲存'
WebUI.click(findTestObject('主畫面/編輯媒資頁/儲存'))

WebUI.delay(2)

println('已完成呼叫 新增媒資。')
WebUI.comment('已完成呼叫 新增媒資。')

