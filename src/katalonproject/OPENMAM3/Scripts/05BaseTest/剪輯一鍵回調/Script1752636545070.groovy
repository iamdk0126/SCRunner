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
import com.kms.katalon.core.util.KeywordUtil
import custom.WebUIExtensions

String specifickeyword = keywordParam

// 點擊第一筆媒資進入資訊頁
WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/Page_/item1svg'))

boolean clickResult = false
TestObject callbackBtn = findTestObject('主畫面/媒資資訊頁/公開媒資回調送播')

'檢查回調按鈕狀態'
if (WebUI.waitForElementVisible(callbackBtn, 5, FailureHandling.OPTIONAL)) {
    
    // 使用 JS 雙重檢查：有些按鈕雖然看起來亮亮的，但後台 attribute 可能是 disabled
    String jsScript = "return (arguments[0].disabled === true || window.getComputedStyle(arguments[0]).cursor === 'not-allowed');"
    Boolean isDisabled = WebUI.executeJavaScript(jsScript, Arrays.asList(WebUI.findWebElement(callbackBtn)))

    if (!isDisabled && WebUI.verifyElementClickable(callbackBtn, FailureHandling.OPTIONAL)) {
        
        println('【媒資回調】按鈕可用，準備執行點擊')
		
		WebUI.callTestCase(findTestCase('05BaseTest/MarkIn'), [:], FailureHandling.STOP_ON_FAILURE)
		
		WebUI.callTestCase(findTestCase('05BaseTest/MarkOut'), [:], FailureHandling.STOP_ON_FAILURE)
		
        WebUIExtensions.retryClick(callbackBtn)
		
		// 檢查資料庫狀態
		if (GlobalVariable.DenyRestoreSameFile) {
			if (CustomKeywords.'custom.dbHelper.isAssetInRestoringStatus'(specifickeyword)) {
				KeywordUtil.markWarning("跳過點擊：媒資 [${specifickeyword}] 仍在處理中 (status 1-8)")
				
				clickResult = false
			} else {
				WebUI.comment("媒資狀態正常，準備按下回調按鈕")
			
				'點媒資回調子選單'
				WebUIExtensions.retryClick(findTestObject('主畫面/公開媒資頁/一鍵回調'))
				
				'填寫回調原因'
				WebUI.callTestCase(findTestCase('05BaseTest/填寫回調原因'), [:], FailureHandling.OPTIONAL)

				clickResult = true
			}
		} else {
			
			WebUI.comment("媒資狀態正常，準備按下回調按鈕")
			
			'點媒資回調子選單'
			WebUIExtensions.retryClick(findTestObject('主畫面/公開媒資頁/一鍵回調'))
			
			'填寫回調原因'
			WebUI.callTestCase(findTestCase('05BaseTest/填寫回調原因'), [:], FailureHandling.OPTIONAL)

			clickResult = true
		}

    } else {
        println('【一鍵回調】按鈕處於 Disable 狀態或不可點擊，跳過。')
        clickResult = false
    }
}

// 無論成功與否，嘗試收合資訊頁以清理 UI
TestObject collapseBtn = findTestObject('主畫面/媒資資訊頁/收合')
if (WebUI.waitForElementClickable(collapseBtn, 3, FailureHandling.OPTIONAL)) {
    WebUIExtensions.retryClick(collapseBtn)
    println('【媒資回調】已收合資訊頁。')
}

println('已完成呼叫 一鍵回調')

return clickResult