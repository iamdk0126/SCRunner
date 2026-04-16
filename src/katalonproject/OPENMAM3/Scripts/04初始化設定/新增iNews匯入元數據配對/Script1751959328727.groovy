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

String Team = teamParam

// --- 1. 定義元數據與對應選項的映射 ---
def metadataMap = GlobalVariable.inewsmetadataMap

// --- 2. 前置導航操作 (保持原樣) ---
if (!(WebUI.waitForElementVisible(findTestObject('系統設定/使用者管理'), 2, FailureHandling.OPTIONAL))) {
    WebUI.callTestCase(findTestCase('05BaseTest/系統設定'), [:], FailureHandling.CONTINUE_ON_FAILURE)
}

WebUI.click(findTestObject('系統設定/外部系統'))

WebUI.click(findTestObject('系統設定/iNews匯入'))

WebUI.delay(2 // 考慮增加此處延遲，確保頁面完全載入
    )

//WebUI.click(findTestObject('ResourceTeam/Team1'))
CustomKeywords.'custom.TeamKeywords.clickTeam'(Team)

WebUI.click(findTestObject('系統設定/iNews匯入頁/元數據配對頁/編輯元數據配對'))

// --- 3. 定義彈出視窗的 Test Object ---
TestObject popupDialog = findTestObject('Object Repository/Common/AntDesignDialog')

// --- 4. 獲取 WebDriver 實例 ---
WebDriver driver = DriverFactory.getWebDriver()

WebUI.comment('開始處理 \'配對元數據\' 彈出視窗中的表格。')

// --- 5. 等待彈出視窗可見 ---
//WebUI.waitForElementVisible(popupDialog, 10) // 確保彈出視窗完全載入
WebUI.delay(1 // 在彈出視窗顯示後，給予額外延遲，確保內容完全渲染
    )

// --- 6. 定位表格的 tbody 元素 ---
TestObject tableBody = new TestObject('Table Body in Dialog')

// *** 關鍵修正：直接獲取 popupDialog 的 XPath 屬性，最常見的方式是直接訪問 TestObject 的 XPath 定位器值 ***
// 通常，如果popupDialog在Object Repository中有定義xpath，你可以這樣獲取它
// 如果這個popupDialog是動態創建的，並且其XPath是在runtime中設置的，那麼可能需要用更通用的方式
// 但假設它是一個已經定義在Object Repository中的Test Object，它的XPath會直接存在於其選擇器列表中。
// 在Katalon中，TestObject的XPath可以直接這樣從其定義中提取
String popupDialogXpath = popupDialog.findProperty('xpath').getValue()

tableBody.addProperty('xpath', ConditionType.EQUALS, "$popupDialogXpath//tbody[@class='ant-table-tbody']")

WebUI.waitForElementVisible(tableBody, 5)

// --- 7. 獲取表格的所有行 ---
// *** 關鍵修正：同樣地，直接獲取 tableBody 的 XPath 屬性 ***
String tableBodyXpath = tableBody.findProperty('xpath').getValue()

TestObject tableRowsTestObject = new TestObject().addProperty('xpath', ConditionType.EQUALS, "$tableBodyXpath//tr")

List<WebElement> tableRowsElements = WebUI.findWebElements(tableRowsTestObject, 10)

if (tableRowsElements.isEmpty()) {
    WebUI.comment('未找到表格行。請檢查表格的定位器。')
}

