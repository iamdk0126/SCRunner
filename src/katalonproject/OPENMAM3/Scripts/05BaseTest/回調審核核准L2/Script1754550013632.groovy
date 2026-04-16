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

String keywordStr = keywordParam // 使用在 'Variables' 頁籤定義的變數

String account = accountParam

'點檔案管理'
WebUI.click(findTestObject('Header/待審清單'))

WebUI.click(findTestObject('檔案管理頁/回調媒資'))

//WebUI.click(findTestObject('檔案管理頁/媒資歸檔頁/重置'))

WebUI.setText(findTestObject('檔案管理頁/媒資歸檔頁/輸入申請人'), account)

WebUI.setText(findTestObject('檔案管理頁/媒資歸檔頁/輸入關鍵字'), keywordStr)

'search'
WebUI.click(findTestObject('檔案管理頁/媒資歸檔頁/搜尋'))

if (WebUI.verifyElementClickable(findTestObject('檔案管理頁/媒資歸檔頁/全部勾選'), FailureHandling.OPTIONAL)) {
	WebUI.waitForElementClickable(findTestObject('檔案管理頁/媒資歸檔頁/全部勾選'), 2)

//if (WebUI.waitForElementClickable(findTestObject('檔案管理頁/媒資歸檔頁/全部勾選'), 30, FailureHandling.OPTIONAL)) {
    WebUI.click(findTestObject('檔案管理頁/媒資歸檔頁/全部勾選'))

    WebUI.click(findTestObject('檔案管理頁/媒資歸檔頁/全部核准'))
	
	WebUI.delay(2)

    WebUI.setText(findTestObject('檔案管理頁/媒資歸檔頁/核准輸入審核說明'), '這是輸入審核同意理由')
	
	WebUI.delay(2)

    WebUI.click(findTestObject('檔案管理頁/媒資歸檔頁/核准審核說明核准'))
}

WebUI.delay(2)

//'openmam logo'
WebUI.click(findTestObject('Header/OPENMAM'))

WebUI.delay(5)

println('已完成呼叫 回調審核核准')

