import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject as TestObject
import org.openqa.selenium.WebElement as WebElement
import org.openqa.selenium.By as By
import org.openqa.selenium.WebDriver as WebDriver
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
//import com.kms.katalon.core.model.ConditionType as ConditionType
import com.kms.katalon.core.testobject.ConditionType as ConditionType
import org.openqa.selenium.Keys as Keys
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import org.openqa.selenium.interactions.Actions as Actions
import org.openqa.selenium.ElementClickInterceptedException as ElementClickInterceptedException

// --- 1. 定義元數據與對應選項的映射 ---
def texttabMap = GlobalVariable.texttabMap

// --- 2. 前置導航操作 (保持原樣) ---
WebUI.click(findTestObject('Header/個人頭像'))

WebUI.click(findTestObject('Header/個人頭像下拉選單/系統設定'), FailureHandling.OPTIONAL)

WebUI.click(findTestObject('系統設定/外部系統'))

WebUI.click(findTestObject('系統設定/OpenNews'))

WebUI.delay(2)

WebUI.click(findTestObject('ResourceTeam/Team1'))

WebUI.click(findTestObject('系統設定/OpenNews頁/文稿欄位配對頁/編輯文稿欄位配對'))

// --- 3. 定義彈出視窗的 Test Object ---
TestObject popupDialog = findTestObject('Object Repository/Common/AntDesignDialog')

// --- 4. 獲取 WebDriver 實例 ---
WebDriver driver = DriverFactory.getWebDriver()

WebUI.click(findTestObject('系統設定/OpenNews頁/文稿欄位配對頁/主碼配對1'))

WebUI.comment('開始處理 \'配對文稿欄位\' 彈出視窗中的表格。')

// --- 5. 等待彈出視窗可見 ---
// WebUI.waitForElementVisible(popupDialog, 10) // 依您的要求保持註解
WebUI.delay(1)

// --- 6. 定位表格的 tbody 元素 ---
TestObject tableBody = new TestObject('Table Body in Dialog')

String popupDialogXpath = popupDialog.findProperty('xpath').getValue()

tableBody.addProperty('xpath', ConditionType.EQUALS, "$popupDialogXpath//tbody[@class='ant-table-tbody']")

WebUI.waitForElementVisible(tableBody, 5)

// --- 7. 獲取表格所有行的基礎 XPath ---
String tableBodyXpath = tableBody.findProperty('xpath').getValue()

// --- 8. 遍歷每一行，找到元數據和對應的下拉選單 ---
int rowIndex = 1 // XPath 的索引從 1 開始

