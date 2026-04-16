package custom

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import org.openqa.selenium.WebDriver
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.JavascriptExecutor
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory
import org.openqa.selenium.NoSuchSessionException
import com.kms.katalon.core.testobject.ConditionType
import java.util.List
import java.util.Map

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import custom.TimecodeHelper
import custom.DropdownHelper
import custom.TimeControl
import custom.AntMultiSelect
import custom.MxfManager


import internal.GlobalVariable

public class WebUIExtensions {
	/**
	 * 嘗試點擊一個元素，支援重試機制
	 * @param testObject  要點擊的物件
	 * @param maxRetry    最大重試次數 (預設 2 次)
	 * @param waitTime    每次等待秒數 (預設 5 秒)
	 * @return boolean    是否成功點擊
	 */
	@Keyword
	static boolean retryClick(TestObject testObject, int maxRetry = 2, int waitTime = 5) {
		boolean clicked = false

		for (int i = 0; i < maxRetry; i++) {
			if (WebUI.verifyElementClickable(testObject, FailureHandling.OPTIONAL)) {
				//WebUI.delay(waitTime)
				WebUI.click(testObject)
				WebUI.comment("✅ 點擊：${testObject.getObjectId()} (第 ${i+1} 次嘗試)")
				clicked = true
				break
			} else {
				WebUI.comment("⚠️ 第 ${i+1} 次點擊失敗，等待 ${waitTime} 秒後重試...")
				WebUI.delay(waitTime)
			}
		}

		if (!clicked) {
			WebUI.comment("❌ 點擊失敗：${testObject.getObjectId()} (已嘗試 ${maxRetry} 次)")
		}

		return clicked
	}
	/**
	 * 嘗試點擊一個元素，並確認點擊後該元素消失（例如關閉對話框或確定按鈕）。
	 * 若元素未消失，將進行重試點擊。
	 * @param testObject  要點擊並確認消失的物件
	 * @param maxRetry    最大重試次數 (預設 5 次)
	 * @param waitTime    每次點擊後等待消失的秒數 (預設 3 秒)
	 * @return boolean    是否成功點擊且物件已消失
	 */
	@Keyword
	static boolean retryClickClose(TestObject testObject, int maxRetry = 5, int waitTime = 3) {
		boolean isClosed = false

		for (int i = 0; i < maxRetry; i++) {
			// 先檢查物件是否還在且可以點擊
			if (WebUI.verifyElementClickable(testObject, FailureHandling.OPTIONAL)) {
				WebUI.click(testObject)
				WebUI.comment("👉 嘗試點擊關閉：${testObject.getObjectId()} (第 ${i+1} 次嘗試)")
				
				// 稍微等待系統前端處理 (避免點擊後瞬間去查，DOM 還沒更新)
				WebUI.delay(2)

				// 檢查物件是否已經消失 (waitForElementNotPresent 如果消失會回傳 true)
				if (WebUI.waitForElementNotVisible(testObject, waitTime, FailureHandling.OPTIONAL)) {
					isClosed = true
					WebUI.comment("✅ 點擊生效，物件已成功消失：${testObject.getObjectId()}")
					break // 成功關閉，跳出迴圈
				} else {
					WebUI.comment("⚠️ 點擊後物件尚未消失，準備進行下一次重試...")
				}
			} else {
				// 如果物件不可點擊，檢查是不是因為它已經自己消失了
				if (WebUI.waitForElementNotVisible(testObject, 1, FailureHandling.OPTIONAL)) {
					isClosed = true
					WebUI.comment("✅ 物件已不存在 (可能已被關閉或尚未出現)：${testObject.getObjectId()}")
					break
				} else {
					WebUI.comment("⚠️ 物件目前無法點擊，等待 1 秒後重試...")
					WebUI.delay(1)
				}
			}
		}

		if (!isClosed) {
			WebUI.comment("❌ 關閉失敗：${testObject.getObjectId()} (已嘗試 ${maxRetry} 次仍未消失)")
			WebUI.takeScreenshot(FailureHandling.OPTIONAL) // 失敗時截圖以便除錯
		}

		return isClosed
	}
	/**
	 * 嘗試點擊一個元素，支援重試機制
	 * @param testObject  要點擊的物件
	 * @param maxRetry    最大重試次數 (預設 2 次)
	 * @param waitTime    每次等待秒數 (預設 5 秒)
	 * @return boolean    是否成功點擊
	 */
	@Keyword
	static boolean loginClick(TestObject testObject, int maxRetry = 2, int waitTime = 5) {
		boolean clicked = false

		for (int i = 0; i < maxRetry; i++) {
			if (WebUI.waitForElementClickable(testObject, waitTime, FailureHandling.OPTIONAL)) {
				WebUI.click(testObject)
				WebUI.comment("✅ 點擊：${testObject.getObjectId()} (第 ${i+1} 次嘗試)")
				clicked = true
				break
			} else {
				WebUI.comment("⚠️ 第 ${i+1} 次點擊失敗，等待 ${waitTime} 秒後重試...")
				WebUI.delay(waitTime)
			}
		}

		if (!clicked) {
			WebUI.comment("❌ 點擊失敗：${testObject.getObjectId()} (已嘗試 ${maxRetry} 次)")
		}

		return clicked
	}
	/**
	 * 嘗試在輸入框設定文字，支援重試與驗證機制
	 * @param testObject   要輸入文字的物件
	 * @param text         要輸入的文字
	 * @param maxRetry     最大重試次數 (預設 2 次)
	 * @param waitTime     每次等待秒數 (預設 5 秒)
	 * @param flowControl  錯誤處理模式 (預設 OPTIONAL，可為 STOP_ON_FAILURE、CONTINUE_ON_FAILURE)
	 * @return boolean     是否成功輸入文字
	 */
	@Keyword
	static boolean retrySetText(TestObject testObject, String text, int maxRetry = 2, int waitTime = 5, FailureHandling flowControl = FailureHandling.OPTIONAL) {
		boolean success = false

		for (int i = 0; i < maxRetry; i++) {
			// 等待元素可見且可點擊
			boolean isVisible = WebUI.waitForElementVisible(testObject, waitTime, FailureHandling.OPTIONAL)
			boolean isClickable = WebUI.waitForElementClickable(testObject, waitTime, FailureHandling.OPTIONAL)

			if (isVisible && isClickable) {
				
				
				WebUIExtensions.retryClick(testObject)
				
				WebUI.delay(2)
				
				// 模擬 Ctrl + A + Backspace
				WebUI.sendKeys(testObject, Keys.chord(Keys.CONTROL,'a'))
				WebUI.sendKeys(testObject, Keys.chord(Keys.BACK_SPACE))
				
				WebUI.delay(2)
				WebUI.setText(testObject, text)
				
				WebUI.delay(0.5) // 避免 race condition

				// 取得實際值
				String actual = WebUI.getAttribute(testObject, 'value', FailureHandling.OPTIONAL) ?: ""
				
				// --- 核心改進：處理千分位與格式化問題 ---
				// 將實際值與期望值都移除逗號後再比對
				String normalizedActual = actual.replace(",", "").trim()
				String normalizedExpected = text.replace(",", "").trim()
		
				if (normalizedActual == normalizedExpected) {
					WebUI.comment("✅ 成功輸入！ 實際值: '${actual}' (格式化後), 期望值: '${text}' (第 ${i+1} 次嘗試)")
					success = true
					break
				} else {
					WebUI.comment("⚠️ 內容不符：實際='${actual}', 期望='${text}'，第 ${i+1} 次正在重試...")
				}
				
			} else {
				WebUI.comment("⚠️ 元素不可見或不可點擊，第 ${i+1} 次失敗，等待 ${waitTime} 秒後重試...")
				WebUI.delay(waitTime)
			}
		}

		if (!success) {
			String currentUrl = WebUI.getUrl(FailureHandling.OPTIONAL)
			WebUI.comment("❌ 輸入文字失敗：'${text}' 到 ${testObject.getObjectId()} (共嘗試 ${maxRetry} 次)")
			WebUI.comment("📍 頁面位置：${currentUrl}")
			WebUI.takeScreenshot(FailureHandling.OPTIONAL)

			if (flowControl == FailureHandling.STOP_ON_FAILURE) {
				KeywordUtil.markFailedAndStop("未能成功輸入文字 '${text}' 至 ${testObject.getObjectId()}")
			} else if (flowControl == FailureHandling.CONTINUE_ON_FAILURE) {
				KeywordUtil.markFailed("未能成功輸入文字 '${text}' 至 ${testObject.getObjectId()}")
			} else {
				WebUI.comment("⚠️ (OPTIONAL 模式) 輸入失敗，但不會中斷測試。")
			}
		}

		return success
	}
	/**
	 * 根據 label 文字輸入文字（Ant Design / 一般表單皆可）
	 * 自動定位 input 或 textarea，並呼叫 retrySetText()
	 * @param labelText   欄位的顯示文字 (例如「格式」或「Workflow ID」)
	 * @param text        要輸入的文字
	 * @param maxRetry    最大重試次數 (預設 2 次)
	 * @param waitTime    每次等待秒數 (預設 5 秒)
	 * @param flowControl 錯誤處理模式 (預設 OPTIONAL)
	 * @return boolean    是否成功輸入
	 */
	@Keyword
	static boolean setTextByLabel(String labelText, String text, int maxRetry = 2, int waitTime = 5, FailureHandling flowControl = FailureHandling.OPTIONAL) {
		WebUI.comment("🔍 以 label '${labelText}' 尋找輸入框並輸入文字 '${text}'")

		// -----------------------------------------------------------
		// 1️⃣ 定義 XPath 策略清單 (優先順序由上而下)
		// -----------------------------------------------------------
		List<String> xpathStrategies = []

		// 【策略 A】舊版嚴格模式 (Ant Design 標準)
		// 適用：<label title="關鍵字"> ... </label> (完全一致)
		// 說明：保留此策略以確保舊有測試案例不出錯
		xpathStrategies.add("//div[contains(@class,'ant-form-item')]//label[@title='${labelText}']/ancestor::div[contains(@class,'ant-form-item-row')]//descendant::*[self::input or self::textarea]")

		// 【策略 B】新版客製化結構 (Span + Sibling Div)
		// 適用：<span class="label...">關鍵字</span> <div...><input...></div>
		// 說明：解決 class 是亂數且沒有 label 標籤的結構
		xpathStrategies.add("//span[normalize-space()='${labelText}']/following-sibling::div//input")
		xpathStrategies.add("//span[normalize-space()='${labelText}']/ancestor::div[contains(@class, 'ant-form-item-row')]//input")


		// 【策略 C】寬鬆比對模式 (解決「冒號」或文字包含問題)
		// 適用：<label>檔案名稱：</label> (多了全形冒號)
		// 說明：不比對 title 屬性，而是比對 label 內的顯示文字，且使用 contains 容錯
		xpathStrategies.add("//div[contains(@class,'ant-form-item')]//label[contains(normalize-space(), '${labelText}')]/ancestor::div[contains(@class,'ant-form-item-row')]//descendant::*[self::input or self::textarea]")


		// -----------------------------------------------------------
		// 2️⃣ 執行搜尋迴圈
		// -----------------------------------------------------------
		TestObject targetInput = null
		boolean isFound = false
		String successStrategy = ""

		// 第一輪：快速掃描 (Time-efficient)
		for (String xpath : xpathStrategies) {
			TestObject tempObj = new TestObject("dynamic_input_" + labelText)
			tempObj.addProperty("xpath", ConditionType.EQUALS, xpath)

			// 用較短的時間 (1秒) 快速確認，避免卡太久
			if (WebUI.waitForElementPresent(tempObj, 1, FailureHandling.OPTIONAL)) {
				targetInput = tempObj
				isFound = true
				successStrategy = xpath
				WebUI.comment("🎯 成功匹配 XPath (第一輪): ${xpath}")
				break
			}
		}

		// -----------------------------------------------------------
		// 3️⃣ 捲動重試機制 (針對 Lazy Loading / Viewport 問題)
		// -----------------------------------------------------------
		if (!isFound) {
			WebUI.comment("⚠️ 初次掃描未找到，嘗試捲動頁面 (Scroll & Retry)...")
			WebUI.scrollToPosition(0, 500)
			WebUI.delay(1) // 等待渲染

			// 第二輪：完整等待掃描
			for (String xpath : xpathStrategies) {
				TestObject tempObj = new TestObject("dynamic_input_retry_" + labelText)
				tempObj.addProperty("xpath", ConditionType.EQUALS, xpath)

				// 這次給足完整的 waitTime
				if (WebUI.waitForElementPresent(tempObj, waitTime, FailureHandling.OPTIONAL)) {
					targetInput = tempObj
					isFound = true
					successStrategy = xpath
					WebUI.comment("🎯 捲動後成功匹配 XPath: ${xpath}")
					break
				}
			}
		}

		// -----------------------------------------------------------
		// 4️⃣ 結果處理與輸入
		// -----------------------------------------------------------
		if (!isFound) {
			WebUI.comment("❌ 徹底失敗：找不到標籤 '${labelText}' 對應的輸入框。已嘗試過 3 種 XPath 策略。")
			WebUI.takeScreenshot()
			return false
		}

		// 確保元素可視 (這一步對於 Headless Chrome 非常重要)
		WebUI.scrollToElement(targetInput, 3, FailureHandling.OPTIONAL)

		// 呼叫您原本的 retrySetText 方法進行輸入
		// 注意：請確保此 class 內有 retrySetText 方法，或引用正確的 Keyword
		boolean result = retrySetText(targetInput, text, maxRetry, waitTime, flowControl)

		if (result) {
			WebUI.comment("✅ 成功以 label '${labelText}' 輸入文字 '${text}'")
		} else {
			WebUI.comment("❌ 無法以 label '${labelText}' 輸入文字 '${text}'")
			WebUI.takeScreenshot()
		}

		return result
	}

