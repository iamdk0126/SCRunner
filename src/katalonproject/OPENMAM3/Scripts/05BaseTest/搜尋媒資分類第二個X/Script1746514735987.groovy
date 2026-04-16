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
// 使用在 'Variables' 頁籤定義的變數
String keywordStr = keywordParam 

WebUI.click(findTestObject('主畫面/未公開媒資頁/搜尋媒資'))

WebUI.click(findTestObject('主畫面/未公開媒資頁/輸入關鍵字'))

WebUI.setText(findTestObject('主畫面/未公開媒資頁/輸入關鍵字'),keywordStr)

'search'
WebUI.click(findTestObject('主畫面/未公開媒資頁/搜尋'))

'reset'
WebUI.click(findTestObject('主畫面/未公開媒資頁/重置'))

'點媒資分類'
WebUI.click(findTestObject('主畫面/未公開媒資頁/媒資分類下拉'))

'點媒資分類第二個'
WebUI.click(findTestObject('主畫面/未公開媒資頁/媒資分類選項2'))

'點search'
WebUI.click(findTestObject('主畫面/未公開媒資頁/搜尋'))

'點關閉搜尋'
WebUI.click(findTestObject('主畫面/未公開媒資頁/關閉搜尋'))

println('已完成呼叫 搜尋媒資。')