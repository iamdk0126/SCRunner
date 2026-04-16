package custom

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType

public class SelectHelper {
	/**
     * 選擇 Ant Design Select 下拉選單中的選項
     * @param labelText 欄位標籤 (例如: "審核順位一")
     * @param optionText 要選的選項文字 (例如: "一般編輯")
     */
    @Keyword
    def selectAntOptionByLabel(String labelText, String optionText) {
        // 找到 label 對應的 Select 區塊
        String selectXpath = "//label[contains(text(),'" + labelText + "')]/following::div[contains(@class,'ant-select')][1]"
        TestObject selectObj = new TestObject("selectObj")
        selectObj.addProperty("xpath", ConditionType.EQUALS, selectXpath)

        // 點擊 Select 展開選單
        WebUI.click(selectObj)
        WebUI.delay(1) // 給選單時間展開

        // 找到下拉選單的選項 (需確保不是隱藏的 dropdown)
        String optionXpath = "//div[contains(@class,'ant-select-dropdown') and not(contains(@style,'display: none'))]//div[@title='" + optionText + "']"
        TestObject optionObj = new TestObject("optionObj")
        optionObj.addProperty("xpath", ConditionType.EQUALS, optionXpath)

        // 點擊指定選項
        WebUI.click(optionObj)
    }
}
