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

//WebUI.callTestCase(findTestCase('05BaseTest/填寫回調原因'), [:], FailureHandling.STOP_ON_FAILURE)

//if (WebUI.verifyElementPresent(findTestObject('主畫面/媒資資訊頁/收合'), 5, FailureHandling.OPTIONAL)) {
    // 如果物件存在，才執行以下這行
//    WebUI.click(findTestObject('主畫面/媒資資訊頁/收合'))

//    WebUI.delay(2)
//}


'點Header回調清單'
WebUIExtensions.retryClick(findTestObject('Header/回調清單'))

WebUI.waitForElementVisible(findTestObject('回調清單頁/申請回調'), 3)

TestObject btnConfirm = findTestObject('回調清單頁/申請回調')
TestObject hideButton = findTestObject('回調清單頁/關閉回調清單')

// 呼叫方式：把 A(要點擊的) 和 B(要檢查消失的) 傳進去
WebUIExtensions.clickUntilTargetHides(btnConfirm, hideButton,5 ,5)

/*
// 設定一個最大嘗試關閉表單的次數
int maxCloseAttempts = 5
int closeAttempt = 0
boolean formSuccessfullyClosed = false

// 當表單的關閉按鈕仍可見，且未達到最大嘗試次數時，持續嘗試關閉
while (WebUI.verifyElementVisible(findTestObject('回調清單頁/關閉回調清單'),  FailureHandling.OPTIONAL) && (closeAttempt < maxCloseAttempts)) {
    closeAttempt++
    WebUI.comment(('偵測到回調清單表單未關閉，正在進行第 ' + closeAttempt) + ' 次嘗試關閉...')
    println(('偵測到回調清單表單未關閉，正在進行第 ' + closeAttempt) + ' 次嘗試關閉...')

    try {
        // 1. 點擊 [申請回調] 按鈕 (第一次是觸發表單關閉，後續可能是重試觸發)
        // 注意：這裡假設每次點擊「申請回調」會嘗試關閉表單。
        // 如果「申請回調」只在表單未開啟時才有效，則需要調整邏輯。
        // 但根據您原有的程式碼，似乎點擊它就是為了觸發關閉。
        WebUIExtensions.retryClick(findTestObject('回調清單頁/申請回調'))

        // 2. 等待最多 3 秒，看 [關閉按鈕] 是否會消失
        // 這裡使用 waitForElementNotVisible 進行動態等待
        formSuccessfullyClosed = WebUI.waitForElementNotVisible(findTestObject('回調清單頁/關閉回調清單'), 3, FailureHandling.OPTIONAL)

        if (formSuccessfullyClosed) {
            WebUI.comment('成功: 表單已如預期自動關閉。')
            println('成功: 表單已如預期自動關閉。')
            break
            // 如果 3 秒後表單仍未關閉，則嘗試手動點擊關閉按鈕
            // 再次檢查元素是否存在並可點擊，因為 verifyElementPresent 可能是舊的狀態
            // 再次等待元素消失，確認手動點擊是否有效
            // 表單已關閉，跳出迴圈
            // 如果按鈕已經不存在，可能是成功關閉了，再次檢查迴圈條件
            // 假設已關閉，讓迴圈重新判斷
        } else {
            WebUI.comment('警告: 表單未自動關閉。')

            println('警告: 表單未自動關閉。')

            if (WebUI.verifyElementVisible(findTestObject('回調清單頁/關閉回調清單'), FailureHandling.OPTIONAL)) {
                 WebUIExtensions.retryClick(findTestObject('回調清單頁/關閉回調清單'))
				// 再次等待元素消失，確認是否有效
                formSuccessfullyClosed = WebUI.waitForElementNotVisible(findTestObject('回調清單頁/關閉回調清單'), 2, FailureHandling.OPTIONAL)
                if (formSuccessfullyClosed) {
                    WebUI.comment('已成功關閉表單。')
                    println('已成功關閉表單。')
                    break // 表單已關閉，跳出迴圈
                }
            } else {
                WebUI.comment('自動關閉嘗試：關閉按鈕已不存在，可能已在重試間隙消失。')
                println('自動關閉嘗試：關閉按鈕已不存在，可能已在重試間隙消失。')
				// 如果按鈕已經不存在，可能是成功關閉了，再次檢查迴圈條件
                formSuccessfullyClosed = true // 假設已關閉，讓迴圈重新判斷
            }
        }
    }
    catch (Exception e) {
        WebUI.comment('關閉表單過程中發生預期外的錯誤: ' + e.getMessage())
        println('關閉表單過程中發生預期外的錯誤: ' + e.getMessage())
		// 發生錯誤時，給一點延遲再重試，避免連續失敗
        WebUI.delay(2)
    } // 發生錯誤時，給一點延遲再重試，避免連續失敗
}

// 迴圈結束後，最後確認狀態
if (WebUI.verifyElementNotPresent(findTestObject('回調清單頁/關閉回調清單'), 2, FailureHandling.OPTIONAL)) {
    WebUI.comment('已成功確認回調清單表單已關閉。')

    println('已成功確認回調清單表單已關閉。')
} else {
    WebUI.comment(('執行了 ' + closeAttempt) + ' 次嘗試後，回調清單表單仍未關閉。請檢查應用程式狀態或測試流程。')

    println(('執行了 ' + closeAttempt) + ' 次嘗試後，回調清單表單仍未關閉。請檢查應用程式狀態或測試流程。')
}
*/
println('已完成呼叫 回調清單')

