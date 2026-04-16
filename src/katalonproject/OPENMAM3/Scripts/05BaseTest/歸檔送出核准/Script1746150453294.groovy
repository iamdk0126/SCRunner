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
//import custom.WebUIExtensions as MyWebUI
import custom.WebUIExtensions

// 使用在 'Variables' 頁籤定義的變數
String keywordStr = keywordParam

String account = accountParam

// --- 修正：使用 .toLong() 提升 IDE 相容性 ---
long endTime = (endTimeParam != null) ? endTimeParam.toLong() : 0

WebUI.comment("申請人:" + account + "  ,關鍵字:"+ keywordStr)

    '點檔案管理'
    WebUIExtensions.retryClick(findTestObject('Header/待審清單'))

    WebUIExtensions.retrySetText(findTestObject('檔案管理頁/媒資歸檔頁/輸入申請人'), account)

    WebUIExtensions.retrySetText(findTestObject('檔案管理頁/媒資歸檔頁/輸入關鍵字'), keywordStr)

   // 無限迴圈：直到「全部勾選」可點擊才跳出
while (true) {
    // --- 修正：改用 .toLong() 避免 BigDecimal 導致的 mod 錯誤 ---
    long remaining = endTime - System.currentTimeMillis()
    long totalSec = (remaining / 1000).toLong()
    long min = (totalSec / 60).toLong()
    long seconds = (totalSec % 60).toLong()
    
    WebUI.comment("【核准等待】搜尋關鍵字中... 剩餘時間：${String.format("%02d:%02d", min, seconds)}")

    if (remaining < 30000) { // 剩不到 30 秒
        println("【警告】時間即將用盡，跳出核准等待迴圈。")
        return null // 回傳 null 讓主程式知道 L1/L2 沒過 
    }
    
    WebUIExtensions.retryClick(findTestObject('檔案管理頁/媒資歸檔頁/搜尋'))
    WebUI.comment("已點擊搜尋，等待結果...")
    WebUI.delay(5)
    
    // 檢查「全部勾選」是否可點擊 (給 5 秒等待時間)
    if (WebUI.verifyElementClickable(findTestObject('檔案管理頁/媒資歸檔頁/核准1'),  FailureHandling.OPTIONAL)) {
        WebUI.comment("找到『核准』按鈕，可以進入核准流程。")
        //WebUI.comment("找到『全部勾選』按鈕，可以進入核准流程。")
        break
    }

    // 沒找到 → 等 10 秒再繼續
    WebUI.comment("目前沒有資料， 10 秒後再次搜尋。")
    WebUI.delay(10)
}

// 找到可以點擊後 → 執行核准流程
if (WebUI.verifyElementClickable(findTestObject('檔案管理頁/媒資歸檔頁/核准1'),  FailureHandling.OPTIONAL)) {
    WebUIExtensions.retryClick(findTestObject('檔案管理頁/媒資歸檔頁/核准1'))
    WebUIExtensions.retrySetText(findTestObject('檔案管理頁/媒資歸檔頁/核准輸入審核說明'), '這是輸入審核核准說明')
    WebUIExtensions.retryClickClose(findTestObject('檔案管理頁/媒資歸檔頁/核准審核說明核准'))
	WebUI.delay(2)
}

'openmam logo'
WebUIExtensions.retryClick(findTestObject('Header/OPENMAM'))
WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資'))

if (WebUI.verifyElementPresent(findTestObject('主畫面/未公開媒資頁/搜尋媒資'), 1, FailureHandling.OPTIONAL)) {
    WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/搜尋媒資'))
}

WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/重置'))
WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/搜尋'))


println('已完成呼叫 歸檔送出核准')
WebUI.comment('已完成呼叫 歸檔送出核准')

return keywordStr