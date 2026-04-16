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

String Team = teamParam

if (!(WebUI.waitForElementVisible(findTestObject('系統設定/使用者管理'), 2, FailureHandling.OPTIONAL))) {
	
	WebUI.callTestCase(findTestCase('05BaseTest/系統設定'), [:], FailureHandling.CONTINUE_ON_FAILURE)

}

//WebUI.click(findTestObject('系統設定/外部系統'))

if (!WebUI.waitForElementVisible(findTestObject('系統設定/OpenShare'), 2, FailureHandling.OPTIONAL)) {
	
		WebUI.click(findTestObject('系統設定/外部系統'))
	}

WebUI.click(findTestObject('系統設定/OpenShare'))

WebUI.delay(2)

'指向第2個資源組別'
//WebUI.click(findTestObject('ResourceTeam/Team1'))
CustomKeywords.'custom.TeamKeywords.clickTeam'(Team)

WebUI.click(findTestObject('系統設定/OpenShare頁/連線資訊/newconnection'))

WebUI.setText(findTestObject('系統設定/OpenShare頁/連線資訊/input__name'), 'OpenShare156')

WebUI.setText(findTestObject('系統設定/OpenShare頁/連線資訊/input__address'), '10.254.16.156')

WebUI.setText(findTestObject('系統設定/OpenShare頁/連線資訊/input_WS_wsPort'), '7150')

WebUI.setText(findTestObject('系統設定/OpenShare頁/連線資訊/input_HTTP_httpPort'), '8150')

WebUI.setText(findTestObject('系統設定/OpenShare頁/連線資訊/input__description'), 'OpenShare156')

WebUI.click(findTestObject('系統設定/OpenShare頁/連線資訊/button_OK'))

