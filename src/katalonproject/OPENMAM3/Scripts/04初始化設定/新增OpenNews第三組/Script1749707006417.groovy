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

WebUI.click(findTestObject('Header/個人頭像'))

WebUI.click(findTestObject('Header/個人頭像下拉選單/系統設定'), FailureHandling.OPTIONAL)

//WebUI.click(findTestObject('系統設定/外部系統'))

if (!WebUI.waitForElementVisible(findTestObject('系統設定/OpenNews'), 2, FailureHandling.OPTIONAL)) {
	
		WebUI.click(findTestObject('系統設定/外部系統'))
	}

WebUI.click(findTestObject('系統設定/OpenNews'))

WebUI.delay(2)

'指向第2個資源組別'
WebUI.click(findTestObject('ResourceTeam/Team3'))

WebUI.click(findTestObject('系統設定/OpenNews頁/連線資訊/newconnection'))

WebUI.setText(findTestObject('系統設定/OpenNews頁/連線資訊/input__name'), 'iNews 223')

WebUI.click(findTestObject('系統設定/OpenNews頁/連線資訊/selecttype_drop'))

WebUI.click(findTestObject('系統設定/OpenNews頁/連線資訊/div_iNews'))

WebUI.setText(findTestObject('系統設定/OpenNews頁/連線資訊/input__host'), '10.254.18.223')

WebUI.setText(findTestObject('系統設定/OpenNews頁/連線資訊/input__port'), '138')

WebUI.setText(findTestObject('系統設定/OpenNews頁/連線資訊/input__account'), 'sqa')

WebUI.setText(findTestObject('系統設定/OpenNews頁/連線資訊/input__password'), '1')

WebUI.setText(findTestObject('系統設定/OpenNews頁/連線資訊/input__subpaths'), '/iNews')

WebUI.setText(findTestObject('系統設定/OpenNews頁/連線資訊/input__description'), 'iNews 223')

WebUI.click(findTestObject('系統設定/OpenNews頁/連線資訊/button_OK'))

