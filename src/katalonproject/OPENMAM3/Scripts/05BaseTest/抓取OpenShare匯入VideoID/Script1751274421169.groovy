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

// --- 1. 定義要抓取的目標元素 (已修改) ---
TestObject specificCell = new TestObject("specificCell")

// 使用您提供的 XPath
String targetXPath = "//div[@class='ant-modal-content'][.//div[@class='ant-modal-title' and text()='OpenShare匯入']]//tbody[@class='ant-table-tbody']/tr[2]/td[9]"
specificCell.addProperty("xpath", ConditionType.EQUALS, targetXPath)


// --- 2. 執行抓取並存放到指定變數 (已修改) ---
String specificVideoID = "" // 初始化一個空字串來存放結果
println("開始執行單一欄位資料抓取腳本...")

try {
    // 等待目標元素出現在畫面上，最多等 5 秒
    WebUI.waitForElementPresent(specificCell, 5)

    // 找到該單一元素
    WebElement targetElement = WebUI.findWebElement(specificCell)
    
    // 取得元素的文字內容並去除前後空白
    specificVideoID = targetElement.getText().trim()
    
    println("成功抓取到內容: " + specificVideoID)
    
} catch (Exception e) {
    println("在抓取指定欄位時發生錯誤: " + e.getMessage())
}

// --- 3. 將結果存到全域變數 (已修改) ---
// 假設您在 Katalon 的 Profile 中已經建立了一個名為 G_specificVideoID 的全域變數
GlobalVariable.G_specificVideoID = specificVideoID

println("資料抓取完成。結果已儲存到 GlobalVariable.G_specificVideoID: " + GlobalVariable.G_specificVideoID)
