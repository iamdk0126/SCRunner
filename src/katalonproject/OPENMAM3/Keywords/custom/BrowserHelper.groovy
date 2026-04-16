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
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import org.openqa.selenium.WebDriver

import internal.GlobalVariable

public class BrowserHelper {
	/**
	 * 確認瀏覽器是否存在
	 * 如果不存在或被關閉，就重新開啟一個瀏覽器
	 * @param startUrl 開新瀏覽器後要打開的網址 (可選)
	 */
	@Keyword
	static void ensureBrowserAlive(String startUrl = null) {
		long threadId = Thread.currentThread().getId()
		try {
			WebDriver driver = DriverFactory.getWebDriver()
			if (driver == null) {
				println("❌ [Thread ${threadId}] Browser 不存在，準備重新開啟")
				WebUI.openBrowser('')
				WebUI.maximizeWindow(FailureHandling.OPTIONAL)
				if (startUrl && startUrl.trim()) {
					WebUI.navigateToUrl(startUrl)
				}
			} else {
				// 嘗試操作 driver，確保 session 還有效
				driver.getTitle()
				println("✅ [Thread ${threadId}] Browser 還在")
			}
		} catch (Exception e) {
			println("❌ [Thread ${threadId}] Browser 已失效，重新開啟")
			try {
				WebUI.closeBrowser()
			} catch (ignored) {}
			WebUI.openBrowser('')
			WebUI.maximizeWindow(FailureHandling.OPTIONAL)
			if (startUrl && startUrl.trim()) {
				WebUI.navigateToUrl(startUrl)
			}
		}
	}
}