// --- 8. 遍歷每一行，找到元數據和對應的下拉選單 ---
for (WebElement currentRowElement : tableRowsElements) {
    String metadataText = currentRowElement.getAttribute('data-row-key')

    if ((metadataText == null) || metadataText.isEmpty()) {
        WebUI.comment('跳過沒有 data-row-key 的行。')

        continue
    }
    
    WebUI.comment('正在處理元數據: ' + metadataText)

    if (metadataMap.containsKey(metadataText)) {
        String targetOption = metadataMap.get(metadataText)

        WebUI.comment('目標選項為: ' + targetOption)

        try {
            WebElement dropdownClickElement = currentRowElement.findElement(By.xpath('./td[2]//div[contains(@class, \'ant-select-selector\')]'))

            TestObject dropdownClickArea = WebUI.convertWebElementToTestObject(dropdownClickElement)

            WebElement selectInputElement = currentRowElement.findElement(By.xpath('./td[2]//input[contains(@class, \'ant-select-selection-search-input\')]'))

            TestObject selectInput = WebUI.convertWebElementToTestObject(selectInputElement)

            // --- 點擊觸發區域，打開下拉選單 ---
            WebUI.click(dropdownClickArea)

            WebUI.comment(('點擊了 \'' + metadataText) + '\' 的下拉選單觸發區域。')

            WebUI.delay(1.5 // 增加延遲，確保下拉選單完全展開並穩定
                )

            CustomKeywords.'custom.DropdownHelper.selectVisibleDropdownOptionScroll'(targetOption)

            // 確保輸入框獲得焦點的代碼
            /*		try {
				WebUI.click(selectInput) // 嘗試常規點擊
				WebUI.comment("顯式點擊了 '" + metadataText + "' 的下拉選單輸入框，確保焦點。")
			} catch (ElementClickInterceptedException e) {
				WebUI.comment("常規點擊被攔截，嘗試使用 JavaScript 點擊輸入框。")
				WebUI.executeJavaScript("arguments[0].click();", true, selectInputElement)
			}
			WebUI.delay(0.5) // 給予時間讓焦點穩定
*/
            boolean optionSelected = false

            int maxTries = 50 // 保持 50 次嘗試

            WebUI.comment('開始追蹤高亮選項，目標： ' + targetOption)

            /*
            for (int i = 0; i < maxTries; i++) {
                String currentActiveTitle = ''

                String activeOptionId = ''

                try {
                    // 獲取當前輸入框的 aria-activedescendant 屬性，這指向高亮選項的 ID
                    activeOptionId = selectInputElement.getAttribute('aria-activedescendant')

                    WebUI.comment('aria-activedescendant: ' + activeOptionId)

                    if ((activeOptionId != null) && !(activeOptionId.isEmpty())) {
                        // 使用這個 ID 去定位實際的高亮選項元素，並獲取其 aria-label
                        String currentActiveOptionXpath = String.format('//div[@id="%s"]', activeOptionId)

                        TestObject currentActiveOptionTO = new TestObject('currentActiveOptionTO')

                        currentActiveOptionTO.addProperty('xpath', ConditionType.EQUALS, currentActiveOptionXpath)

                        // 嘗試等待這個高亮元素出現
                        WebUI.waitForElementPresent(currentActiveOptionTO, 1, FailureHandling.CONTINUE_ON_FAILURE)

                        WebElement currentActiveOptionElement = driver.findElement(By.xpath(currentActiveOptionXpath))

                        if (currentActiveOptionElement != null) {
                            currentActiveTitle = currentActiveOptionElement.getAttribute('aria-label')

                            if ((currentActiveTitle == null) || currentActiveTitle.isEmpty()) {
                                // 如果 aria-label 為空，則嘗試獲取其文本內容
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

                // --- 檢查是否為目標選項 ---
                if ((currentActiveTitle != null) && currentActiveTitle.equals(targetOption)) {
                    WebUI.comment('目標已高亮！準備按下 Enter。')

                    WebUI.sendKeys(selectInput, Keys.chord(Keys.ENTER))

                    optionSelected = true

                    WebUI.delay(0.5 // 給予時間讓下拉選單關閉
                        )

                    break
                }
                
                // --- 發送向下箭頭鍵 ---
                WebUI.sendKeys(selectInput, Keys.chord(Keys.ARROW_DOWN))

                WebUI.delay(0.5 // 每次按箭頭鍵後增加適當延遲
                    )
            }
   */
            if (!(optionSelected)) {
                WebUI.comment(((('錯誤：未能為 \'' + metadataText) + '\' 選擇目標選項 \'') + targetOption) + '\'。請檢查選項是否存在或增加 maxTries。')

                WebUI.takeScreenshot(FailureHandling.OPTIONAL)
            }
        }
        catch (Exception e) {
            WebUI.comment((('為 \'' + metadataText) + '\' 選擇選項時發生錯誤: ') + e.getMessage())

            WebUI.takeScreenshot(FailureHandling.OPTIONAL)
        } 
    } else {
        WebUI.comment(('元數據 \'' + metadataText) + '\' 沒有在 metadataMap 中定義對應選項，跳過。')
    }
}

WebUI.comment('表格處理完成。')

WebUI.click(findTestObject('系統設定/iNews匯入頁/元數據配對頁/元數據配對儲存'))

WebUI.delay(5)

WebUI.check(findTestObject('系統設定/iNews匯入頁/iNews啟用系統'))

