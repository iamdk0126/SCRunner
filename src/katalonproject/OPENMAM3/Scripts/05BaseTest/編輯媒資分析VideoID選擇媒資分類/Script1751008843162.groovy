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
import com.kms.katalon.core.util.KeywordUtil
import org.openqa.selenium.WebElement
import custom.WebUIExtensions

// --- 步驟一：從 VideoID 提取 Code ---
String VideoID = VideoIDParam // 這裡可以換成您的動態變數
WebUI.comment('步驟一：開始分析 VideoID: ' + VideoID)
// 先做正則比對
def matcher = (VideoID =~ /[A-Z]+/)

if (!matcher.find()) {
    WebUI.comment("VideoID [" + VideoID + "] 未找到任何大寫字母，結束測試。")
	return  // ← 直接結束 Test Case，不算失敗
}

String mediatypeChar = matcher.group(0)
WebUI.comment('提取出的 Code 為: ' + mediatypeChar)

// --- 步驟二：查閱 Map 並【組合】出最終的目標文字 ---
WebUI.comment('步驟二：正在從全域變數 TextType 中查詢並組合目標文字...')
def textTypeMap = GlobalVariable.Team1TextType
String chineseName = textTypeMap.get(mediatypeChar) // 先取出中文名，例如 "政治"
WebUI.comment('提取出的 中文名 為: ' + chineseName)
// 健壯性檢查
if (chineseName == null) {
     WebUI.comment('在全域變數 TextType 中，找不到 Code [' + mediatypeChar + '] 對應的中文名稱。')
	return  // ← 直接結束 Test Case，不算失敗
}

// 【關鍵修正】將中文名和 Code 拼接成最終的目標字串
String targetOptionText = chineseName //+ mediatypeChar // 例如 "政治" + "P" -> "政治P"
WebUI.comment('組合完畢！最終目標選項為: ' + targetOptionText)


// --- 步驟三：使用最終目標文字，操作 UI ---
WebUI.comment('步驟三：開始操作UI，選擇選項: ' + targetOptionText)

def selectTrigger = findTestObject('主畫面/編輯媒資頁/媒資分類/selector')
def selectInput = findTestObject('主畫面/編輯媒資頁/媒資分類/selector_input')
boolean optionSelected = false
int maxTries = 50

//MyWebUI.retryClick(selectTrigger)
WebUIExtensions.retryClick(selectTrigger)
WebUI.delay(1)

CustomKeywords.'custom.DropdownHelper.selectVisibleDropdownOptionScroll'(targetOptionText)

/*
String activeOptionXpath = "//div[contains(@class, 'ant-select-item-option-active')]"
TestObject activeOption = new TestObject("activeOption")
activeOption.addProperty('xpath', ConditionType.EQUALS, activeOptionXpath)

// 使用我們最終的「先移動，後檢查」邏輯
println("開始向下搜尋選項，本次使用『先移動，後檢查』邏輯...")
for (int i = 0; i < maxTries; i++) {
    WebUI.sendKeys(selectInput, Keys.chord(Keys.ARROW_DOWN))
    WebUI.delay(0.2) 

    String currentActiveTitle = WebUI.getAttribute(activeOption, 'title', FailureHandling.CONTINUE_ON_FAILURE)
    println("第 " + (i + 1) + " 次嘗試：當前高亮選項是 '" + currentActiveTitle + "'")
    
    if (currentActiveTitle != null && currentActiveTitle.equals(targetOptionText)) {
        WebUI.sendKeys(selectInput, Keys.chord(Keys.ENTER))
        optionSelected = true
        break
    }
}
*/
WebUI.delay(1)
