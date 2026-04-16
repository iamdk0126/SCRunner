import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import java.util.Arrays

// --- 設定區 ---
boolean allTaskDone = false 

TestObject unpublicTab = findTestObject('主畫面/未公開媒資')
TestObject sizeHeader = new TestObject("SizeHeader").addProperty("xpath", ConditionType.EQUALS, "//th[@title='主檔大小']")

// 定位器：尋找大小為 "0 KB" 且「編輯按鈕」可用的列
String xpathValidRow = "//tr[.//span[@title='0 KB'] and .//button[contains(@id, 'edit-asset-btn') and not(@disabled)]]"
TestObject validRowObj = new TestObject("FirstAvailableRow").addProperty("xpath", ConditionType.EQUALS, xpathValidRow)

WebUI.comment(">>> 自動化程序開始：只要有 .mxf 就處理，否則刪除")

// 初始化環境
WebUI.refresh()
WebUI.waitForPageLoad(30)
WebUI.click(unpublicTab)
WebUI.delay(5)
WebUI.click(sizeHeader)
WebUI.delay(3)

while (!allTaskDone) {
    
    if (WebUI.waitForElementPresent(validRowObj, 10, FailureHandling.OPTIONAL)) {
        
        // 1. 抓取這筆媒資的「名稱」(td[4])，作為後續刪除的精確憑證
        String xpathNameInRow = "(${xpathValidRow})[1]//td[4]//span"
        TestObject assetNameObj = new TestObject("AssetName").addProperty("xpath", ConditionType.EQUALS, xpathNameInRow)
        String currentAssetName = WebUI.getAttribute(assetNameObj, "title")
        
        if (currentAssetName == null || currentAssetName == "") {
            currentAssetName = WebUI.getText(assetNameObj)
        }
        
        WebUI.comment("正在處理媒資：[${currentAssetName}]")
        
        // 定位編輯按鈕並點擊開啟 Modal
        TestObject editBtn = new TestObject("EditBtn").addProperty("xpath", ConditionType.EQUALS, "(${xpathValidRow})[1]//button[contains(@id, 'edit-asset-btn')]")
        WebUI.executeJavaScript("arguments[0].click();", Arrays.asList(WebUI.findWebElement(editBtn)))
        
        boolean hasClickedRadio = false
        TestObject editModal = new TestObject("Modal").addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class, 'ant-modal-content')]")
        
        if (WebUI.waitForElementVisible(editModal, 15, FailureHandling.OPTIONAL)) {
            
            // --- A. 清除「檔案上傳失敗」及其氣泡確認框 ---
            String xpathUploadFailX = "//li[.//span[contains(text(), '檔案上傳失敗')]]//button[contains(@class, 'file-delete-btn')]"
            TestObject uploadFailXObj = new TestObject("UploadFailX").addProperty("xpath", ConditionType.EQUALS, xpathUploadFailX)
            String xpathConfirmDel = "//div[contains(@class, 'ant-popover')]//button[contains(@class, 'ant-btn-dangerous')]"
            TestObject confirmDelBtnObj = new TestObject("ConfirmDel").addProperty("xpath", ConditionType.EQUALS, xpathConfirmDel)

            while (WebUI.verifyElementPresent(uploadFailXObj, 2, FailureHandling.OPTIONAL)) {
                WebUI.executeJavaScript("arguments[0].click();", Arrays.asList(WebUI.findWebElement(uploadFailXObj)))
                if (WebUI.waitForElementVisible(confirmDelBtnObj, 3, FailureHandling.OPTIONAL)) {
                    WebUI.executeJavaScript("arguments[0].click();", Arrays.asList(WebUI.findWebElement(confirmDelBtnObj)))
                    WebUI.delay(1)
                }
            }

            // --- B. 選取任何可用的 .mxf Radio ---
            // 邏輯：直接找 value 包含 .mxf 且非 disabled 的 radio
            String xpathMxfRadio = "//li[.//input[contains(@value, '.mxf')]]//input[@type='radio' and not(@disabled)]"
            TestObject mxfRadio = new TestObject("MxfRadio").addProperty("xpath", ConditionType.EQUALS, xpathMxfRadio)
            
            if (WebUI.waitForElementPresent(mxfRadio, 5, FailureHandling.OPTIONAL)) {
                WebUI.executeJavaScript("arguments[0].click();", Arrays.asList(WebUI.findWebElement(mxfRadio)))
                WebUI.comment("成功選取 .mxf 檔案。")
                hasClickedRadio = true
            } else {
                WebUI.comment("視窗內未發現可點擊之 .mxf Radio。")
            }

            // --- C. 點擊「儲存/確定」離開視窗 (確保按鈕非 disabled) ---
            String xpathSubmit = "//div[contains(@class, 'ant-modal-footer')]//button[(contains(., '儲') or contains(., '確')) and not(@disabled)]"
            TestObject submitBtn = new TestObject("SubmitBtn").addProperty("xpath", ConditionType.EQUALS, xpathSubmit)
            
            if (WebUI.waitForElementClickable(submitBtn, 10, FailureHandling.OPTIONAL)) {
                WebUI.click(submitBtn)
            } else {
                // 強制點擊 footer 內第一個可用的 Primary 按鈕
                WebUI.executeJavaScript("document.querySelector('.ant-modal-footer button.ant-btn-primary:not([disabled])').click();", null)
            }
            
            WebUI.waitForElementNotPresent(editModal, 15)
            WebUI.delay(2) 

            // --- D. 若視窗內沒按到任何 Radio，回主列表刪除對應名稱的媒資 ---
            if (!hasClickedRadio) {
                WebUI.comment("因無有效檔案可選，刪除名稱為 [${currentAssetName}] 的媒資")
                
                String xpathSpecificTrash = "//tr[.//td[4]//span[@title='${currentAssetName}']]//button[contains(@id, 'trash-asset-btn')]"
                TestObject specificTrashBtn = new TestObject("SpecificTrash").addProperty("xpath", ConditionType.EQUALS, xpathSpecificTrash)
                
                if (WebUI.waitForElementPresent(specificTrashBtn, 5, FailureHandling.OPTIONAL)) {
                    WebUI.executeJavaScript("arguments[0].click();", Arrays.asList(WebUI.findWebElement(specificTrashBtn)))
                    WebUI.comment("已刪除無效媒資：${currentAssetName}")
                }
                WebUI.delay(2) 
            }
        }
    } else {
        // 如果畫面上找不到可處理的 0 KB，刷新排序再試一次
        WebUI.click(sizeHeader)
        WebUI.delay(3)
        if (!WebUI.verifyElementPresent(validRowObj, 5, FailureHandling.OPTIONAL)) {
            allTaskDone = true
            WebUI.comment("所有符合條件的 0 KB 媒資已處理完畢。")
        }
    }
}