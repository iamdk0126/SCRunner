package custom

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

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
import org.openqa.selenium.WebElement
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.util.KeywordUtil

import internal.GlobalVariable

public class TimecodeHelper {
	/**
	 * 自動將 timecode 輸入到多個 <input> 欄位中 (反向填寫：4 -> 3 -> 2 -> 1)
	 * 強制填入包含 00 的所有數值，避免造成欄位偏移
	 */
	static void inputTimecode(TestObject timecodeObject, String timecodeString) {

		if (!timecodeString || timecodeString.trim() == '') {
			WebUI.comment("⚠️ 傳入 timecode 為空，略過")
			return
		}

		// 1. 清理字串：移除空格，並切割 00:00:00:00
		String cleanedTC = timecodeString.replaceAll("\\s", "")
		String[] parts = cleanedTC.split(":")

		List<WebElement> inputs = WebUiCommonHelper.findWebElements(timecodeObject, 10)

		if (!inputs || inputs.size() == 0) {
			WebUI.comment("❌ 找不到 timecode 的 input")
			return
		}

		int count = Math.min(parts.size(), inputs.size())

		// 🚀 反向輸入：格 -> 秒 -> 分 -> 時
		for (int i = count - 1; i >= 0; i--) {

			String currentPart = parts[i].trim()

			// ⭐ 修改處：即便是 "00" 或 "0"，也要執行填寫動作，不再 skip
			// 但為了 UI 穩定，如果值是 "00"，我們填入 "0"
			String valueToInput = (currentPart == "00") ? "0" : currentPart

			TestObject dynamic = new TestObject()
			dynamic.addProperty(
					"xpath",
					ConditionType.EQUALS,
					"(${timecodeObject.findPropertyValue('xpath')})[" + (i+1) + "]"
					)

			try {
				// 先點擊進入該格
				WebUI.click(dynamic)

				// 強力清除舊值
				WebUI.sendKeys(dynamic, Keys.chord(Keys.CONTROL, "a", Keys.BACK_SPACE))

				// 執行輸入 (包含 0)
				WebUI.sendKeys(dynamic, valueToInput)

				WebUI.comment("⏱ [反向輸入] 第 ${i+1} 格 → [${valueToInput}]")

				// 每填完一格稍微停頓，確保 React 狀態更新
				WebUI.delay(0.1)
			} catch (Exception e) {
				WebUI.comment("⚠️ 第 ${i+1} 格輸入失敗: " + e.getMessage())
			}
		}

		// 最後按一下 TAB 鍵跳出，確保整組 Timecode 被系統接受
		WebUI.sendKeys(new TestObject().addProperty("xpath", ConditionType.EQUALS, "(${timecodeObject.findPropertyValue('xpath')})[1]"), Keys.chord(Keys.TAB))

		WebUI.comment("✅ Timecode 完整輸入完成：" + cleanedTC)
	}
	static String getTimecode(TestObject timecodeObject) {
		List<WebElement> inputs = WebUiCommonHelper.findWebElements(timecodeObject, 10)

		if (inputs == null || inputs.size() == 0) {
			WebUI.comment("❌ 找不到 timecode 的輸入欄位！")
			return null
		}

		List<String> parts = []

		for (int i = 0; i < inputs.size(); i++) {
			String value = inputs[i].getAttribute("value") ?: ""
			parts.add(value)
			WebUI.comment("📖 第 ${i+1} 格讀取值：" + value)
		}

		String timecode = parts.join(":")
		WebUI.comment("✅ 讀取完成的 timecode：" + timecode)
		return timecode
	}
	// ─────────────────────────────────────────────────────────────────────────────
	// 新增：VTR時間區間的 Keyword
	// ─────────────────────────────────────────────────────────────────────────────

	private static TestObject createXPathObject(String xpath) {
		TestObject to = new TestObject()
		to.addProperty("xpath", ConditionType.EQUALS, xpath)
		return to
	}

