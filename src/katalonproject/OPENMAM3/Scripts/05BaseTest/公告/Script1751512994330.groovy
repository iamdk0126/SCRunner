import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import internal.GlobalVariable as GlobalVariable
import custom.WebUIExtensions as WebUIExtensions

WebUI.delay(GlobalVariable.AnnounceDelayTime)

TestObject rightBtn = findTestObject('主畫面/公告頁/right')

TestObject closeBtn = findTestObject('主畫面/公告頁/關閉公告X')

if (WebUI.waitForElementVisible(rightBtn, 5, FailureHandling.OPTIONAL)) {

	if (WebUI.verifyElementClickable(rightBtn, FailureHandling.OPTIONAL)) {
		
		int presscount = 2
		// 持續點擊 right，直到 disabled 屬性存在
		while (presscount > 0) {
			// 使用 JS 判斷 cursor 是否為 not-allowed
			Boolean isDisabled = WebUI.executeJavaScript('return window.getComputedStyle(arguments[0]).cursor === \'not-allowed\';',
				Arrays.asList(WebUI.findWebElement(rightBtn)))
	
			if (!(isDisabled)) {
				WebUIExtensions.retryClick(rightBtn)
	
				WebUI.delay(2)
				presscount = presscount - 1
	
				println('點擊一次 right')
			} else {
				println('right 按鈕不可點，停止點擊')
	
				break
			}
		}
	}
	
	WebUI.delay(GlobalVariable.AnnounceDelayTime)
	WebUIExtensions.retryClick(closeBtn)
	// 檢查關閉公告 X
	//TestObject closeBtn = findTestObject('主畫面/公告頁/關閉公告X')
	if (WebUI.verifyElementClickable(closeBtn, FailureHandling.OPTIONAL)) {
		WebUIExtensions.retryClick(closeBtn)
	
		println('已關閉公告。')
	} else {
		println('沒有找到關閉公告 X，跳過。')
	}
}

