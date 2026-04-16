package custom

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import org.openqa.selenium.Keys
import com.kms.katalon.core.model.FailureHandling

class SelectByArrowDown {

    /**
     * 用方向鍵滾動 AntD Select，選中目標選項
     * @param labelText  欄位 label，例如 "審核順位四"
     * @param optionText 欲選擇的選項文字，例如 "一般編輯"
     * @param maxTries   最大嘗試次數，預設 50
     */
    @Keyword
    def selectOption(String labelText, String optionText, int maxTries = 50) {

        // --- 定位 Select Trigger ---
        TestObject selectTrigger = new TestObject("selectTrigger")
        String triggerXpath = "//span[normalize-space(text())='" + labelText + "']/ancestor::div[contains(@class,'ant-row')]//div[contains(@class,'ant-select-selector')]"
        selectTrigger.addProperty("xpath", ConditionType.EQUALS, triggerXpath)

        // --- 定位 Select Input ---
        TestObject selectInput = new TestObject("selectInput")
        String inputXpath = "//span[normalize-space(text())='" + labelText + "']/ancestor::div[contains(@class,'ant-row')]//input[contains(@class,'ant-select-selection-search-input')]"
        selectInput.addProperty("xpath", ConditionType.EQUALS, inputXpath)

        // --- 定位目前 active option ---
        TestObject activeOption = new TestObject("activeOption")
        String activeXpath = "//div[contains(@class,'ant-select-dropdown') and not(contains(@style,'display: none'))]//div[contains(@class,'ant-select-item-option-active')]"
        activeOption.addProperty("xpath", ConditionType.EQUALS, activeXpath)

        // --- 點擊 Trigger 展開選單 ---
        WebUI.click(selectTrigger)
        WebUI.delay(0.5)

        boolean optionSelected = false

        for (int i = 0; i < maxTries; i++) {
            // 取得目前高亮選項
            String currentActive = WebUI.getAttribute(activeOption, "title", FailureHandling.CONTINUE_ON_FAILURE)
            println("第 ${i+1} 次，高亮選項: ${currentActive}")

            if (currentActive != null && currentActive.equals(optionText)) {
                // 選中目標
                WebUI.sendKeys(selectInput, Keys.chord(Keys.ENTER))
                optionSelected = true
                println("選中目標選項: " + optionText)
                break
            }

            // 按下方向鍵滾動
            WebUI.sendKeys(selectInput, Keys.chord(Keys.ARROW_DOWN))
            WebUI.delay(0.2)
        }

        if (!optionSelected) {
            println("未找到目標選項: " + optionText)
        }
    }
}