	/**
	 * 設定 VTR 時間區間的開始與結束 timecode
	 * @param startTC "00:01:23:12"
	 * @param endTC   "00:04:56:10"
	 */
	@Keyword
	static boolean setVTRRange(String startTC, String endTC) {

		// 開始時間的 4 個 input
		TestObject startInputs = createXPathObject(
				"(//label[normalize-space()='VTR時間區間']/ancestor::div[@class='ant-form-item']//input[@type='tel'])[position()<=4]"
				)

		// 結束時間的 4 個 input
		TestObject endInputs = createXPathObject(
				"(//label[normalize-space()='VTR時間區間']/ancestor::div[@class='ant-form-item']//input[@type='tel'])[position()>=5 and position()<=8]"
				)

		WebUI.comment("⏱ 開始時間輸入 → " + startTC)
		inputTimecode(startInputs, startTC)

		WebUI.comment("⏱ 結束時間輸入 → " + endTC)
		inputTimecode(endInputs, endTC)
	}

	/**
	 * 清除 VTR 時間區間（點兩個 close-circle 按鈕）
	 */
	@Keyword
	static boolean clearVTRRange() {

		// 兩個刪除按鈕
		TestObject clear1 = createXPathObject(
				"(//label[normalize-space()='VTR時間區間']/ancestor::div[@class='ant-form-item']//span[@role='img' and @aria-label='close-circle'])[1]"
				)

		TestObject clear2 = createXPathObject(
				"(//label[normalize-space()='VTR時間區間']/ancestor::div[@class='ant-form-item']//span[@role='img' and @aria-label='close-circle'])[2]"
				)

		WebUI.click(clear1)
		WebUI.comment("🧹 已點擊開始區間清除按鈕")

		WebUI.click(clear2)
		WebUI.comment("🧹 已點擊結束區間清除按鈕")
	}
	/**
	 * 根據 Label 名稱自動輸入 Timecode (適用於 4 格 input 結構)
	 * @param labelText 欄位名稱，如 "影音帶 In", "影音帶 Out", "開始時間"
	 * @param tcString  Timecode 字串，如 "00:01:23:12"
	 */
	@Keyword
	static void setTimecodeByLabel(String labelText, String tcString) {
		if (!tcString || tcString.trim() == "") {
			WebUI.comment("⚠️ ${labelText} 數值為空，略過填寫")
			return
		}

		// 動態定位該 Label 之後的 4 個數值輸入框 (input[@type='tel'])
		String xpath = "//label[contains(text(),'${labelText}')]/ancestor::div[contains(@class,'ant-form-item')]//input[@type='tel']"

		TestObject tcInputs = new TestObject("tc_inputs_" + labelText)
		tcInputs.addProperty("xpath", ConditionType.EQUALS, xpath)

		WebUI.comment("⏱ 正在設定 ${labelText} → ${tcString}")
		inputTimecode(tcInputs, tcString)
	}
	/**
	 * 設定段落資訊 (支援自動點擊新增)
	 */
	@Keyword
	static void setSegmentData(int rowIndex, String title, String startTC, String endTC, double fps) {
		// 1. 定位該行的 Base XPath
		String rowBaseXPath = "//label[@title='段落資訊']/ancestor::div[contains(@class,'ant-form-item-row')]//tbody//tr[@data-row-key='${rowIndex}']"
		TestObject rowObj = createXPathObject(rowBaseXPath)
	
		// 2. 檢查：如果該行不存在，則點擊「新增」按鈕
		if (!WebUI.waitForElementPresent(rowObj, 2, FailureHandling.OPTIONAL)) {
			WebUI.comment("➕ 找不到第 ${rowIndex + 1} 行，點擊『新增』按鈕")
			String addBtnXpath = "//label[@title='段落資訊']/ancestor::div[contains(@class,'ant-form-item-row')]//div[contains(@class,'ant-table-footer')]//button"
			WebUI.click(createXPathObject(addBtnXpath))
			WebUI.waitForElementPresent(rowObj, 5)
		}
	
		// 3. 填寫標題 (第一欄)
		if (title != null) {
			WebUI.setText(createXPathObject(rowBaseXPath + "//td[1]//textarea"), title)
		}
	
		// 4. 填寫時碼
		if (rowIndex == 0) {
			WebUI.comment("🚀 第一列：處理 FPS 下拉選單並反向填寫時碼")
			
			// 處理 Start TC (第二欄)
			if (startTC != null) {
				fillTimecodeWithFPS_Manual(rowBaseXPath + "//td[2]", startTC, fps)
			}
			
			// 處理 End TC (第三欄)
			if (endTC != null) {
				fillTimecodeWithFPS_Manual(rowBaseXPath + "//td[3]", endTC, fps)
			}
		} else {
			WebUI.comment("📝 第 ${rowIndex + 1} 列：直接執行反向填寫時碼 (不觸發 FPS)")
			
			if (startTC != null) {
				inputTimecode(createXPathObject(rowBaseXPath + "//td[2]//input"), startTC)
			}
			if (endTC != null) {
				inputTimecode(createXPathObject(rowBaseXPath + "//td[3]//input"), endTC)
			}
		}
	}
	
