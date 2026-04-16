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

String account = accountParam

if (WebUI.verifyElementPresent(findTestObject('主畫面/未公開媒資頁/搜尋媒資'),1,FailureHandling.OPTIONAL)) {
WebUI.click(findTestObject('主畫面/未公開媒資頁/搜尋媒資'))
}

//WebUI.setText(findTestObject('主畫面/未公開媒資頁/輸入關鍵字'), keywordStr)

'search'
//WebUI.click(findTestObject('主畫面/未公開媒資頁/搜尋'))

if (WebUI.verifyElementClickable(findTestObject('主畫面/未公開媒資頁/歸檔送出1'), FailureHandling.OPTIONAL)) {

'點送出'
WebUI.click(findTestObject('主畫面/未公開媒資頁/歸檔送出1'))

WebUI.delay(2)

WebUI.waitForElementClickable(findTestObject('主畫面/未公開媒資頁/送出歸檔頁/搜尋歸檔送出'), 2)

'(送出歸檔)點送出'
WebUI.click(findTestObject('主畫面/未公開媒資頁/送出歸檔頁/搜尋歸檔送出'))

'點檔案管理'
WebUI.click(findTestObject('Header/待審清單'))

WebUI.setText(findTestObject('檔案管理頁/媒資歸檔頁/輸入申請人'), account)

WebUI.setText(findTestObject('檔案管理頁/媒資歸檔頁/輸入關鍵字'), keywordStr)

'search'
WebUI.click(findTestObject('檔案管理頁/媒資歸檔頁/搜尋'))

WebUI.click(findTestObject('檔案管理頁/媒資歸檔頁/全部勾選'))

WebUI.click(findTestObject('檔案管理頁/媒資歸檔頁/全部退回'))

//WebUI.click(findTestObject('檔案管理頁/媒資歸檔頁/退回1'))

WebUI.setText(findTestObject('檔案管理頁/媒資歸檔頁/退回輸入審核說明'), 'noway')

WebUI.click(findTestObject('檔案管理頁/媒資歸檔頁/退回審核說明退回'))

WebUI.delay(2)
}
'openmam logo'
WebUI.click(findTestObject('Header/OPENMAM'))

println('已完成呼叫 歸檔送出退回')

