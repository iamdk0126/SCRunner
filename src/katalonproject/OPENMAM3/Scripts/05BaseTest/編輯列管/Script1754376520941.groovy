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
import java.util.Random as Random
import custom.WebUIExtensions

List<String> CRList = GlobalVariable.Team1CR

Random randomCR = new Random()

int randomIndex = randomCR.nextInt(CRList.size())

String CRName = CRList.get(randomIndex)

//'點未公開媒資'
//WebUI.click(findTestObject('主畫面/未公開媒資'))
'點編輯列管1'
if (WebUI.waitForElementClickable(findTestObject('主畫面/未公開媒資頁/編輯列管1'), 5, FailureHandling.OPTIONAL)) {
    WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/編輯列管1'))
}
if (!WebUI.verifyElementVisible(findTestObject('主畫面/編輯列管頁/時間軸now'), FailureHandling.OPTIONAL)) {
        WebUIExtensions.retryClick(findTestObject('主畫面/編輯列管頁/取消'))
    /*
	檢查MarkIn是否可視來判斷是否有主檔, 如果沒有主檔就取消退出
	*/
    } else {
		if (WebUI.verifyElementVisible(findTestObject('主畫面/編輯列管頁/主檔資訊/主檔刪除1'), FailureHandling.OPTIONAL)) {
			WebUI.callTestCase(findTestCase('主畫面/編輯列管頁/主檔資訊/主檔刪除1'), [:], FailureHandling.OPTIONAL)
		}
		
		if (WebUI.verifyElementVisible(findTestObject('主畫面/編輯列管頁/MarkIn'), FailureHandling.OPTIONAL)) {
			WebUI.callTestCase(findTestCase('05BaseTest/MarkIn'), [:], FailureHandling.OPTIONAL)

			WebUI.callTestCase(findTestCase('05BaseTest/MarkOut'), [:], FailureHandling.OPTIONAL)
		}
		
        WebUIExtensions.retryClick(findTestObject('主畫面/編輯列管頁/新增列管'))

        if (WebUI.waitForElementClickable(findTestObject('主畫面/編輯列管頁/主檔資訊/列管名稱1下拉'), 2, FailureHandling.OPTIONAL)) {
           WebUIExtensions.retryClick(findTestObject('主畫面/編輯列管頁/主檔資訊/列管名稱1下拉'))
        }
        
        CustomKeywords.'custom.DropdownHelper.selectVisibleDropdownOptionScroll'(CRName)

        WebUIExtensions.retrySetText(findTestObject('主畫面/編輯列管頁/主檔資訊/輸入備註1'), '這是列管原因備註')

        '點編輯列管頁儲存'
        WebUIExtensions.retryClickClose(findTestObject('主畫面/編輯列管頁/儲存'))

        if (WebUI.verifyElementClickable(findTestObject('主畫面/編輯列管頁/儲存'), FailureHandling.OPTIONAL)) {
            WebUIExtensions.retryClickClose(findTestObject('主畫面/編輯列管頁/儲存'))
        }
    }


println('已完成呼叫 編輯列管。')