	/**
	 * 內部私有方法：專門處理表格第一列那種 [Input Group] + [FPS Select] 的結構
	 */
	private static void fillTimecodeWithFPS_Manual(String cellXPath, String tc, double fps) {
		String fpsStr = String.valueOf(fps)
		
		try {
			// A. 點擊 FPS 下拉選單
			TestObject selectObj = createXPathObject("${cellXPath}//div[contains(@class, 'ant-select-selector')]")
			WebUI.click(selectObj)
			WebUI.delay(0.5)
			
			// B. 點擊對應 FPS 的選項 (過濾掉隱藏的 dropdown)
			String optionXPath = "//div[contains(@class, 'ant-select-dropdown') and not(contains(@class, 'ant-select-dropdown-hidden'))]" +
								 "//div[contains(@class, 'ant-select-item-option-content') and normalize-space(.)='${fpsStr}']"
			WebUI.click(createXPathObject(optionXPath))
			WebUI.comment("🎯 已成功選取第一列 FPS: ${fpsStr}")
		} catch (Exception e) {
			WebUI.comment("⚠️ FPS 選擇失敗或已是預設值: " + e.getMessage())
		}
	
		// C. 呼叫你原本的反向填寫邏輯 (4 -> 3 -> 2 -> 1)
		TestObject inputsObj = createXPathObject("${cellXPath}//input")
		inputTimecode(inputsObj, tc)
	}

	/**
	 * 核心輸入邏輯
	 */
	private static void executeTimecodeEntry(TestObject timecodeObject, String timecodeString, boolean isReverse) {
		if (!timecodeString || timecodeString.trim() == '') {
			WebUI.comment("⚠️ 傳入 timecode 為空，略過")
			return
		}

		String cleanedTC = timecodeString.replaceAll("\\s", "")
		String[] parts = cleanedTC.split(":")
		List<WebElement> inputs = WebUiCommonHelper.findWebElements(timecodeObject, 10)

		if (!inputs || inputs.size() == 0) {
			WebUI.comment("❌ 找不到 timecode 的 input")
			return
		}

		int count = Math.min(parts.size(), inputs.size())
		def indices = isReverse ? (count - 1..0) : (0..count - 1)
		String modeText = isReverse ? "反向" : "正向"

		for (int i in indices) {
			String currentPart = parts[i].trim()
			String valueToInput = (currentPart == "00" || currentPart == "0") ? "0" : currentPart

			TestObject dynamic = new TestObject()
			dynamic.addProperty("xpath", ConditionType.EQUALS, "(${timecodeObject.findPropertyValue('xpath')})[" + (i + 1) + "]")

			try {
				WebUI.click(dynamic)
				WebUI.sendKeys(dynamic, Keys.chord(Keys.CONTROL, "a", Keys.BACK_SPACE))
				WebUI.sendKeys(dynamic, valueToInput)
				WebUI.comment("⏱ [${modeText}輸入] 第 ${i + 1} 格 → [${valueToInput}]")
				WebUI.delay(0.1)
			} catch (Exception e) {
				WebUI.comment("⚠️ 第 ${i + 1} 格輸入失敗: " + e.getMessage())
			}
		}
		WebUI.sendKeys(new TestObject().addProperty("xpath", ConditionType.EQUALS, "(${timecodeObject.findPropertyValue('xpath')})[1]"), Keys.chord(Keys.TAB))
	}
	/**
	 * 設定 VTR 時間區間 - Start 與 End 分別控制方向
	 * @param startReverse 開始時間是否反向 (預設 true)
	 * @param endReverse   結束時間是否反向 (預設 true)
	 */
	@Keyword
	static boolean setVTRRange2(String startTC, String endTC, boolean startReverse = true, boolean endReverse = true) {
		TestObject startInputs = createXPathObject("(//label[normalize-space()='VTR時間區間']/ancestor::div[@class='ant-form-item']//input[@type='tel'])[position()<=4]")
		TestObject endInputs = createXPathObject("(//label[normalize-space()='VTR時間區間']/ancestor::div[@class='ant-form-item']//input[@type='tel'])[position()>=5 and position()<=8]")

		WebUI.comment("⏱ 設定 VTR 開始時間 [${startTC}] 方向: ${startReverse ? '反向' : '正向'}")
		executeTimecodeEntry(startInputs, startTC, startReverse)

		WebUI.comment("⏱ 設定 VTR 結束時間 [${endTC}] 方向: ${endReverse ? '反向' : '正向'}")
		executeTimecodeEntry(endInputs, endTC, endReverse)
		return true
	}

