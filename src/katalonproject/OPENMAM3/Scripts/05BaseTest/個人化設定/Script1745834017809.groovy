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
import custom.WebUIExtensions as WebUIExtensions

// --- 定義此特定執行要使用的帳號密碼 ---
'點頭像\r\n'
WebUI.waitForElementClickable(findTestObject('Header/個人頭像'), 30)

WebUIExtensions.retryClick(findTestObject('Header/個人頭像'))

WebUI.waitForElementClickable(findTestObject('Header/個人頭像下拉選單/個人資訊'), 30)

'點個人資訊'
WebUIExtensions.retryClick(findTestObject('Header/個人頭像下拉選單/個人資訊'))

WebUI.waitForElementClickable(findTestObject('Header/個人頭像下拉選單/個人資訊設定/個人化設定'), 30)

'點個人化設定'
WebUIExtensions.retryClick(findTestObject('Header/個人頭像下拉選單/個人資訊設定/個人化設定'))

WebUI.waitForElementClickable(findTestObject('Header/個人頭像下拉選單/個人資訊設定/外觀主題'), 30)

'點外觀主題'
WebUIExtensions.retryClick(findTestObject('Header/個人頭像下拉選單/個人資訊設定/外觀主題'))

WebUI.waitForElementClickable(findTestObject('Header/個人頭像下拉選單/個人資訊設定/外觀主題'), 30)

'點外觀主題'
WebUIExtensions.retryClick(findTestObject('Header/個人頭像下拉選單/個人資訊設定/外觀主題'))

WebUI.waitForElementClickable(findTestObject('Header/個人頭像下拉選單/個人資訊設定/列表欄位編輯'), 30)

'點列表編輯'
WebUIExtensions.retryClick(findTestObject('Header/個人頭像下拉選單/個人資訊設定/列表欄位編輯'))

WebUI.waitForElementClickable(findTestObject('Header/個人頭像下拉選單/個人資訊設定/編輯列表欄位/可視2'), 30)

'點媒資分類off'
WebUIExtensions.retryClick(findTestObject('Header/個人頭像下拉選單/個人資訊設定/編輯列表欄位/可視2'))

WebUI.waitForElementClickable(findTestObject('Header/個人頭像下拉選單/個人資訊設定/編輯列表欄位/名稱展開段落checkbox'), 30)

'點名稱展開段落V'
WebUIExtensions.retryClick(findTestObject('Header/個人頭像下拉選單/個人資訊設定/編輯列表欄位/名稱展開段落checkbox'))

WebUI.delay(2)

WebUI.waitForElementClickable(findTestObject('Header/個人頭像下拉選單/個人資訊設定/編輯列表欄位/儲存'), 30)

'點儲存'
WebUIExtensions.retryClick(findTestObject('Header/個人頭像下拉選單/個人資訊設定/編輯列表欄位/儲存'))

WebUI.waitForElementClickable(findTestObject('Header/OPENMAM'), 30)

'點Openmam Logo'
WebUIExtensions.retryClick(findTestObject('Header/OPENMAM'))

WebUI.waitForElementClickable(findTestObject('主畫面/未公開媒資頁/名稱展開段落開關'), 30)

'點名稱展開段落'
WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/名稱展開段落開關'))

WebUI.waitForElementClickable(findTestObject('主畫面/未公開媒資頁/名稱展開段落開關'), 30)

'點名稱關閉段落'
WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/名稱展開段落開關'))

WebUI.waitForElementClickable(findTestObject('Header/個人頭像'), 30)

'點頭像\r\n'
WebUIExtensions.retryClick(findTestObject('Header/個人頭像'))

WebUI.waitForElementClickable(findTestObject('Header/個人頭像下拉選單/個人資訊'), 30)

'點個人資訊\r\n'
WebUIExtensions.retryClick(findTestObject('Header/個人頭像下拉選單/個人資訊'))

WebUI.waitForElementClickable(findTestObject('Header/個人頭像下拉選單/個人資訊設定/個人化設定'), 30)

'點個人化設定'
WebUIExtensions.retryClick(findTestObject('Header/個人頭像下拉選單/個人資訊設定/個人化設定'))

WebUI.waitForElementClickable(findTestObject('Header/個人頭像下拉選單/個人資訊設定/還原預設'), 30)

'點還原預設'
WebUIExtensions.retryClick(findTestObject('Header/個人頭像下拉選單/個人資訊設定/還原預設'))

WebUI.waitForElementClickable(findTestObject('Header/個人頭像下拉選單/個人資訊設定/還原預設頁/還原'), 30)

'點還原'
WebUIExtensions.retryClick(findTestObject('Header/個人頭像下拉選單/個人資訊設定/還原預設頁/還原'))

WebUI.waitForElementClickable(findTestObject('Header/OPENMAM'), 30)

'點頭像'
WebUIExtensions.retryClick(findTestObject('Header/OPENMAM'))

println('已完成呼叫 個人化設定。')