	/**
	 * 點擊 Ant Design Table 內指定 title 的欄位
	 * @param titleText  欲點擊的 title 文字，例如: "看公視說英語"
	 */
	@Keyword
	static boolean clickTableTitle(String titleText) {

		// 依 title 動態產生 XPath
		String xpath = "//td[@title='${titleText}']"

		// 建立 TestObject
		TestObject obj = new TestObject("dynamicTitleCell")
		obj.addProperty("xpath", ConditionType.EQUALS, xpath)

		// 點擊
		WebUI.click(obj)

		WebUI.delay(2)
	}
	/**
	 * 整合點擊邏輯：如果目標物件 (targetObj) 不可見，則點擊觸發物件 (triggerObj) 將其喚出後再點擊。
	 * 常用場景：點擊「系統設定」前，若選單沒出現，先點擊「使用者頭像」。
	 * * @param targetObj   最終想要點擊的目標 (例如：系統設定)
	 * @param triggerObj  用來觸發目標出現的物件 (例如：使用者頭像)
	 * @param waitTime    點擊觸發物件後的等待秒數 (預設 5 秒)
	 * @return boolean    是否成功點擊目標
	 */
	@Keyword
	static boolean clickWithTrigger(TestObject targetObj, TestObject triggerObj, int waitTime = 5) {
		WebUI.comment("🔍 檢查目標物件 ${targetObj.getObjectId()} 是否可見...")

		// 1. 檢查目標是否可見
		boolean isVisible = WebUI.verifyElementVisible(targetObj, FailureHandling.OPTIONAL)

		if (!isVisible) {
			WebUI.comment("⚠️ 目標不可見，嘗試點擊觸發物件 ${triggerObj.getObjectId()}...")

			// 2. 點擊觸發物件 (這裡可以使用您類別內現有的 retryClick)
			boolean triggerSuccess = retryClick(triggerObj, 2, 2)

			if (triggerSuccess) {
				// 3. 等待目標物件出現
				WebUI.waitForElementVisible(targetObj, waitTime, FailureHandling.OPTIONAL)
			} else {
				WebUI.comment("❌ 無法點擊觸發物件，操作中止")
				return false
			}
		}

		// 4. 執行最終點擊 (建議使用 retryClick 確保穩定性)
		return retryClick(targetObj, 2, 2)
	}
	/**
	 * 點擊按鈕 A，並檢查物件 B 是否消失 (解決收合按鈕狀態改變與 DOM 移除的問題)
	 * @param btnA       要點擊的按鈕 (A)
	 * @param targetB    期望會看不見或被移除的目標物件 (B)
	 * @param maxRetry   最大重試次迴圈 (預設 5 次)
	 * @param waitTime   每次操作後等待物件 B 消失的秒數 (預設 5 秒)
	 * @return boolean   物件 B 是否成功消失
	 */
	@Keyword
	static boolean clickUntilTargetHides(TestObject btnA, TestObject targetB, int maxRetry = 5, int waitTime = 5) {
		boolean isGone = false

		for (int i = 0; i < maxRetry; i++) {
			WebUI.comment("🔄 開始第 ${i+1} 次檢查/操作迴圈")

			// 1. 綜合前置檢查：如果 B 已經不在 DOM 裡面，或者隱藏了，直接當作成功
			if (WebUI.waitForElementNotPresent(targetB, 2, FailureHandling.OPTIONAL) ||
				WebUI.waitForElementNotVisible(targetB, 1, FailureHandling.OPTIONAL)) {
				isGone = true
				WebUI.comment("✅ 目標物件 B 已經成功移除或隱藏：${targetB.getObjectId()}")
				break
			}

			// 2. 嘗試點擊 A (加上 verifyElementPresent 避免 A 已經消失導致報錯)
			if (WebUI.verifyElementPresent(btnA, 1, FailureHandling.OPTIONAL) &&
				WebUI.verifyElementClickable(btnA, FailureHandling.OPTIONAL)) {
				WebUI.click(btnA, FailureHandling.OPTIONAL)
				WebUI.comment("👉 已點擊按鈕 A：${btnA.getObjectId()}")
			} else {
				WebUI.comment("⚠️ 按鈕 A 目前無法點擊或已消失。")
			}

			// 3. 點擊後，給予充足時間等待 B 消失 (優先檢查 NotPresent)
			WebUI.comment("⏳ 正在等待目標物件 B 消失，最多等待 ${waitTime} 秒...")
			if (WebUI.waitForElementNotPresent(targetB, waitTime, FailureHandling.OPTIONAL) ||
				WebUI.waitForElementNotVisible(targetB, 1, FailureHandling.OPTIONAL)) {
				isGone = true
				WebUI.comment("✅ 點擊/等待生效，目標物件 B 已成功消失！")
				break
			} else {
				WebUI.comment("⚠️ 等待結束後，目標物件 B 似乎還在。")
			}
		}

		if (!isGone) {
			WebUI.comment("❌ 操作失敗：已嘗試 ${maxRetry} 次，但目標物件 B 仍未消失")
			WebUI.takeScreenshot(FailureHandling.OPTIONAL)
		}

		return isGone
	}
	/**
	 * 專門用於輸入數值或 textarea 欄位的填寫 (針對 Ant Design 優化)
	 * @param labelText  欄位名稱
	 * @param value      欲輸入的數值或文字
	 */
	@Keyword
	static boolean setValueByLabel(String labelText, String value, int waitTime = 5) {
		WebUI.comment("🔢 正在設定 '${labelText}' 的數值為: ${value}")

		// 1. 定位 XPath：鎖定 Ant Row 結構下的 textarea (優先) 或 input
		String xpath = "//div[contains(@class,'ant-form-item-row')][descendant::label[text()='${labelText}' or contains(.,'${labelText}')]]" +
				"//descendant::*[self::textarea or self::input]"

		TestObject dynamicTarget = new TestObject("value_by_label_" + labelText)
		dynamicTarget.addProperty("xpath", ConditionType.EQUALS, xpath)

		try {
			// 2. 確保元素存在並點擊它 (觸發 Focus)
			WebUI.waitForElementVisible(dynamicTarget, waitTime)
			WebUI.click(dynamicTarget)

			// 3. 清除舊值並輸入新值
			// 有些數值欄位用普通的 setText 不穩定，這裡改用 sendKeys 的組合技
			WebUI.sendKeys(dynamicTarget, Keys.chord(Keys.CONTROL, "a", Keys.BACK_SPACE))
			WebUI.sendKeys(dynamicTarget, value)

			// 4. 重要：輸入完後按一次 TAB 鍵，確保資料被系統「確認」並觸發驗證邏輯
			WebUI.sendKeys(dynamicTarget, Keys.chord(Keys.TAB))

			WebUI.comment("✅ 欄位 '${labelText}' 已填入: ${value}")
			return true
		} catch (Exception e) {
			WebUI.comment("❌ 設定欄位 '${labelText}' 失敗: " + e.getMessage())
			return false
		}
	}


