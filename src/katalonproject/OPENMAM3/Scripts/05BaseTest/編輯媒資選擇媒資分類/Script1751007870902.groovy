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
import com.kms.katalon.core.testobject.ConditionType as ConditionType
import org.openqa.selenium.WebElement

// --- 1. 初始化設定 ---
String targetOptionText = '財經B'
def selectTrigger = findTestObject('主畫面/編輯媒資頁/媒資分類/selector')
def selectInput = findTestObject('主畫面/編輯媒資頁/媒資分類/selector_input') // 我們對 input 發送鍵盤指令
boolean optionSelected = false
int maxTries = 50 

// --- 2. 點擊觸發器打開選單 ---
WebUI.click(selectTrigger)
WebUI.delay(1)

// --- 3. 建立一個專門用來尋找「當前高亮選項」的 TestObject ---
String activeOptionXpath = "//div[contains(@class, 'ant-select-item-option-active')]"
TestObject activeOption = new TestObject("activeOption")
activeOption.addProperty('xpath', ConditionType.EQUALS, activeOptionXpath)

// --- 4. 開始循環：按「下」方向鍵，並檢查當前高亮選項的文字 ---
println("開始追蹤高亮選項，目標： " + targetOptionText)
for (int i = 0; i < maxTries; i++) {
    // a. 獲取當前高亮選項的 title 屬性值
    //    我們使用 FailureHandling.CONTINUE_ON_FAILURE，因為一開始可能沒有 active 的選項
    String currentActiveTitle = WebUI.getAttribute(activeOption, 'title', FailureHandling.CONTINUE_ON_FAILURE)
    println("第 " + (i + 1) + " 次嘗試：當前高亮選項是 '" + currentActiveTitle + "'")

    // b. 檢查當前高亮選項的 title 是否是我們的目標
    if (currentActiveTitle != null && currentActiveTitle.equals(targetOptionText)) {
        println("目標已高亮！準備按下 Enter。")
        // 如果是，就對 input 發送 Enter 鍵，並設定成功旗標，然後跳出循環
        WebUI.sendKeys(selectInput, Keys.chord(Keys.ENTER))
        optionSelected = true
        break // 目標達成，跳出 for 循環
    }

    // c. 如果不是，就模擬按一次「下」方向鍵
    WebUI.sendKeys(selectInput, Keys.chord(Keys.ARROW_DOWN))
    WebUI.delay(0.3) // 每次按鍵後短暫延遲 300 毫秒，給予反應時間
}
/*
// --- 5. 循環結束後，驗證最終結果 ---
if (optionSelected) {
    println("腳本成功執行選擇動作。")
    // 最後的驗證步驟
    WebUI.verifyElementAttributeValue(findTestObject('主畫面/編輯媒資頁/媒資分類/selector_selected_item'), 'title', targetOptionText, 10)
    println("測試案例成功！已正確選擇並驗證選項： " + targetOptionText)
} else {
    WebUI.fail("在嘗試了 " + maxTries + " 次向下移動後，依然無法將 '" + targetOptionText + "' 設為高亮選項。")
}*/