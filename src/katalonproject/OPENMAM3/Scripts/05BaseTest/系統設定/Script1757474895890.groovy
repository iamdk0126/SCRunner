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
import org.openqa.selenium.Keys as Keys
import custom.WebUIExtensions

// --- 定義此特定執行要使用的帳號密碼 ---
/*
WebUIExtensions.retryClick(findTestObject('Header/OPENMAM'))

if (!(WebUI.waitForElementVisible(findTestObject('Header/個人頭像下拉選單/系統設定'), 2, FailureHandling.OPTIONAL))) {
	
'點頭像'
WebUI.waitForElementClickable(findTestObject('Header/個人頭像'), 10)

WebUIExtensions.retryClick(findTestObject('Header/個人頭像'))

'點系統設定'
WebUI.waitForElementClickable(findTestObject('Header/個人頭像下拉選單/系統設定'), 10)

WebUIExtensions.retryClick(findTestObject('Header/個人頭像下拉選單/系統設定'))

}
*/

// 點 OPENMAM
WebUIExtensions.retryClick(findTestObject('Header/OPENMAM'))

WebUIExtensions.clickWithTrigger(findTestObject('Header/個人頭像下拉選單/系統設定'),findTestObject('Header/個人頭像'))
