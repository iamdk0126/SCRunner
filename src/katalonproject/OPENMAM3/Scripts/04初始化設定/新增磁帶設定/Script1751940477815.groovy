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
import java.util.ArrayList as ArrayList
import custom.TimeControl as TimeControl
import custom.AuthKeywords
import custom.DropdownHelper
import custom.TimeControl
import custom.WebUIExtensions as WebUIExtensions



if (!(WebUI.waitForElementVisible(findTestObject('系統設定/使用者管理'), 2, FailureHandling.OPTIONAL))) {
	
	WebUI.callTestCase(findTestCase('05BaseTest/系統設定'), [:], FailureHandling.CONTINUE_ON_FAILURE)

}



WebUI.click(findTestObject('系統設定/磁帶設定'))



WebUI.delay(2)


    WebUI.click(findTestObject('系統設定/磁帶設定頁/新增'))

    //WebUI.setText(findTestObject('系統設定/磁帶設定頁/磁帶類型'), GlobalVariable.divapts)
	DropdownHelper.selectDropdownOptionByLabel('磁帶類型',GlobalVariable.divapts)
	
	WebUI.setText(findTestObject('系統設定/磁帶設定頁/顯示名稱'), GlobalVariable.divapts_name)
	
	WebUI.check(findTestObject('系統設定/磁帶設定頁/不限'))

    WebUI.click(findTestObject('系統設定/磁帶設定頁/儲存'))

    WebUI.delay(10)

	
	WebUI.click(findTestObject('系統設定/磁帶設定頁/新增'))
	
		//WebUI.setText(findTestObject('系統設定/磁帶設定頁/磁帶類型'), GlobalVariable.divatwplus)
		DropdownHelper.selectDropdownOptionByLabel('磁帶類型',GlobalVariable.divatwplus)
		
		WebUI.setText(findTestObject('系統設定/磁帶設定頁/顯示名稱'), GlobalVariable.divatwplus_name)
		
		//WebUI.check(findTestObject('系統設定/磁帶設定頁/不限'))
	
		WebUI.click(findTestObject('系統設定/磁帶設定頁/清除開始時間'))
	
		WebUI.sendKeys(findTestObject('系統設定/磁帶設定頁/開始時間'), "02:00" + Keys.ENTER)
	
		WebUI.delay(2)
	
		WebUI.click(findTestObject('系統設定/磁帶設定頁/清除結束時間'))
	
		WebUI.sendKeys(findTestObject('系統設定/磁帶設定頁/結束時間'), "06:00" + Keys.ENTER)
	
		WebUI.delay(2)
	
		WebUI.click(findTestObject('系統設定/磁帶設定頁/儲存'))
	
		WebUI.delay(2)

println('磁帶設定完成')

