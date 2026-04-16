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
import java.util.Arrays as Arrays
import custom.WebUIExtensions


Boolean editstatus = false

//'點未公開媒資'
//WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資'))

if (WebUI.waitForElementClickable(findTestObject('主畫面/未公開媒資頁/編輯媒資1'), 5, FailureHandling.OPTIONAL)) {
	WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/編輯媒資1'))
	editstatus = true
	
	WebUI.delay(120)
	
	WebUIExtensions.retryClickClose(findTestObject('主畫面/編輯媒資頁/儲存'))

	
}

return editstatus
println('已完成呼叫 編輯媒資。')