	/**
	 * 專門用於 Ant Design DatePicker 的日期輸入
	 * 自動將 YYYY/MM/DD 轉換為 YYYY-MM-DD
	 */
	@Keyword
	static boolean setDateByLabel(String labelText, String dateValue, int waitTime = 5) {
		// 檢查數值，避免填入無意義的預設文字
		if (!dateValue || dateValue.trim() == "" || dateValue.contains("請選擇")) {
			WebUI.comment("⚠️ ${labelText} 日期數值為空或無效，略過填寫")
			return false
		}

		// 🔥 格式轉換：將 YYYY/MM/DD 轉換為 YYYY-MM-DD
		String formattedDate = dateValue.replace('/', '-')

		WebUI.comment("📅 正在設定 '${labelText}' 的日期為: ${formattedDate} (原值: ${dateValue})")

		// 定義 XPath：鎖定 label 下方的 ant-picker input
		String xpath = "//div[contains(@class,'ant-form-item-row')][descendant::label[text()='${labelText}' or contains(.,'${labelText}')]]" +
				"//div[contains(@class,'ant-picker')]//input"

		TestObject dateInput = new TestObject("date_picker_" + labelText)
		dateInput.addProperty("xpath", ConditionType.EQUALS, xpath)

		try {
			WebUI.waitForElementVisible(dateInput, waitTime)

			// 1. 點擊以聚焦輸入框
			WebUI.click(dateInput)

			// 2. 強力清除舊內容 (Ctrl+A + Backspace)
			WebUI.sendKeys(dateInput, Keys.chord(Keys.CONTROL, "a", Keys.BACK_SPACE))

			// 3. 輸入日期文字 (使用轉換後的格式)
			WebUI.sendKeys(dateInput, formattedDate)

			// 4. 重要：按下 ENTER 鍵以關閉日期彈窗並確認輸入
			WebUI.sendKeys(dateInput, Keys.chord(Keys.ENTER))

			WebUI.comment("✅ 日期欄位 '${labelText}' 已填入: ${formattedDate}")
			return true
		} catch (Exception e) {
			WebUI.comment("❌ 設定日期欄位 '${labelText}' 失敗: " + e.getMessage())
			return false
		}
	}


