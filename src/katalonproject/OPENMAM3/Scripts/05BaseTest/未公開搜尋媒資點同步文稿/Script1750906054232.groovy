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
import org.openqa.selenium.WebElement
import custom.WebUIExtensions

// 使用在 'Variables' 頁籤定義的變數
String keywordStr = keywordParam

WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資'))


WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/搜尋媒資'))

//WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/輸入關鍵字'))

WebUIExtensions.retrySetText(findTestObject('主畫面/未公開媒資頁/輸入關鍵字'),keywordStr)

'search'
WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/搜尋'))

// 步驟 1: 我們不再需要 waitForElementPresent，因為 findWebElements 會自己處理等待
// 它會根據專案設定的預設超時時間來尋找元素

// 步驟 2: 使用 findWebElements 取得所有符合條件的元素，並將它們放入一個 List (列表) 中
List<WebElement> elementList = WebUI.findWebElements(findTestObject('主畫面/未公開媒資頁/編輯媒資1'),1)

// 步驟 3: 使用 .size() 方法來取得這個 List 的大小（也就是元素的總數）
int elementCount = elementList.size()

// 印出找到的元素數量，方便除錯
println('偵錯訊息：找到的「編輯媒資1」元素數量為：' + elementCount)

// 如果元素的數量大於 0，才執行迴圈
if (elementCount > 0) {
    println('偵錯訊息：元素數量大於0，準備進入 if 迴圈。')

    WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/搜尋'))

    WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/編輯媒資1'))
    
    WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/同步文稿'))

    WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/儲存'))

} else {
    println('偵錯訊息：元素數量為0，跳過 if 迴圈。')
}

println('已完成呼叫 未公開搜尋媒資點同步文稿。')