	/**
	 * 設定段落資訊 - Start 與 End 分別控制方向
	 */
	@Keyword
	static void setSegmentData2(int rowIndex, String title, String startTC, String endTC, boolean startReverse = true, boolean endReverse = true) {
		String rowBaseXPath = "(//label[text()='段落資訊']/ancestor::div[contains(@class,'ant-form-item-row')]//tbody//tr[contains(@class,'ant-table-row')])[${rowIndex + 1}]"

		if (title != null) {
			TestObject titleObj = createXPathObject(rowBaseXPath + "//td[1]//textarea")
			WebUI.setText(titleObj, title)
		}

		if (startTC != null) {
			TestObject startObj = createXPathObject(rowBaseXPath + "//td[2]//input")
			WebUI.comment("⏱ 第 ${rowIndex+1} 行 Start 方向: ${startReverse ? '反向' : '正向'}")
			executeTimecodeEntry(startObj, startTC, startReverse)
		}

		if (endTC != null) {
			TestObject endObj = createXPathObject(rowBaseXPath + "//td[3]//input")
			WebUI.comment("⏱ 第 ${rowIndex+1} 行 End 方向: ${endReverse ? '反向' : '正向'}")
			executeTimecodeEntry(endObj, endTC, endReverse)
		}
	}
	// ─────────────────────────────────────────────────────────────────────────────
	// 新增：抓取媒資資訊視窗中特定 Timecode 的 Keyword
	// ─────────────────────────────────────────────────────────────────────────────

	/**
	 * 取得媒資資訊中「起始 Timecode」(定錨點為快捷鍵 E 的按鈕)
	 * @return 格式如 "00:00:30;00"
	 */
	@Keyword
	static String getAssetStartTC() {
		// 定位到按鈕左邊的時碼容器中的所有 input
		String xpath = "//button[@title='E']/preceding-sibling::div[contains(@class, 'time-picker')]//input"
		TestObject to = createXPathObject(xpath)
		
		WebUI.comment("📖 正在抓取起始 Timecode (E)...")
		// 等待第一格出現，確保 UI 已渲染
		if (WebUI.waitForElementPresent(to, 10, FailureHandling.OPTIONAL)) {
			return getFormattedTimecode(to)
		}
		WebUI.comment("❌ 抓取失敗：找不到起始 Timecode 欄位")
		return null
	}

	/**
	 * 取得媒資資訊中「結束 Timecode」(定錨點為快捷鍵 R 的按鈕)
	 * @return 格式如 "00:49:10;20"
	 */
	@Keyword
	static String getAssetEndTC() {
		String xpath = "//button[@title='R']/preceding-sibling::div[contains(@class, 'time-picker')]//input"
		TestObject to = createXPathObject(xpath)
		
		WebUI.comment("📖 正在抓取結束 Timecode (R)...")
		if (WebUI.waitForElementPresent(to, 10, FailureHandling.OPTIONAL)) {
			return getFormattedTimecode(to)
		}
		WebUI.comment("❌ 抓取失敗：找不到結束 Timecode 欄位")
		return null
	}