	/**
	 * 專門用於 Ant Design DateTimePicker (日期+時間) 的輸入
	 * 自動將 YYYY/MM/DD 轉換為 YYYY-MM-DD，並檢查補齊秒數
	 */
	@Keyword
	static boolean setTimeByLabel(String labelText, String dateTimeValue, int waitTime = 5) {
		if (!dateTimeValue || dateTimeValue.trim() == "" || dateTimeValue.contains("請選擇")) {
			WebUI.comment("⚠️ ${labelText} 數值為空，略過填寫")
			return false
		}

		// 1. 基礎轉換：斜線轉橫線並去前後空白
		String formatted = dateTimeValue.replace('/', '-').trim()

		// 2. 🔥 檢查秒數邏輯：
		// 如果只有 YYYY-MM-DD (長度10)，可能需要補 00:00:00
		// 如果是 YYYY-MM-DD HH:mm (長度16)，補上 :00
		if (formatted.length() == 10) {
			formatted += " 00:00:00"
			WebUI.comment("💡 偵測到僅有日期，自動補齊時間為: ${formatted}")
		} else if (formatted.length() == 16) {
			formatted += ":00"
			WebUI.comment("💡 偵測到缺失秒數，自動補齊為: ${formatted}")
		}

		WebUI.comment("🕒 最終設定 '${labelText}' 的時間為: [${formatted}]")

		// 定義 XPath
		String xpath = "//div[contains(@class,'ant-form-item-row')][descendant::label[text()='${labelText}' or contains(.,'${labelText}')]]" +
				"//div[contains(@class,'ant-picker')]//input"

		TestObject timeInput = new TestObject("time_picker_" + labelText)
		timeInput.addProperty("xpath", ConditionType.EQUALS, xpath)

		try {
			WebUI.waitForElementVisible(timeInput, waitTime)

			// 3. 點擊輸入框
			WebUI.click(timeInput)

			// 4. 強力清除舊內容
			WebUI.sendKeys(timeInput, Keys.chord(Keys.CONTROL, "a", Keys.BACK_SPACE))
			WebUI.delay(0.3)

			// 5. 輸入完整的日期時間字串
			WebUI.sendKeys(timeInput, formatted)
			WebUI.delay(0.5)

			// 6. 重要：兩次 ENTER 確保 Ant Design 元件捕捉到完整的長字串
			WebUI.sendKeys(timeInput, Keys.chord(Keys.ENTER))
			WebUI.delay(0.5)
			WebUI.sendKeys(timeInput, Keys.chord(Keys.ENTER))

			// 驗證輸入結果
			String finalValue = WebUI.getAttribute(timeInput, 'value')
			WebUI.comment("🔎 填寫後欄位最終數值: ${finalValue}")

			return true
		} catch (Exception e) {
			WebUI.comment("❌ 設定時間欄位 '${labelText}' 失敗: " + e.getMessage())
			return false
		}
	}
	/**
	 * 專門用於填寫音軌表格 (CH1 ~ CH8)
	 * 自動檢查是否需要點擊「新增」，並填寫第一列資料
	 * @param channels 傳入 8 個聲道的文字內容 List
	 */
	@Keyword
	static void setTableForTrack(List<String> channels) {
		WebUI.comment("🎵 正在處理音軌表格...")

		// 1. 定位「新增」按鈕
		TestObject btnAddTrack = new TestObject("btn_add_track")
		btnAddTrack.addProperty("xpath", ConditionType.EQUALS,
				"//label[@title='音軌']/ancestor::div[contains(@class,'ant-form-item-row')]//button[span[text()='新增']]")

		// 2. 定位第一列的第一個輸入框 (用來判斷表格是否已展開)
		String row0XPath = "//label[@title='音軌']/ancestor::div[contains(@class,'ant-form-item-row')]//tbody/tr[@data-row-key='0']//input"
		TestObject firstInput = new TestObject("ch1_check")
		firstInput.addProperty("xpath", ConditionType.EQUALS, "(${row0XPath})[1]")

		// 3. 檢查：如果第一列不存在，則點擊「新增」
		if (!WebUI.waitForElementPresent(firstInput, 2, FailureHandling.OPTIONAL)) {
			WebUI.comment("📝 音軌表格目前無資料列，點擊『新增』按鈕")
			WebUI.click(btnAddTrack)
			// 等待動畫完成
			WebUI.waitForElementPresent(firstInput, 5)
		}

		// 4. 開始依序填寫 CH1 ~ CH8
		for (int i = 0; i < 8; i++) {
			String value = (i < channels.size()) ? channels[i] : ""

			// 如果 CSV 沒資料，略過該格
			if (value == null || value.trim() == "" || value.equalsIgnoreCase("null")) {
				continue
			}

			TestObject chInput = new TestObject("ch_input_" + (i + 1))
			chInput.addProperty("xpath", ConditionType.EQUALS, "(${row0XPath})[" + (i + 1) + "]")

			try {
				WebUI.scrollToElement(chInput, 3, FailureHandling.OPTIONAL)
				// 先點擊，再全選刪除，最後輸入
				WebUI.click(chInput)
				WebUI.sendKeys(chInput, Keys.chord(Keys.CONTROL, "a", Keys.BACK_SPACE))
				WebUI.sendKeys(chInput, value)
				WebUI.comment("✅ CH${i + 1} 填入: ${value}")
			} catch (Exception e) {
				WebUI.comment("⚠️ CH${i + 1} 填寫異常: " + e.getMessage())
			}
		}
	}
	/**
	 * 專門用於填寫「明細資料」表格 For MAM v.2.5.2
	 * @param rowIndex 表格的行索引 (從 0 開始，通常第一筆是 0)
	 * @param details  包含 7 個欄位資料的 List: [開始時間, 結束時間, 來賓, 內容, 地點, 演唱者, 其他]
	 */
	@Keyword
	static void setTableForDetail(int tableRowIndex, List<String> details) {
		WebUI.comment("📝 正在填寫明細資料表格，第 ${tableRowIndex + 1} 列...")

		// 1. 定位「新增」按鈕
		TestObject btnAddDetail = new TestObject("btn_add_detail")
		btnAddDetail.addProperty("xpath", ConditionType.EQUALS,
				"//label[@title='明細資料']/ancestor::div[contains(@class,'ant-form-item-row')]//button[span[text()='新增']]")

		// 2. 定位該列的容器 (tr)
		String rowXPath = "//label[@title='明細資料']/ancestor::div[contains(@class,'ant-form-item-row')]//tbody/tr[@data-row-key='${tableRowIndex}']"
		TestObject rowObj = new TestObject("detail_row_" + tableRowIndex)
		rowObj.addProperty("xpath", ConditionType.EQUALS, rowXPath)

		// 3. 檢查：如果該列不存在，點擊「新增」
		if (!WebUI.waitForElementPresent(rowObj, 2, FailureHandling.OPTIONAL)) {
			WebUI.click(btnAddDetail)
			WebUI.waitForElementPresent(rowObj, 5)
		}

		// --- 開始填寫 7 個欄位 ---

		// 欄位 1: 開始時間 (Timecode 結構)
		TestObject startTC = new TestObject()
		startTC.addProperty("xpath", ConditionType.EQUALS, "(${rowXPath}/td)[1]//input[@type='tel']")
		custom.TimecodeHelper.inputTimecode(startTC, details[0])

		// 欄位 2: 結束時間 (Timecode 結構)
		TestObject endTC = new TestObject()
		endTC.addProperty("xpath", ConditionType.EQUALS, "(${rowXPath}/td)[2]//input[@type='tel']")
		custom.TimecodeHelper.inputTimecode(endTC, details[1])

		// 欄位 3 ~ 7: 普通文字輸入框 (來賓, 內容, 地點, 演唱者, 其他)
		for (int i = 2; i < 7; i++) {
			String value = details[i]
			if (value == null || value.trim() == "" || value.equalsIgnoreCase("null")) continue

				TestObject textInput = new TestObject()
			textInput.addProperty("xpath", ConditionType.EQUALS, "(${rowXPath}/td)[${i + 1}]//input[@type='text']")

			WebUI.scrollToElement(textInput, 3, FailureHandling.OPTIONAL)
			WebUI.click(textInput)
			WebUI.sendKeys(textInput, Keys.chord(Keys.CONTROL, "a", Keys.BACK_SPACE))
			WebUI.sendKeys(textInput, value)
		}
	}
	
