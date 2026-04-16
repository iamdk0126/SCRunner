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
import custom.WebUIExtensions
import custom.DropdownHelper

String Team = teamParam

WebUI.click(findTestObject('Header/個人頭像'))

WebUI.click(findTestObject('Header/個人頭像下拉選單/系統設定'), FailureHandling.OPTIONAL)

//WebUI.click(findTestObject('系統設定/外部系統'))
if (!(WebUI.waitForElementVisible(findTestObject('系統設定/iNews匯入'), 2, FailureHandling.OPTIONAL))) {
    WebUI.click(findTestObject('系統設定/外部系統'))
}

WebUI.click(findTestObject('系統設定/iNews匯入'))

WebUI.delay(2)

//WebUI.click(findTestObject('ResourceTeam/Team1'))
'指向第2個資源組別'
CustomKeywords.'custom.TeamKeywords.clickTeam'(Team)

if (GlobalVariable.iNews223) { 
	//iNews (FTP)
	WebUI.click(findTestObject('系統設定/iNews匯入頁/連線資訊/newconnection'))
	
	WebUIExtensions.setTextByLabel('名稱', 'Test iNews')
	
	DropdownHelper.selectDropdownOptionByLabel('類別','iNews (FTP)')
	
	WebUIExtensions.setTextByLabel('地址', '10.45.34.1')
	
	WebUIExtensions.setTextByLabel('子路徑', '/inews/xml')
	
	WebUIExtensions.setTextByLabel('端口', '445')
	
	WebUIExtensions.setTextByLabel('帳號', 'opencapture')
	
	WebUIExtensions.setTextByLabel('密碼', 'open2025')
	
	WebUIExtensions.setTextByLabel('描述', 'Test')
	
	WebUI.click(findTestObject('系統設定/iNews匯入頁/連線資訊/button_OK'))
	
	//常規 Samba
	
	WebUI.click(findTestObject('系統設定/iNews匯入頁/連線資訊/newconnection'))
	
	WebUIExtensions.setTextByLabel('名稱', 'Test NAS1')
	
	DropdownHelper.selectDropdownOptionByLabel('類別','常規 Samba')
	
	WebUIExtensions.setTextByLabel('地址', '10.45.34.1')
	
	WebUIExtensions.setTextByLabel('子路徑', '/inews/sot')
	
	WebUIExtensions.setTextByLabel('端口', '445')
	
	WebUIExtensions.setTextByLabel('帳號', 'opencapture')
	
	WebUIExtensions.setTextByLabel('密碼', 'open2025')
	
	WebUIExtensions.setTextByLabel('描述', 'Test')
	
	WebUI.click(findTestObject('系統設定/iNews匯入頁/連線資訊/button_OK'))
	
	}
	
	else {

		//iNews (FTP)
		WebUI.click(findTestObject('系統設定/iNews匯入頁/連線資訊/newconnection'))
		
		WebUIExtensions.setTextByLabel('名稱', 'Test iNews')
		
		DropdownHelper.selectDropdownOptionByLabel('類別','iNews (FTP)')
		
		WebUIExtensions.setTextByLabel('地址', '10.254.16.129')
		
		WebUIExtensions.setTextByLabel('子路徑', '/share/winnie/xml')
		
		WebUIExtensions.setTextByLabel('端口', '445')
		
		WebUIExtensions.setTextByLabel('帳號', 'openmam')
		
		WebUIExtensions.setTextByLabel('密碼', 'tvbs2022')
		
		WebUIExtensions.setTextByLabel('描述', 'Test')
		
		WebUI.click(findTestObject('系統設定/iNews匯入頁/連線資訊/button_OK'))
		
		//常規 Samba
		
		WebUI.click(findTestObject('系統設定/iNews匯入頁/連線資訊/newconnection'))
		
		WebUIExtensions.setTextByLabel('名稱', 'Test NAS1')
		
		DropdownHelper.selectDropdownOptionByLabel('類別','常規 Samba')
		
		WebUIExtensions.setTextByLabel('地址', '10.254.16.129')
		
		WebUIExtensions.setTextByLabel('子路徑', '/share/winnie/sot')
		
		WebUIExtensions.setTextByLabel('端口', '445')
		
		WebUIExtensions.setTextByLabel('帳號', 'openmam')
		
		WebUIExtensions.setTextByLabel('密碼', 'tvbs2022')
		
		WebUIExtensions.setTextByLabel('描述', 'Test')
		
		WebUI.click(findTestObject('系統設定/iNews匯入頁/連線資訊/button_OK'))
		
}
