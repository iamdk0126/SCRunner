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

'點Header回調清單'
WebUI.click(findTestObject('Header/回調清單'))

WebUI.waitForElementVisible(findTestObject('回調清單頁/申請回調'), 2)

' 1. 點擊 [申請回調] 按鈕 (此動作應關閉表單) '
WebUI.click(findTestObject('回調清單頁/申請回調'))

' 2. 等待最多 3 秒，看 [關閉按鈕] 是否會消失 '
WebUI.comment("點擊 [申請回調] 後，等待最多3秒，看表單是否自動關閉...")

// 如果按鈕在3秒內消失，didFormClose 會是 true
// 如果3秒後按鈕還在，didFormClose 會是 false
boolean didFormClose = WebUI.waitForElementNotVisible(findTestObject('回調清單頁/關閉回調清單'), 3, FailureHandling.OPTIONAL)

' 3. 根據表單是否成功關閉，決定下一步動作 '
// 注意這裡的判斷條件是 "if a form did NOT close" (!didFormClose)
if (!didFormClose) {
    // 如果 didFormClose 是 false，代表表單沒有關閉，[關閉按鈕] 還在畫面上
    WebUI.comment("警告: 表單未如預期自動關閉，執行手動點擊 [關閉按鈕] 進行補救。")

    // 因為元素還在，所以可以安全地點擊它
    WebUI.waitForElementClickable(findTestObject('回調清單頁/關閉回調清單'), 5)
    WebUI.click(findTestObject('回調清單頁/關閉回調清單'))
    
    // 再次驗證表單現在真的被關閉了
    WebUI.verifyElementNotVisible(findTestObject('回調清單頁/關閉回調清單'), 5)
    WebUI.comment("已成功手動關閉表單。")

} else {
    // 如果 didFormClose 是 true，代表表單已成功關閉
    WebUI.comment("成功: 表單已如預期自動關閉。")
}


println('已完成呼叫 回調請單')