	@Keyword
	static void checkAndResetBrowser(int currentIteration, int totalRunloop) {
		// 判斷是否為 10 的倍數，且為了避免重複關閉，確認不是最後一圈
		if (currentIteration % GlobalVariable.ResetBrowserCount == 0 && currentIteration != totalRunloop) {
			println("--- 偵測到第 ${currentIteration} 次執行，正在重置瀏覽器 ---")
					
			WebUI.closeBrowser()
			WebUI.delay(10)
			WebUI.comment("已達 10 次循環，瀏覽器已關閉，下次循環將重新開啟。")
		}
	}
	
	/**
	 * 等待頁面上任何處理中的文字消失，並驗證下載圖示出現
	 */
	@Keyword
	static boolean waitforprocessing(int maxWaitMinutes = 150) {
		int elapsedSeconds = 0
		int maxWaitSeconds = maxWaitMinutes * 60
		int checkInterval = 10 // 縮短檢查間隔（從30秒改為10秒），提升反應速度
		
		String processingXpath = "//span[contains(@class, 'file-hint-text') and contains(., '處理')]"
		TestObject progressObj = new TestObject("anyProcessingText").addProperty("xpath", ConditionType.EQUALS, processingXpath)
		
		String downloadIconXpath = "//span[@aria-label='download']"
		TestObject downloadIconObj = new TestObject("anyDownloadIcon").addProperty("xpath", ConditionType.EQUALS, downloadIconXpath)
	
		KeywordUtil.logInfo("【監控開始】等待全域處理文字消失，最大時限：${maxWaitMinutes} 分鐘")
	
		while (elapsedSeconds < maxWaitSeconds) {
			// --- 1. 防禦性檢查：確保瀏覽器還開著 ---
			try {
				DriverFactory.getWebDriver()
			} catch (Exception e) {
				KeywordUtil.markFailedAndStop("❌ 檢測到瀏覽器已關閉或 Session 遺失，監控中斷。")
				return false
			}
	
			// --- 2. 檢查「處理中」字眼是否還在 ---
			// 使用 OPTIONAL，避免找不到元素時直接拋出 Exception
			boolean isProcessing = WebUI.verifyElementPresent(progressObj, 3, FailureHandling.OPTIONAL)
			
			if (isProcessing) {
				String currentStatus = "讀取狀態中..."
				try {
					// 🔥 核心修正：使用 try-catch 包裹 getText，防止在讀取瞬間元素消失導致崩潰
					currentStatus = WebUI.getText(progressObj, FailureHandling.OPTIONAL)
				} catch (Exception ex) {
					KeywordUtil.logInfo("💡 狀態文字在此瞬間消失，視為處理即將完成。")
					isProcessing = false // 強制進入下方的完成判斷邏輯
				}
				
				if (isProcessing) {
					KeywordUtil.logInfo("[已耗時 ${elapsedSeconds / 60} min] 當前狀態: ${currentStatus}")
					
					// 活性維持：每 10 分鐘捲動一下頁面
					if (elapsedSeconds > 0 && elapsedSeconds % 600 == 0) {
						WebUI.scrollToPosition(0, 0)
						WebUI.delay(1)
						WebUI.scrollToElement(progressObj, 3, FailureHandling.OPTIONAL)
					}
				}
			}
	
			// --- 3. 如果「處理中」消失了，執行最終檢查 ---
			if (!isProcessing) {
				KeywordUtil.logInfo("✨ 處理文字已消失，正在確認『下載圖示』是否出現...")
				
				// 給予 15 秒 Buffer，等待下載按鈕渲染出來
				boolean hasDownloadIcon = WebUI.waitForElementPresent(downloadIconObj, 15, FailureHandling.OPTIONAL)
				
				if (hasDownloadIcon) {
					KeywordUtil.markPassed("✅ 【處理成功】下載圖示已出現，流程結束。")
					return true
				} else {
					// 如果文字消失了但下載圖示沒出來，代表可能出錯了
					WebUI.takeScreenshot()
					KeywordUtil.markFailedAndStop("❌ 【異常】處理文字消失，但『下載圖示』未出現，請檢查頁面是否報錯。")
					return false
				}
			}
			
			WebUI.delay(checkInterval)
			elapsedSeconds += checkInterval
		}
		
		KeywordUtil.markFailedAndStop("⏰ 【超時】超過 ${maxWaitMinutes} 分鐘仍未完成處理。")
		return false
	}
	
