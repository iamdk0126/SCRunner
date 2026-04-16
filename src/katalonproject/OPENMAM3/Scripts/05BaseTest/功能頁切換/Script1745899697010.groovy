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
'點頭像'
WebUI.waitForElementClickable(findTestObject('Header/個人頭像'), 30)

WebUIExtensions.retryClick(findTestObject('Header/個人頭像'))

'點系統設定'
WebUI.waitForElementClickable(findTestObject('Header/個人頭像下拉選單/系統設定'), 30)

WebUIExtensions.retryClick(findTestObject('Header/個人頭像下拉選單/系統設定'))

'點檔案管理'
WebUI.waitForElementClickable(findTestObject('Header/待審清單'), 30)

WebUIExtensions.retryClick(findTestObject('Header/待審清單'))

'點回調媒資\r\n'
WebUI.waitForElementClickable(findTestObject('檔案管理頁/回調媒資'), 30)

WebUIExtensions.retryClick(findTestObject('檔案管理頁/回調媒資'))

'點媒資歸檔'
WebUI.waitForElementClickable(findTestObject('檔案管理頁/媒資歸檔'), 30)

WebUIExtensions.retryClick(findTestObject('檔案管理頁/媒資歸檔'))

'點送播媒資'
WebUI.waitForElementClickable(findTestObject('檔案管理頁/送播媒資'), 30)

WebUIExtensions.retryClick(findTestObject('檔案管理頁/送播媒資'))

'點任務列表'
WebUI.waitForElementClickable(findTestObject('Header/任務列表'), 30)

WebUIExtensions.retryClick(findTestObject('Header/任務列表'))

'點媒資歸檔'
WebUI.waitForElementClickable(findTestObject('任務列表頁/媒資歸檔'), 30)

WebUIExtensions.retryClick(findTestObject('任務列表頁/媒資歸檔'))

'點媒資歸檔'
WebUI.waitForElementClickable(findTestObject('任務列表頁/回調媒資'), 30)

WebUIExtensions.retryClick(findTestObject('任務列表頁/回調媒資'))

'點媒資歸檔'
WebUI.waitForElementClickable(findTestObject('任務列表頁/送播媒資'), 30)

WebUIExtensions.retryClick(findTestObject('任務列表頁/送播媒資'))

'點媒資歸檔'
WebUI.waitForElementClickable(findTestObject('任務列表頁/匯出媒資'), 30)

WebUIExtensions.retryClick(findTestObject('任務列表頁/匯出媒資'))

'點媒資歸檔'
WebUI.waitForElementClickable(findTestObject('任務列表頁/iNews匯入'), 30)

WebUIExtensions.retryClick(findTestObject('任務列表頁/iNews匯入'))

'點媒資歸檔'
WebUI.waitForElementClickable(findTestObject('任務列表頁/錄製檔案'), 30)

WebUIExtensions.retryClick(findTestObject('任務列表頁/錄製檔案'))

'點媒資歸檔'
WebUI.waitForElementClickable(findTestObject('任務列表頁/EDL匯出'), 30)

WebUIExtensions.retryClick(findTestObject('任務列表頁/EDL匯出'))

'點影片剪輯'
WebUI.waitForElementClickable(findTestObject('Header/影片剪輯'), 30)

WebUIExtensions.retryClick(findTestObject('Header/影片剪輯'))

'點OpenMAM回主頁'
WebUI.waitForElementClickable(findTestObject('Header/OPENMAM'), 30)

WebUIExtensions.retryClick(findTestObject('Header/OPENMAM'))


'點垃圾桶'
WebUI.waitForElementClickable(findTestObject('主畫面/垃圾桶'), 30)

WebUIExtensions.retryClick(findTestObject('主畫面/垃圾桶'))

'點公開媒資'
WebUI.waitForElementClickable(findTestObject('主畫面/公開媒資'), 30)

WebUIExtensions.retryClick(findTestObject('主畫面/公開媒資'))

'點未公開媒資'
WebUI.waitForElementClickable(findTestObject('主畫面/未公開媒資'), 30)

WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資'))

println('已完成呼叫 功能頁切換。')

