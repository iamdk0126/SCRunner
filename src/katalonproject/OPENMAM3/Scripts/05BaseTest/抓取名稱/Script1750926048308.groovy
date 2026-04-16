import org.openqa.selenium.WebElement
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.webui.keyword.WebUI as WebUI

// --- 1. 定義要抓取的目標元素 ---

TestObject allNamesColumn = new TestObject("allNamesColumn")

// 【關鍵】使用我們剛剛建立的、更穩定的 XPath
String stableXPath = "//div[contains(@class, 'ant-table-body')]//tbody/tr/td[4]/span"
allNamesColumn.addProperty("xpath", ConditionType.EQUALS, stableXPath)


// --- 後續的程式碼完全一樣 ---

List<String> namesList = new ArrayList<>()
println("開始執行資料抓取腳本...")

try {
    WebUI.waitForElementPresent(allNamesColumn, 5)
    List<WebElement> nameElements = WebUI.findWebElements(allNamesColumn)
    
    if (nameElements.size() > 0) {
        println("在當前頁面找到了 " + nameElements.size() + " 個名稱項目。")
        for (WebElement element : nameElements) {
            String name = element.getText().trim()
            if (!name.isEmpty()) {
                namesList.add(name)
            }
        }
    } else {
        println("警告：在當前頁面上沒有找到任何符合條件的名稱項目。")
    }
} catch (Exception e) {
    println("在抓取名稱列表時發生錯誤: " + e.getMessage())
}

println("資料抓取完成。List 內容: " + namesList)

// (可選) 將結果存到全域變數
// 在 Console 印出最終的 List 內容，方便除錯
println("資料抓取完成。List 內容: " + namesList)

// 將抓取到的 List 存放到我們剛才建立的全域變數中
GlobalVariable.G_scrapedNames = namesList

println("結果已儲存到 GlobalVariable.G_scrapedNames")