	/**
	 * 專門用於填寫 Ant Design 複合型 Timecode 並選擇 FPS
	 */
	@Keyword
	static boolean setAntTimecodeWithFPS(String labelText, String timecode, double detectfps, int waitTime = 5) {
		WebUI.comment("🕒 正在設定 '${labelText}' 為 ${timecode}，FPS 為 ${detectfps}")
		def driver = DriverFactory.getWebDriver()
		String fpsStr = String.valueOf(detectfps)

		// 1. 定位基礎列容器 (確保鎖定在該 Label 這一橫列)
		String baseXPath = "//div[contains(@class,'ant-form-item-row')][descendant::label[text()='${labelText}' or contains(.,'${labelText}')]]"
		
		try {
			// --- 步驟 A: 設定 FPS 下拉選單 ---
			// 定位 Select 本體 (Ant Design 的點擊觸發區)
			String selectXPath = "${baseXPath}//div[contains(@class, 'ant-select-selector')]"
			TestObject selectObj = new TestObject("fps_select_" + labelText).addProperty("xpath", ConditionType.EQUALS, selectXPath)
			
			if (WebUI.waitForElementPresent(selectObj, waitTime)) {
				// 先確認目前選中的值，如果已經對了就不重複點擊
				String currentText = WebUI.getText(selectObj, FailureHandling.OPTIONAL)
				
				if (!currentText.contains(fpsStr)) {
					// 強力點擊打開選單
					WebUI.scrollToElement(selectObj, 3, FailureHandling.OPTIONAL)
					WebUI.click(selectObj)
					WebUI.delay(0.5) // 給予選單彈出的緩衝時間

					// ⭐ 關鍵修正：定位彈出的選項
					// AntD 選單通常在 body 最底層，我們過濾掉隱藏的選單(ant-select-dropdown-hidden)
					String optionXPath = "//div[contains(@class, 'ant-select-dropdown') and not(contains(@class, 'ant-select-dropdown-hidden'))]" +
										 "//div[contains(@class, 'ant-select-item-option-content') and normalize-space(.)='${fpsStr}']"
					
					TestObject optionObj = new TestObject("target_fps_option").addProperty("xpath", ConditionType.EQUALS, optionXPath)
					
					if (WebUI.waitForElementPresent(optionObj, 5)) {
						// 使用 JavaScript 強力點擊選項，避免 "element not interactable"
						WebElement element = WebUiCommonHelper.findWebElement(optionObj, 3)
						WebUI.executeJavaScript("arguments[0].click();", Arrays.asList(element))
						WebUI.comment("🎯 已選擇 FPS: ${fpsStr}")
					} else {
						KeywordUtil.markWarning("⚠️ 找不到 FPS 選項: ${fpsStr}")
					}
				}
			}

			// --- 步驟 B: 填寫 Timecode (4 個輸入框) ---
			// 此部分邏輯保持與你現有的一致
			def parts = timecode.split("[:;]").collect { it.trim() }
			while (parts.size() < 4) { parts.add("00") }

			String inputsXPath = "${baseXPath}//div[contains(@class, 'input-pane')]//input"
			List<WebElement> inputs = driver.findElements(By.xpath(inputsXPath))

			if (inputs.size() >= 4) {
				for (int i = 0; i < 4; i++) {
					WebElement input = inputs.get(i)
					input.click()
					input.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.BACK_SPACE))
					String val = parts[i].padLeft(2, '0')
					input.sendKeys(val)
				}
				WebUI.comment("✅ 成功設定 ${labelText}: ${timecode}")
				return true
			}
		} catch (Exception e) {
			KeywordUtil.markFailed("❌ 設定 ${labelText} 失敗: " + e.getMessage())
		}
		return false
	}
	/**
	 * 專門給表格內部使用的版本 (WebUIExtensions 內)
	 * @param cellXPath 傳入該格 td 的 XPath
	 */
	@Keyword
	static boolean setAntTimecodeWithFPS_ForTable(String cellXPath, String timecode, double detectfps) {
		def driver = DriverFactory.getWebDriver()
		String fpsStr = String.valueOf(detectfps)
	
		try {
			// --- 步驟 A: 設定 FPS ---
			String selectXPath = "${cellXPath}//div[contains(@class, 'ant-select-selector')]"
			TestObject selectObj = new TestObject().addProperty("xpath", ConditionType.EQUALS, selectXPath)
			
			WebUI.scrollToElement(selectObj, 3, FailureHandling.OPTIONAL)
			WebUI.click(selectObj)
			WebUI.delay(0.5)
	
			String optionXPath = "//div[contains(@class, 'ant-select-dropdown') and not(contains(@class, 'ant-select-dropdown-hidden'))]" +
								 "//div[contains(@class, 'ant-select-item-option-content') and normalize-space(.)='${fpsStr}']"
			
			WebElement element = driver.findElement(By.xpath(optionXPath))
			WebUI.executeJavaScript("arguments[0].click();", Arrays.asList(element))
	
			// --- 步驟 B: 填寫 Timecode (由右向左填寫邏輯) ---
			// 這裡可以呼叫 TimecodeHelper.inputTimecode，它本來就是由右向左填
			TestObject inputGroup = new TestObject().addProperty("xpath", ConditionType.EQUALS, "${cellXPath}//input")
			custom.TimecodeHelper.inputTimecode(inputGroup, timecode)
			
			return true
		} catch (Exception e) {
			return false
		}
	}
	/**
	 * 專用的登出流程 Keyword
	 * 1. 點擊頭像觸發選單
	 * 2. 點擊登出按鈕
	 * 3. 驗證是否回到登入頁面
	 * @param avatarObj    個人頭像的 TestObject
	 * @param logoutBtnObj 登出按鈕的 TestObject
	 * @param loginPageObj 登入頁面識別物件 (例如：登入按鈕)
	 * @param maxRetry     最大重試次數
	 * @return boolean     是否登出成功
	 */
	@Keyword
	static boolean logoutFlow(TestObject avatarObj, TestObject logoutBtnObj, TestObject loginPageObj, int maxRetry = 3) {
		boolean isSuccess = false

		for (int i = 0; i < maxRetry; i++) {
			WebUI.comment("🔄 嘗試登出流程 (第 ${i + 1} 次嘗試)")
			
			// 1. 確保頭像可見並點擊
			if (!WebUI.waitForElementVisible(avatarObj, 10, FailureHandling.OPTIONAL)) {
				WebUI.refresh() // 若畫面卡住，嘗試重新整理
				WebUI.waitForPageLoad(10)
			}
			WebUIExtensions.retryClick(avatarObj, 2, 2)
			
			// 2. 等待選單動畫完成並檢查登出按鈕
			if (WebUI.waitForElementVisible(logoutBtnObj, 5, FailureHandling.OPTIONAL)) {
				WebUI.click(logoutBtnObj)
				WebUI.comment("👉 已點擊登出按鈕")
			} else {
				WebUI.comment("⚠️ 登出按鈕未出現，重新點擊頭像觸發選單...")
				WebUI.click(avatarObj, FailureHandling.OPTIONAL)
				if (WebUI.waitForElementVisible(logoutBtnObj, 3, FailureHandling.OPTIONAL)) {
					WebUI.click(logoutBtnObj)
				}
			}

			// 3. 驗證是否回到登入頁面 (關鍵：等待頁面跳轉)
			WebUI.waitForPageLoad(10)
			if (WebUI.waitForElementPresent(loginPageObj, 10, FailureHandling.OPTIONAL)) {
				isSuccess = true
				WebUI.comment("✅ 登出成功：已回到登入頁面")
				break
			} else {
				WebUI.comment("⚠️ 登出後未偵測到登入頁面物件，準備重試...")
			}
		}

		if (!isSuccess) {
			WebUI.comment("❌ 登出失敗：已嘗試 ${maxRetry} 次仍無法回到登入頁面")
			WebUI.takeScreenshot()
		}

		return isSuccess
	}
	/**
	 * 從影片擷取一幀並存為 TGA
	 */
	@Keyword
	static String captureFrameToTga(String videoPath) {
		try {
			// 1. 確保路徑處理正確
			File videoFile = new File(videoPath)
			if (!videoFile.exists()) {
				KeywordUtil.logInfo("❌ 原始影片不存在: " + videoPath)
				return ""
			}
	
			// 產生輸出路徑 (與影片同目錄，副檔名改 .tga)
			String outputPath = videoPath.substring(0, videoPath.lastIndexOf(".")) + ".tga"
			
			// 2. 構建 FFmpeg 命令
			// 使用 -ss 00:00:30 跳過開頭測試訊號
			// 使用 thumbnail=100 在接下來的區間找一張「最不像彩條」的圖
			// 重要：路徑一定要包在雙引號裡，防止空白導致錯誤
			String ffmpegExe = "C:\\ffmpeg\\bin\\ffmpeg.exe" // 如果 cmd 不能執行，請改為絕對路徑如 "C:\\ffmpeg\\bin\\ffmpeg.exe"
			String command = "${ffmpegExe} -y -ss 00:00:30 -i \"${videoPath}\" -vf \"thumbnail=100\" -frames:v 1 \"${outputPath}\""
			
			KeywordUtil.logInfo("🚀 準備執行指令: " + command)
	
			// 3. 執行指令並抓取錯誤流 (ErrStream)
			ProcessBuilder pb = new ProcessBuilder("cmd", "/c", command)
			pb.redirectErrorStream(true) // 合併輸出與錯誤，方便 Debug
			Process process = pb.start()
			
			// 讀取執行過程中的輸出訊息 (這會在 Katalon Console 顯示)
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))
			String line
			while ((line = reader.readLine()) != null) {
				System.out.println("FFmpeg Log: " + line)
			}
	
			boolean finished = process.waitFor(20, java.util.concurrent.TimeUnit.SECONDS)
			
			if (finished && new File(outputPath).exists()) {
				KeywordUtil.logInfo("✅ TGA 生成成功: " + outputPath)
				return outputPath
			} else {
				KeywordUtil.logInfo("❌ TGA 生成失敗：超時或檔案未產生。請檢查上方 FFmpeg Log。")
			}
		} catch (Exception e) {
			KeywordUtil.logInfo("❌ captureFrameToTga 發生異常: " + e.getMessage())
		}
		return ""
	}
	/**
	 * 填寫「時段檔次」表格
	 * @param airtimeData 格式範例: [[period: "晨間06:00~11:59", count: "4"], [period: "午間12:00~17:59", count: "5"]]
	 */
	@Keyword
	static void setAirtimeTable(List<Map<String, String>> airtimeData) {
		WebUI.comment("📊 開始填寫時段檔次表格...")
		def driver = DriverFactory.getWebDriver()
		
		// 1. 定位表格容器與新增按鈕
		String tableContainerXPath = "//label[@title='時段檔次']/ancestor::div[contains(@class,'ant-form-item-row')]"
		TestObject btnAdd = new TestObject("btn_add_airtime").addProperty("xpath", ConditionType.EQUALS, "${tableContainerXPath}//button[span[text()='新增']]")

		for (int i = 0; i < airtimeData.size(); i++) {
			String period = airtimeData[i].get("period")
			String count = airtimeData[i].get("count")
			
			// 2. 檢查該行 (tr) 是否存在，不存在則點擊新增
			String rowXPath = "${tableContainerXPath}//tbody/tr[@data-row-key='${i}']"
			TestObject rowObj = new TestObject("airtime_row_${i}").addProperty("xpath", ConditionType.EQUALS, rowXPath)
			
			if (!WebUI.waitForElementPresent(rowObj, 2, FailureHandling.OPTIONAL)) {
				WebUI.click(btnAdd)
				WebUI.waitForElementPresent(rowObj, 5)
			}

			// 3. 填寫「時段」 (Ant Design Select)
			if (period) {
				String selectXPath = "${rowXPath}/td[1]//div[contains(@class, 'ant-select-selector')]"
				WebElement selectElem = driver.findElement(By.xpath(selectXPath))
				
				// 捲動到可視範圍，避免被 Header 擋住
				WebUI.scrollToElement(new TestObject().addProperty("xpath", ConditionType.EQUALS, selectXPath), 3, FailureHandling.OPTIONAL)
				
				selectElem.click()
				WebUI.delay(0.5) // 等待動畫展開
				
				// 🔥 關鍵修正：確保只點擊「目前開啟中」的選單選項
				// AntD 開啟的選單會帶有 ant-select-dropdown-hidden 以外的 class
				String optionXPath = "//div[contains(@class, 'ant-select-dropdown') and not(contains(@class, 'ant-select-dropdown-hidden'))]//div[contains(@class, 'ant-select-item-option-content') and normalize-space()='${period}']"
				
				try {
					WebElement option = driver.findElement(By.xpath(optionXPath))
					// 使用 JavaScript 點擊以繞過 "not interactable"
					driver.executeScript("arguments[0].click();", option)
					WebUI.comment("✅ 第 ${i+1} 行時段設定為: ${period}")
				} catch (Exception ex) {
					KeywordUtil.logInfo("⚠️ 正常點擊失敗，嘗試強制等待並重新點擊...")
					WebUI.delay(1)
					WebElement option = driver.findElement(By.xpath(optionXPath))
					driver.executeScript("arguments[0].click();", option)
				}
				WebUI.delay(0.5) // 等待選單收合
			}

			// 4. 填寫「檔次」 (Textarea/Input)
			if (count) {
				String inputXPath = "${rowXPath}/td[2]//textarea | ${rowXPath}/td[2]//input"
				WebElement inputElem = driver.findElement(By.xpath(inputXPath))
				inputElem.click()
				// 清除並輸入
				inputElem.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a", org.openqa.selenium.Keys.BACK_SPACE))
				inputElem.sendKeys(count)
				WebUI.comment("✅ 第 ${i+1} 行檔次設定為: ${count}")
			}
		}
	}
	
	/**
	 * 填寫「有效區間」表格 (終極強化版)
	 * 解決橫向捲軸找不到元素、選單點不開、長選單需捲動等問題
	 * @param intervalData 格式範例: [[channel: "公視", start: "2026-03-02", end: "2027-03-01", weeks: ["星期一", "星期二"]]]
	 */
	@Keyword
	static void setValidityIntervalTable(List<Map<String, Object>> intervalData) {
		WebUI.comment("📅 開始填寫有效區間表格...")
		def driver = DriverFactory.getWebDriver()
		JavascriptExecutor js = (JavascriptExecutor) driver
		
		// 1. 定位表格容器
		String tableContainerXPath = "//label[@title='有效區間']/ancestor::div[contains(@class,'ant-form-item-row')]"
		TestObject btnAdd = new TestObject("btn_add_interval").addProperty("xpath", ConditionType.EQUALS, "${tableContainerXPath}//button[span[text()='新增']]")

		for (int i = 0; i < intervalData.size(); i++) {
			def data = intervalData[i]
			String rowXPath = "${tableContainerXPath}//tbody/tr[@data-row-key='${i}']"
			
			// 檢查該行是否存在，不存在則點擊新增
			if (!WebUI.waitForElementPresent(new TestObject().addProperty("xpath", ConditionType.EQUALS, rowXPath), 2, FailureHandling.OPTIONAL)) {
				WebUI.click(btnAdd)
				WebUI.delay(1)
			}

			// --- 2. 填寫「有效頻道」 (核心修改：使用方向鍵尋找) ---
			if (data.channel) {
				String chanInputXPath = "${rowXPath}/td[1]//input"
				WebElement chanInputElem = driver.findElement(By.xpath(chanInputXPath))
				
				// A. 強制捲動並點開
				js.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", chanInputElem)
				WebUI.delay(0.5)
				WebUI.click(new TestObject().addProperty("xpath", ConditionType.EQUALS, chanInputXPath))
				WebUI.delay(1)

				// B. 使用方向鍵遍歷選單
				boolean found = false
				int maxTries = 30 // 最多向下找 30 次，防止無限迴圈
				
				for (int attempt = 0; attempt < maxTries; attempt++) {
					// 取得當前「高亮」的選項文字
					String activeOptionXPath = "//div[contains(@class, 'ant-select-dropdown') and not(contains(@class, 'ant-select-dropdown-hidden'))]//div[contains(@class, 'ant-select-item-option-active')]//div[@class='ant-select-item-option-content']"
					
					try {
						String currentText = driver.findElement(By.xpath(activeOptionXPath)).getText().trim()
						WebUI.comment("🔍 目前高亮選項: ${currentText}")

						if (currentText.contains(data.channel)) {
							chanInputElem.sendKeys(Keys.ENTER)
							WebUI.comment("🎯 找到並選取: ${data.channel}")
							found = true
							break
						}
					} catch (Exception e) {
						// 沒看到高亮選項時也送一次向下鍵
					}
					
					chanInputElem.sendKeys(Keys.ARROW_DOWN)
					WebUI.delay(0.1) // 快速切換
				}

				if (!found) {
					KeywordUtil.markWarning("❌ 透過方向鍵仍找不到頻道: ${data.channel}")
					chanInputElem.sendKeys(Keys.ESCAPE)
				}
				WebUI.delay(0.5)
			}

			// --- 步驟 B: 有效日期 (第 2, 3 欄) ---
			["start": 2, "end": 3].each { key, colIndex ->
				String dateVal = data.get(key)
				if (dateVal) {
					String dateXpath = "${rowXPath}/td[${colIndex}]//input"
					// 每次填寫新欄位前，再次確保該欄位捲動到畫面內 (解決橫向捲軸問題)
					WebElement dateInput = driver.findElement(By.xpath(dateXpath))
					js.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", dateInput)
					
					js.executeScript("arguments[0].click();", dateInput)
					WebUI.delay(0.3)
					dateInput.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.BACK_SPACE))
					dateInput.sendKeys(dateVal.replace('/', '-'))
					dateInput.sendKeys(Keys.ENTER)
					WebUI.delay(0.3)
				}
			}

			// --- 4. 填寫「有效星期」 (複選同樣適用方向鍵邏輯) ---
			if (data.weeks instanceof List && !data.weeks.isEmpty()) {
				String weekInputXPath = "${rowXPath}/td[4]//input"
				WebElement weekInputElem = driver.findElement(By.xpath(weekInputXPath))
				
				js.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", weekInputElem)
				WebUI.click(new TestObject().addProperty("xpath", ConditionType.EQUALS, weekInputXPath))
				WebUI.delay(1)

				data.weeks.each { weekName ->
					int weekTries = 15
					for (int w = 0; w < weekTries; w++) {
						String activeOptionXPath = "//div[contains(@class, 'ant-select-dropdown') and not(contains(@class, 'ant-select-dropdown-hidden'))]//div[contains(@class, 'ant-select-item-option-active')]//div[@class='ant-select-item-option-content']"
						try {
							String currentWeek = driver.findElement(By.xpath(activeOptionXPath)).getText().trim()
							if (currentWeek.contains(weekName)) {
								weekInputElem.sendKeys(Keys.ENTER)
								WebUI.comment("✅ 已勾選: ${weekName}")
								break
							}
						} catch (Exception e) {}
						weekInputElem.sendKeys(Keys.ARROW_DOWN)
						WebUI.delay(0.1)
					}
				}
				weekInputElem.sendKeys(Keys.ESCAPE)
				WebUI.delay(0.5)
			}
			WebUI.comment("✅ 有效區間第 ${i+1} 列設定完成")
		}
	}
}
