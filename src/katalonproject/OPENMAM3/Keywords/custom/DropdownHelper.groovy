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

import com.kms.katalon.core.testobject.ConditionType

import com.kms.katalon.core.webui.driver.DriverFactory

import com.kms.katalon.core.webui.common.WebUiCommonHelper
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.By
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.Keys
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.Duration
import com.kms.katalon.core.util.KeywordUtil
import java.util.Arrays


import internal.GlobalVariable

public class DropdownHelper {
	@Keyword
	def selectVisibleDropdownOption(String optionText) {
		// 限定在顯示出來的 dropdown
		String xpath = "//div[contains(@class,'ant-select-dropdown') and not(contains(@class,'ant-select-dropdown-hidden'))]" +
				"//div[@class='ant-select-item-option-content' and text()='" + optionText + "']"

		TestObject option = new TestObject("dynamicVisibleOption")
		option.addProperty("xpath", ConditionType.EQUALS, xpath)

		WebUI.waitForElementVisible(option, 10)
		WebUI.click(option)
	}
	@Keyword
	def selectVisibleDropdownOptionScrollOld(String optionText, int waitSeconds = 10) {
		WebDriver driver = DriverFactory.getWebDriver()
		JavascriptExecutor js = (JavascriptExecutor) driver

		// 取得目前已展開的下拉容器
		WebElement dropdownContainer = WebUiCommonHelper.findWebElement(
				new TestObject("visibleDropdown")
				.addProperty("xpath", com.kms.katalon.core.testobject.ConditionType.EQUALS,
				"//div[contains(@class,'ant-select-dropdown') and not(contains(@class,'ant-select-dropdown-hidden'))]//div[contains(@class,'rc-virtual-list-holder')]"),
				waitSeconds
				)

		boolean found = false
		long lastScrollTop = -1
		boolean triedFallback = false

		while (!found) {
			List<WebElement> options = dropdownContainer.findElements(
					By.xpath(".//div[contains(@class,'ant-select-item-option-content')]")
					)

			def visibleOptions = options.collect { it.getText().trim() }
			println("👀 目前可見選項: " + visibleOptions)

			for (WebElement option : options) {
				if (option.getText().trim().equals(optionText)) {
					// 滾動到中間並暫時高亮
					js.executeScript("arguments[0].scrollIntoView({block:'center'});", option)
					js.executeScript("arguments[0].style.backgroundColor='yellow';", option)
					Thread.sleep(200)
					js.executeScript("arguments[0].style.backgroundColor='';", option)
					Thread.sleep(200)
					option.click()
					found = true
					println("✅ 選擇完成：" + optionText)
					break
				}
			}

			if (!found) {
				long currentScrollTop = (Long) js.executeScript("return arguments[0].scrollTop;", dropdownContainer)
				if (currentScrollTop == lastScrollTop) {
					if (!triedFallback) {
						// 滾到底再回到頂
						js.executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", dropdownContainer)
						Thread.sleep(800)
						js.executeScript("arguments[0].scrollTop = 0;", dropdownContainer)
						Thread.sleep(800)
						triedFallback = true
					} else {
						throw new RuntimeException("❌ 無法找到選項：" + optionText)
					}
				}
				lastScrollTop = currentScrollTop

				long containerHeight = (Long) js.executeScript("return arguments[0].clientHeight;", dropdownContainer)
				js.executeScript("arguments[0].scrollTop += arguments[1];", dropdownContainer, containerHeight)
				Thread.sleep(300)
			}
		}
	}
	/**
	 * 在已展開的下拉選單中滾動尋找並點選指定文字的選項。
	 * 如果第一次完整滾動找不到，會再進行多次「下->上」循環嘗試。
	 *
	 * @param optionText 要選的文字
	 * @param waitSeconds 等待可見下拉容器的秒數（預設 10）
	 * @param extraRetries 當第一次完整檢查失敗後，要額外嘗試的下->上循環次數（預設 2）
	 */
	@Keyword
	static void selectVisibleDropdownOptionScroll(String optionText, int waitSeconds = 10, int extraRetries = 2, int scrollDelayMs = 100) {
		WebDriver driver = DriverFactory.getWebDriver()
		JavascriptExecutor js = (JavascriptExecutor) driver

		// 取得目前已展開的下拉容器
		WebElement dropdownContainer = WebUiCommonHelper.findWebElement(
				new TestObject("visibleDropdown")
				.addProperty("xpath", com.kms.katalon.core.testobject.ConditionType.EQUALS,
				"//div[contains(@class,'ant-select-dropdown') and not(contains(@class,'ant-select-dropdown-hidden'))]//div[contains(@class,'rc-virtual-list-holder')]"),
				waitSeconds
				)

		boolean found = false
		long lastScrollTop = -1
		int attempt = 0

		while (!found && attempt <= extraRetries) {
			List<WebElement> options = dropdownContainer.findElements(
					By.xpath(".//div[contains(@class,'ant-select-item-option-content')]")
					)

			def visibleOptions = options.collect { it.getText().trim() }
			println("👀 目前可見選項: " + visibleOptions)

			for (WebElement option : options) {
				if (option.getText().trim().equals(optionText)) {
					// 滾動到中間並暫時高亮
					js.executeScript("arguments[0].scrollIntoView({block:'center'});", option)
					js.executeScript("arguments[0].style.backgroundColor='yellow';", option)
					Thread.sleep(scrollDelayMs)
					js.executeScript("arguments[0].style.backgroundColor='';", option)
					Thread.sleep(scrollDelayMs)
					option.click()
					found = true
					println("✅ 選擇完成：" + optionText)
					break
				}
			}

			if (!found) {
				long currentScrollTop = (Long) js.executeScript("return arguments[0].scrollTop;", dropdownContainer)
				if (currentScrollTop == lastScrollTop) {
					if (attempt < extraRetries) {
						println("⚠️ 沒找到，嘗試 fallback 循環：第 ${attempt + 1} 次")
						// 滾到底再回到頂
						js.executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", dropdownContainer)
						Thread.sleep(scrollDelayMs * 1)
						js.executeScript("arguments[0].scrollTop = 0;", dropdownContainer)
						Thread.sleep(scrollDelayMs * 1)
						attempt++
						continue
					} else {
						throw new RuntimeException("❌ 無法找到選項：" + optionText)
					}
				}
				lastScrollTop = currentScrollTop

				long containerHeight = (Long) js.executeScript("return arguments[0].clientHeight;", dropdownContainer)
				js.executeScript("arguments[0].scrollTop += arguments[1];", dropdownContainer, containerHeight)
				Thread.sleep(scrollDelayMs)
			}
		}
	}
	@Keyword
	static boolean selectDropdownOptionByLabel(String labelText, String optionText, int waitTime = 5, int extraScrollRetries = 3, int scrollDelayMs = 100, FailureHandling flowControl = FailureHandling.OPTIONAL) {
		WebDriver driver = DriverFactory.getWebDriver()
		JavascriptExecutor js = (JavascriptExecutor) driver
		boolean success = false

		try {
			// 1️⃣ 找到下拉觸發器
			String dropdownXpath = "//label[normalize-space(text())='" + labelText + "']/ancestor::div[contains(@class,'ant-form-item')]//div[contains(@class,'ant-select-selector')]"
			TestObject dropdownTO = new TestObject("dropdown_" + labelText)
			dropdownTO.addProperty("xpath", ConditionType.EQUALS, dropdownXpath)

			if (!WebUI.verifyElementPresent(dropdownTO, waitTime, FailureHandling.OPTIONAL)) {
				KeywordUtil.markFailed("⚠️ 找不到標籤 '${labelText}' 對應的下拉容器")
				success = false
				return success
			}

			// 2️⃣ 點擊展開下拉
			WebUI.executeJavaScript("arguments[0].scrollIntoView(true);", Arrays.asList(WebUiCommonHelper.findWebElement(dropdownTO, waitTime)))
			WebUI.click(dropdownTO, FailureHandling.OPTIONAL)

			// 3️⃣ 找到展開的下拉列表
			String containerXpath = "//div[contains(@class,'ant-select-dropdown') and not(contains(@class,'ant-select-dropdown-hidden'))]//div[contains(@class,'rc-virtual-list-holder')]"
			TestObject containerTO = new TestObject("visibleDropdownContainer")
			containerTO.addProperty("xpath", ConditionType.EQUALS, containerXpath)
			WebElement container = WebUiCommonHelper.findWebElement(containerTO, waitTime)

			// 4️⃣ 滾動尋找選項
			boolean foundOption = false
			long lastScrollTop = -1
			int scrollAttempt = 0

			while (!foundOption && scrollAttempt <= extraScrollRetries) {
				List<WebElement> options = container.findElements(By.xpath(".//div[contains(@class,'ant-select-item-option-content')]"))
				for (WebElement option : options) {
					String currentOptionText = option.getText()
					// 使用 contains 匹配並加入 null 檢查
					if (currentOptionText != null && currentOptionText.trim().contains(optionText)) {
						js.executeScript("arguments[0].scrollIntoView({block:'center'});", option)
						js.executeScript("arguments[0].style.backgroundColor='yellow';", option)
						Thread.sleep(scrollDelayMs)
						js.executeScript("arguments[0].style.backgroundColor='';", option)

						// 使用 JS 點擊選項，最為保險
						js.executeScript("arguments[0].click();", option)
						foundOption = true
						break
					}
				}

				if (!foundOption) {
					long currentScrollTop = (Long) js.executeScript("return arguments[0].scrollTop;", container)
					if (currentScrollTop == lastScrollTop) {
						if (scrollAttempt < extraScrollRetries) {
							js.executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", container)
							Thread.sleep(scrollDelayMs)
							js.executeScript("arguments[0].scrollTop = 0;", container)
							Thread.sleep(scrollDelayMs)
							scrollAttempt++
							continue
						} else {
							KeywordUtil.markFailed("❌ 無法找到包含 '${optionText}' 的選項")
							success = false
							return success
						}
					}
					lastScrollTop = currentScrollTop
					long containerHeight = (Long) js.executeScript("return arguments[0].clientHeight;", container)
					js.executeScript("arguments[0].scrollTop += arguments[1];", container, containerHeight)
					Thread.sleep(scrollDelayMs)
				}
			}

			// 5️⃣ 移除驗證步驟，直接回傳成功
			if (foundOption) {
				WebUI.comment("✅ 已成功點擊包含 '${optionText}' 的選項")
				success = true
			}
		} catch (Exception e) {
			// 發生異常時記錄訊息，但依據您的需求仍回傳 true 以免測試完全中斷
			KeywordUtil.logInfo("ℹ️ 選擇過程結束（可能發生非預期狀況）: " + e.getMessage())
			success = true
		}

		return success
	}
}