	/**
	 * 內部私有方法：將 4 個 input 的值組合成標準 Timecode 格式
	 */
	private static String getFormattedTimecode(TestObject timecodeObject) {
		// 使用 WebUiCommonHelper 抓取該物件定位到的所有 WebElements (應為 4 個)
		List<WebElement> inputs = WebUiCommonHelper.findWebElements(timecodeObject, 10)

		if (inputs == null || inputs.size() < 4) {
			WebUI.comment("❌ 抓取失敗：預期 4 格，實際只找到 ${inputs?.size()} 格")
			return null
		}

		// 依序抓取：時、分、秒、格
		String hh = inputs[0].getAttribute("value") ?: "00"
		String mm = inputs[1].getAttribute("value") ?: "00"
		String ss = inputs[2].getAttribute("value") ?: "00"
		String ff = inputs[3].getAttribute("value") ?: "00"

		// 格式化輸出
		String timecode = "${hh}:${mm}:${ss};${ff}"
		WebUI.comment("✅ 成功讀取 Timecode：${timecode}")
		return timecode
	}
	/**
	 * 自動切割並填入六段段落資訊 (完美支援 23.98, 29.97, 59.94 等動態數值)
	 */
	@Keyword
	static void splitAndSetSixSegments(String startTC, String endTC, double fps) {
		// 🔥 強制防護：若 startTC 或 endTC 為 null，立即中斷並報錯
		if (startTC == null || endTC == null) {
			KeywordUtil.markFailedAndStop("❌ [致命錯誤] 傳入的 Timecode 為空值 (null)，無法進行切割計算。請檢查抓取步驟是否成功。")
			return
		}

		int fpsBase = (int) Math.round(fps)
		WebUI.comment("🎬 偵測到 FPS: ${fps}, 使用進位基底: ${fpsBase}")

		// 輔助方法 A：增加 Null 檢查與 Split 容錯
		def tcToFrames = { String tc ->
			// 使用 regex [:;] 同時支援冒號與分號
			def parts = tc.split("[:;]")
			if (parts.size() < 4) {
				throw new Exception("時碼格式錯誤: ${tc}")
			}
			def p = parts.collect { it.trim().toInteger() }
			return (((p[0] * 3600) + (p[1] * 60) + p[2]) * fpsBase) + p[3]
		}

		// 輔助方法 B：影格數轉回 TC 格式
		def framesToTC = { int f ->
			int hh = f / (3600 * fpsBase)
			int mm = (f % (3600 * fpsBase)) / (60 * fpsBase)
			int ss = (f % (60 * fpsBase)) / fpsBase
			int ff = f % fpsBase
			// 統一回傳冒號格式，您的 inputTimecode 會處理它
			return String.format("%02d:%02d:%02d:%02d", hh, mm, ss, ff)
		}

		int startFrames = tcToFrames(startTC)
		int endFrames = tcToFrames(endTC)
		int totalFrames = endFrames - startFrames
		int segmentFrames = Math.floor(totalFrames / 6) // 每段長度

		for (int i = 0; i < 6; i++) {
			int currentIn = startFrames + (segmentFrames * i)
			// 確保最後一段精準落在 endTC，其餘段落則銜接下一段(減1格避免重疊)
			int currentOut = (i == 5) ? endFrames : (startFrames + (segmentFrames * (i + 1)) - 1)

			String segmentTitle = "段落 ${i + 1}"
			String inStr = framesToTC(currentIn)
			String outStr = framesToTC(currentOut)

			WebUI.comment("📝 [${fps} fps] 填寫第 ${i + 1} 段: ${inStr} ~ ${outStr}")
			
			// 這裡會依序呼叫 setSegmentData(0...5)
			// 當 i=1 時，它會發現 data-row-key="1" 不存在，然後自動點新增
			setSegmentData(i, "SEC ${i + 1}", inStr, outStr)
		}
	}

