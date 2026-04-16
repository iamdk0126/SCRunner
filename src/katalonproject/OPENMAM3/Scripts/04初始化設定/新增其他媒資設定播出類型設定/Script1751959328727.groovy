import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject as TestObject
import org.openqa.selenium.WebElement as WebElement
import org.openqa.selenium.By as By
import org.openqa.selenium.WebDriver as WebDriver
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
//import com.kms.katalon.core.model.ConditionType as ConditionType
import com.kms.katalon.core.testobject.ConditionType as ConditionType
import org.openqa.selenium.Keys as Keys
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import org.openqa.selenium.interactions.Actions as Actions
import org.openqa.selenium.ElementClickInterceptedException as ElementClickInterceptedException
import custom.WebUIExtensions
import custom.DropdownHelper

String Team = teamParam

// --- 1. 定義元數據與對應選項的映射 ---
def metadataMap = GlobalVariable.othermetadataMap

if (!(WebUI.waitForElementVisible(findTestObject('系統設定/使用者管理'), 2, FailureHandling.OPTIONAL))) {
	
	WebUI.callTestCase(findTestCase('05BaseTest/系統設定'), [:], FailureHandling.CONTINUE_ON_FAILURE)

}

// --- 2. 前置導航操作 (保持原樣) ---
if (!WebUI.waitForElementVisible(findTestObject('系統設定/其他媒資設定'), 2, FailureHandling.OPTIONAL)) {
	
	if (!WebUI.waitForElementVisible(findTestObject('系統設定/媒資資訊'), 2, FailureHandling.OPTIONAL)) {
		
			WebUI.click(findTestObject('系統設定/媒資設定'))
		}
	
		WebUI.click(findTestObject('系統設定/媒資資訊'))
	}

WebUI.click(findTestObject('系統設定/其他媒資設定'))

WebUI.delay(2) // 考慮增加此處延遲，確保頁面完全載入

//WebUI.click(findTestObject('ResourceTeam/Team1'))
CustomKeywords.'custom.TeamKeywords.clickTeam'(Team)

WebUI.click(findTestObject('系統設定/其他媒資設定頁/播出類型設定頁/編輯播出類型設定'))

DropdownHelper.selectDropdownOptionByLabel('選項1-節目','媒資資訊')

WebUI.sendKeys(null, Keys.chord(Keys.ESCAPE))

DropdownHelper.selectDropdownOptionByLabel('選項1-節目','公視播出用元數據')

WebUI.sendKeys(null, Keys.chord(Keys.ESCAPE))

DropdownHelper.selectDropdownOptionByLabel('選項1-節目','節目資訊')

WebUI.sendKeys(null, Keys.chord(Keys.ESCAPE))

DropdownHelper.selectDropdownOptionByLabel('選項1-節目','主控插播帶')

WebUI.sendKeys(null, Keys.chord(Keys.ESCAPE))

DropdownHelper.selectDropdownOptionByLabel('選項1-節目','主控鏡面')

WebUI.sendKeys(null, Keys.chord(Keys.ESCAPE))

DropdownHelper.selectDropdownOptionByLabel('選項2-短帶','媒資資訊')

WebUI.sendKeys(null, Keys.chord(Keys.ESCAPE))

DropdownHelper.selectDropdownOptionByLabel('選項2-短帶','公視播出用元數據')

WebUI.sendKeys(null, Keys.chord(Keys.ESCAPE))

DropdownHelper.selectDropdownOptionByLabel('選項2-短帶','短帶資訊')

WebUI.sendKeys(null, Keys.chord(Keys.ESCAPE))


WebUI.click(findTestObject('系統設定/其他媒資設定頁/播出類型設定頁/儲存'))

WebUI.delay(5)