while (true) {
    // *** 關鍵修正：使用 findWebElements 並檢查列表是否為空，避免拋出異常 ***
    TestObject currentRowTestObject = new TestObject().addProperty('xpath', ConditionType.EQUALS, "$tableBodyXpath//tr[$rowIndex]")

    List<WebElement> currentRowElements = WebUI.findWebElements(currentRowTestObject, 1 // 尋找當前行，等待1秒
        )

    if (currentRowElements.isEmpty()) {
        WebUI.comment("找不到第 $rowIndex 行，已處理所有可見行。")

        break
    }
    
    WebElement currentRowElement = currentRowElements.get(0 // 獲取找到的第一個元素
        )

    String texttabText = currentRowElement.getAttribute('data-row-key')

    if ((texttabText == null) || texttabText.isEmpty()) {
        WebUI.comment('跳過沒有 data-row-key 的行。')

        rowIndex++ // 移動到下一行

        continue
    }
    
    WebUI.comment('正在處理元數據: ' + texttabText)

    if (texttabMap.containsKey(texttabText)) {
        String targetOption = texttabMap.get(texttabText)

        WebUI.comment('目標選項為: ' + targetOption)

        try {
            WebElement dropdownClickElement = currentRowElement.findElement(By.xpath('./td[3]//div[contains(@class, \'ant-select-selector\')]'))

            TestObject dropdownClickArea = WebUI.convertWebElementToTestObject(dropdownClickElement)

            WebElement selectInputElement = currentRowElement.findElement(By.xpath('./td[3]//input[contains(@class, \'ant-select-selection-search-input\')]'))

            TestObject selectInput = WebUI.convertWebElementToTestObject(selectInputElement)

            WebUI.click(dropdownClickArea)

            WebUI.comment(('點擊了 \'' + texttabText) + '\' 的下拉選單觸發區域。')

            WebUI.delay(1.5)
			
			CustomKeywords.'custom.DropdownHelper.selectVisibleDropdownOptionScroll'(targetOption)

            // 確保輸入框獲得焦點的代碼 (依您的要求保持註解)
            /*
            try {
                WebUI.click(selectInput)
                WebUI.comment("顯式點擊了 '" + texttabText + "' 的下拉選單輸入框，確保焦點。")
            } catch (ElementClickInterceptedException e) {
                WebUI.comment("常規點擊被攔截，嘗試使用 JavaScript 點擊輸入框。")
                WebUI.executeJavaScript("arguments[0].click();", true, selectInputElement)
            }
            WebUI.delay(0.5)
            */
            boolean optionSelected = false

            int maxTries = 50

            WebUI.comment('開始追蹤高亮選項，目標： ' + targetOption)
/*
            for (int i = 0; i < maxTries; i++) {
                String currentActiveTitle = ''

                String activeOptionId = ''

                try {
                    activeOptionId = selectInputElement.getAttribute('aria-activedescendant')

                    WebUI.comment('aria-activedescendant: ' + activeOptionId)

                    if ((activeOptionId != null) && !(activeOptionId.isEmpty())) {
                        String currentActiveOptionXpath = String.format('//div[@id="%s"]', activeOptionId)

                        TestObject currentActiveOptionTO = new TestObject('currentActiveOptionTO')

                        currentActiveOptionTO.addProperty('xpath', ConditionType.EQUALS, currentActiveOptionXpath)

                        WebUI.waitForElementPresent(currentActiveOptionTO, 1, FailureHandling.CONTINUE_ON_FAILURE)

                        WebElement currentActiveOptionElement = driver.findElement(By.xpath(currentActiveOptionXpath))

                        if (currentActiveOptionElement != null) {
                            currentActiveTitle = currentActiveOptionElement.getAttribute('aria-label')

                            if ((currentActiveTitle == null) || currentActiveTitle.isEmpty()) {
                                currentActiveTitle = currentActiveOptionElement.getText().trim()
                            }
                        }
                    } else {
                        WebUI.comment('aria-activedescendant 屬性為空或不存在，高亮選項未被識別。')
                    }
                }
                catch (org.openqa.selenium.NoSuchElementException noSuchElement) {
                    WebUI.comment('高亮選項元素尚未出現或已消失 (可能 aria-activedescendant 指向的元素還未渲染)。')
                } 
                catch (Exception e) {
                    WebUI.comment('獲取高亮選項資訊時發生錯誤: ' + e.getMessage())
                } 
                
                WebUI.comment(((('第 ' + (i + 1)) + ' 次嘗試：當前高亮選項是 \'') + currentActiveTitle) + '\'')

                if ((currentActiveTitle != null) && currentActiveTitle.equals(targetOption)) {
                    WebUI.comment('目標已高亮！準備按下 Enter。')

                    WebUI.sendKeys(selectInput, Keys.chord(Keys.ENTER))

                    optionSelected = true

                    WebUI.delay(0.5)

                    break
                }
                
                WebUI.sendKeys(selectInput, Keys.chord(Keys.ARROW_DOWN))

                WebUI.delay(0.5)
            }
            */
            if (!(optionSelected)) {
                WebUI.comment(((('錯誤：未能為 \'' + texttabText) + '\' 選擇目標選項 \'') + targetOption) + '\'。請檢查選項是否存在或增加 maxTries。')

                WebUI.takeScreenshot(FailureHandling.OPTIONAL)
            }
        }
        catch (Exception e) {
            WebUI.comment((('為 \'' + texttabText) + '\' 選擇選項時發生錯誤: ') + e.getMessage())

            WebUI.takeScreenshot(FailureHandling.OPTIONAL)
        } 
    } else {
        WebUI.comment(('元數據 \'' + texttabText) + '\' 沒有在 texttabMap 中定義對應選項，跳過。')
    }
    
    rowIndex++ // 處理完當前行，準備處理下一行
}

WebUI.comment('表格處理完成。')

WebUI.click(findTestObject('系統設定/OpenNews頁/文稿欄位配對頁/文稿欄位配對儲存'))

WebUI.comment('文稿欄位配對完成。')

WebUI.delay(5)

WebUI.click(findTestObject('系統設定/OpenNews頁/opennews啟用系統'))

