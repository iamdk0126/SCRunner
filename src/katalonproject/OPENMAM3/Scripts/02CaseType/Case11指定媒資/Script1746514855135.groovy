import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import internal.GlobalVariable as GlobalVariable
import java.time.LocalDate as LocalDate // 導入 LocalDate
import java.time.format.DateTimeFormatter as DateTimeFormatter // 導入 DateTimeFormatter
import java.io.File as File // 導入 File
import java.io.IOException as IOException // 導入 IOException
import java.util.ArrayList as ArrayList // 確保 ArrayList 類別已導入


String account = accountParam
'隨機讀取一筆公開媒資名稱'
//def specifickeyword = WebUI.callTestCase(findTestCase('05BaseTest/選擇公開無列管關鍵字'), [:], FailureHandling.CONTINUE_ON_FAILURE)
def specifickeyword = GlobalVariable.MultiAccessAssetName

//def specifickeyword = CustomKeywords.'custom.dbHelper.getRandomAssetNameWithoutCopyright'(1,1)

// 嘗試最多 360 次 (3600 秒 = 1 小時)，每次間隔 10 秒
int maxAttempts = 360
int attempt = 0

while (attempt < maxAttempts) {
    if (specifickeyword != null) {
        WebUI.comment("✅ 已取得未公開媒資名稱: " + specifickeyword)
        break   // 有值就跳出，繼續執行下面程式
    }
    WebUI.comment("⚠️ 未取得未公開媒資名稱，第 ${attempt + 1} 次等待，10 秒後重試...")
    WebUI.delay(10)
    attempt++
}

// 如果等滿一小時都還是 null，直接結束
if (specifickeyword == null) {
    WebUI.comment("❌ 等待 1 小時後仍未取得未公開媒資名稱，結束此測試案例。")
    return
}

WebUI.waitForElementClickable(findTestObject('主畫面/公開媒資'), 30)

WebUI.click(findTestObject('主畫面/公開媒資'))

WebUI.callTestCase(findTestCase('05BaseTest/公開搜尋媒資'), [('keywordParam') : specifickeyword], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('05BaseTest/編輯列管'), [:], FailureHandling.CONTINUE_ON_FAILURE)

println('已完成 Case11 - 公開媒資 (搜尋)')
WebUI.comment('已完成 Case11 - 公開媒資 (搜尋)')