	/**
	 * 專門針對「編輯媒資」彈窗抓取「主檔時長」後的 FPS 值
	 */
	@Keyword
	static double getAssetFPS() {
		// 定位策略：
		// 1. 鎖定具有 role='dialog' 的 ant-modal 容器
		// 2. 在該容器內尋找 title 為 '主檔時長' 的 label
		// 3. 往上找同一列的控制區塊，再往下找包含 'fps' 文字的 span
		String xpath = "//div[@role='dialog']//label[contains(., '主檔時長')]/ancestor::div[contains(@class,'ant-row')]//span[contains(., 'fps')]"                                                                                                                                      
		TestObject fpsObj = createXPathObject(xpath)
		
		WebUI.comment("🔍 正在『編輯媒資』彈窗中定位 FPS 文字...")

		// 增加等待時間，因為彈窗開啟與 API 資料填入 span 有時間差
		if (WebUI.waitForElementPresent(fpsObj, 15)) {
			try {
				// 確保元素進入視覺範圍
				WebUI.scrollToElement(fpsObj, 3, FailureHandling.OPTIONAL)
				
				String rawText = WebUI.getText(fpsObj) // 取得如 "( 59.94 fps )"
				WebUI.comment("📖 讀取到原始文字: " + rawText)
				
				// 使用 Regex 提取數字 (支援整數與小數)
				java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\d+\\.?\\d*").matcher(rawText)
				
				if (matcher.find()) {
					double fps = Double.parseDouble(matcher.group())
					WebUI.comment("✅ 成功抓取 FPS: " + fps)
					return fps
				} else {
					WebUI.comment("⚠️ 抓到文字但無法解析數字: " + rawText)
				}
			} catch (Exception e) {
				WebUI.comment("❌ 抓取過程發生錯誤: " + e.getMessage())
			}
		} else {
			WebUI.comment("❌ 逾時 15 秒仍找不到彈窗內的 FPS 標籤。")
			WebUI.comment("💡 建議檢查：彈窗是否真的已完全展開？XPath 是否能在 F12 Console 運作？")
		}

		WebUI.comment("⚠️ 使用保險預設值 29.97")
		return 29.97
	}
	/**
	 * 自動根據時長動態切割段落
	 * @param startTC 開始時間 "00:00:00;00"
	 * @param endTC   結束時間 "00:48:00;00"
	 * @param fps     影格率 (如 29.97)
	 * @param intervalMinutes 自訂每幾分鐘切一段 (預設 8 分鐘)
	 */
	@Keyword
	static void splitAndSetDynamicSegments(String startTC, String endTC, double fps, int intervalMinutes = 8) {
		if (startTC == null || endTC == null ) {
			KeywordUtil.markFailedAndStop("❌ [錯誤] 傳入的 Timecode 無效，請檢查抓取結果。")
			return
		}

		int fpsBase = (int) Math.round(fps)
		
		// 輔助方法：TC 轉影格數 (支援 : 與 ;)
		def tcToFrames = { String tc ->
			def p = tc.split("[:;]").collect { it.trim().toInteger() }
			return (((p[0] * 3600) + (p[1] * 60) + p[2]) * fpsBase) + p[3]
		}

		// 輔助方法：影格數轉回 TC
		def framesToTC = { int f ->
			int hh = f / (3600 * fpsBase)
			int mm = (f % (3600 * fpsBase)) / (60 * fpsBase)
			int ss = (f % (60 * fpsBase)) / fpsBase
			int ff = f % fpsBase
			return String.format("%02d:%02d:%02d:%02d", hh, mm, ss, ff)
		}

		int startFrames = tcToFrames(startTC)
		int endFrames = tcToFrames(endTC)
		int totalFrames = endFrames - startFrames
		
		// --- 動態邏輯計算 ---
		double totalMinutes = (totalFrames / fpsBase) / 60
		
		// 使用傳入的 intervalMinutes 計算段數 (四捨五入，確保至少 1 段)
		int segmentCount = Math.max(1, (int) Math.round(totalMinutes / intervalMinutes))
		
		KeywordUtil.logInfo("🎬 總長 ${totalMinutes.round(2)} 分，設定每 ${intervalMinutes} 分一段，共切為 ${segmentCount} 段")

		int segmentFrames = Math.floor(totalFrames / segmentCount)

		for (int i = 0; i < segmentCount; i++) {
			int currentIn = startFrames + (segmentFrames * i)
			// 銜接邏輯：最後一段到 End，其餘段落 Out 為下一段 In 減 1 格
			int currentOut = (i == (segmentCount - 1)) ? endFrames : (startFrames + (segmentFrames * (i + 1)) - 1)

			String segmentTitle = "段落 ${i + 1}"
			String inStr = framesToTC(currentIn)
			String outStr = framesToTC(currentOut)

			// 呼叫您現有的 setSegmentData (內含自動點擊「新增」按鈕邏輯)
			setSegmentData(i, segmentTitle, inStr, outStr, fps)
		}
	}
}
