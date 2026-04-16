import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import java.util.Arrays

// --- 設定區 ---
int timeoutMinutes = 10
long endTime = System.currentTimeMillis() + (timeoutMinutes * 60 * 1000)
boolean allTaskDone = false 

TestObject unpublicTab = findTestObject('主畫面/未公開媒資')
TestObject sizeHeader = new TestObject("SizeHeader").addProperty("xpath", ConditionType.EQUALS, "//th[@title='大小']")
// 您驗證成功的定位器：第一列大小為 0 的 span
String xpathSizeZero = "//tr[contains(@class, 'ant-table-row-level-0')][1]//span[@title='0']"
TestObject firstRowSizeZero = new TestObject("FirstRowSizeZero").addProperty("xpath", ConditionType.EQUALS, xpathSizeZero)

WebUI.comment(">>> 自動化程序開始：連續處理所有大小為 0 的媒資")

// 初始化環境
WebUI.refresh()
WebUI.waitForPageLoad(10)
WebUI.click(unpublicTab)
WebUI.delay(5)
WebUI.click(sizeHeader)
WebUI.delay(3)

while (System.currentTimeMillis() < endTime && !allTaskDone) {
    
    // 檢查第一筆是否為 0
    if (WebUI.verifyElementPresent(firstRowSizeZero, 5, FailureHandling.OPTIONAL)) {
        WebUI.comment("發現大小為 0 的媒資，開始編輯處理...")
        
        boolean hasClickedRadio = false // 用於追蹤本筆媒資是否有可選檔案
        
        // 1. 點擊編輯 (使用確定的 ID)
        WebUI.executeJavaScript("document.getElementById('edit-asset-btn-1').click();", null)
        
        TestObject editModal = new TestObject("Modal").addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class, 'ant-modal-content')]")
        if (WebUI.waitForElementVisible(editModal, 5, FailureHandling.OPTIONAL)) {
            
            // 2. 嘗試選取第一個 Radio
            String xpathRadio = "//ul[contains(@class, 'file-list')]//input[@type='radio' and not(@disabled)][1]"
            TestObject firstRadio = new TestObject("FirstRadio").addProperty("xpath", ConditionType.EQUALS, xpathRadio)
            
            if (WebUI.verifyElementPresent(firstRadio, 5, FailureHandling.OPTIONAL)) {
                WebUI.executeJavaScript("arguments[0].click();", Arrays.asList(WebUI.findWebElement(firstRadio)))
                WebUI.comment("成功選取檔案。")
                hasClickedRadio = true
            } else {
                WebUI.comment("無可選檔案，將標記為需刪除之媒資。")
            }
            
            WebUI.delay(1)

            // 3. 點擊確定儲存
            String xpathSubmit = "//div[contains(@class, 'ant-modal-footer')]//button[contains(., '確') and contains(., '定')]"
            TestObject submitBtn = new TestObject("SubmitBtn").addProperty("xpath", ConditionType.EQUALS, xpathSubmit)
            
            if (WebUI.waitForElementClickable(submitBtn, 5, FailureHandling.OPTIONAL)) {
                WebUI.click(submitBtn)
            } else {
                WebUI.executeJavaScript("document.querySelector('.ant-modal-footer .ant-btn-primary').click();", null)
            }
            
            // 4. 等待視窗關閉
            WebUI.waitForElementNotPresent(editModal, 5)
            WebUI.delay(2) // 等待列表更新

            // --- 5. 修改邏輯：若沒勾到 Radio，點擊第一個垃圾桶 ---
            if (!hasClickedRadio) {
                WebUI.comment("因無檔案可選，執行點擊垃圾桶按鈕 (trash-asset-btn-1)")
                
                // 使用 JS 點擊確保無視固定列遮擋
				
				WebUI.executeJavaScript("document.getElementById('trash-asset-btn-1').click();", null)
                
                // 如果刪除後有二次確認彈窗，請在此加入處理邏輯，例如：
                // WebUI.click(findTestObject('Object Repository/通用/彈窗確認按鈕'))
                
                WebUI.delay(2) // 刪除後的緩衝
            }
            
            WebUI.comment("本筆處理完畢，繼續檢查下一筆。")
        }
    } else {
        // 第一筆不是 0，再嘗試排序確認
        WebUI.comment("第一筆目前不是 0，嘗試重新點擊排序確認...")
        WebUI.click(sizeHeader)
        WebUI.delay(3)
        
        if (!WebUI.verifyElementPresent(firstRowSizeZero, 3, FailureHandling.OPTIONAL)) {
            WebUI.comment("確認已無大小為 0 的媒資。")
            allTaskDone = true
        }
    }
}

if (allTaskDone) {
    WebUI.comment(">>> 任務成功完成。")
} else {
    WebUI.comment(">>> 超時或異常結束。")
}