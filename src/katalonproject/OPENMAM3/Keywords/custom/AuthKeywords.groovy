package custom

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW

import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows

import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory
import org.openqa.selenium.WebDriver
import org.openqa.selenium.NoSuchSessionException
import custom.WebUIExtensions
import custom.DropdownHelper


import internal.GlobalVariable

public class AuthKeywords {
	/**
	 * 確保登入，如果 browser 不存在或未登入，會自動登入
	 */
	@Keyword
	static void ensureLogin(String account, String encryptedPassword, String domain,String url, boolean openUrl = true) {
		WebDriver driver = getValidDriver()
		if (driver == null) {
			KeywordUtil.logInfo("Browser 不存在或 session 無效，開啟新 Browser 並登入")
			doLogin(account, encryptedPassword, domain, url,openUrl)
			return
		}

		// Browser 存在 → 檢查是否已登入
		boolean loggedIn = WebUI.verifyElementPresent(findTestObject('Header/OPENMAM'), 3, FailureHandling.OPTIONAL)
		if (loggedIn) {
			KeywordUtil.logInfo("已經登入，不需要重新登入")
		} else {
			KeywordUtil.logInfo("Browser 存在，但未登入 → 執行登入")
			doLogin(account, encryptedPassword, domain,url, openUrl)
		}
	}

	/**
	 * 執行登入，包含重試機制
	 */
	@Keyword
	static void doLogin(String account, String encryptedPassword, String domain, String url,boolean openUrl = true) {
		//String url = "https://${GlobalVariable.OpenMAM_ip}:${GlobalVariable.OpenMAM_port}/"
		//String domain = "TVBS"
		int maxRetry = 3
		boolean loginSuccess = false

		for (int i = 1; i <= maxRetry; i++) {
			try {
				WebDriver driver = getValidDriver()
				if (driver == null) {
					KeywordUtil.logInfo("Browser 不存在或 session 無效 → 開新 browser")
					WebUI.openBrowser('')
					WebUI.setViewPortSize(1920, 1080,FailureHandling.OPTIONAL)
					WebUI.maximizeWindow(FailureHandling.OPTIONAL)
				}

				if (openUrl) {
					WebUI.navigateToUrl(url)
				}

				WebUI.setText(findTestObject('登入頁/輸入帳號'), account)
				WebUI.setText(findTestObject('登入頁/輸入密碼'), encryptedPassword)

				WebUI.click(findTestObject('登入頁/域名下拉'))

				if (account.equalsIgnoreCase('root')) {

					DropdownHelper.selectVisibleDropdownOptionScroll("請選擇")
					WebUI.comment('帳號為 root，不需選擇網域。')
				} else {
					DropdownHelper.selectVisibleDropdownOptionScroll(domain)
					WebUI.comment('選擇網域:'+ domain )
				}

				// 使用 retryClick 保證點擊成功
				WebUIExtensions.retryClick(findTestObject('登入頁/登入'))

				// 等待登入完成
				loginSuccess = WebUI.waitForElementVisible(findTestObject('Header/OPENMAM'), 15, FailureHandling.OPTIONAL)
				if (loginSuccess) {
					KeywordUtil.logInfo("第 ${i} 次登入成功")
					WebUI.comment("登入成功: " + account )
					if (account.equalsIgnoreCase('root')) {
						WebUI.comment('帳號為 root，不需公告。')

						WebUI.delay(2)
						//WebUI.click(findTestObject('Header/資源組下拉'))

						//DropdownHelper.selectVisibleDropdownOptionScroll("新聞組")
					} else {
						WebUI.callTestCase(findTestCase('05BaseTest/公告'), [:], FailureHandling.OPTIONAL)

						WebUI.delay(2)
						//WebUI.click(findTestObject('Header/資源組下拉'))

						//DropdownHelper.selectVisibleDropdownOptionScroll("新聞部")
					}
					break
				} else {
					KeywordUtil.logInfo("第 ${i} 次登入失敗，重試中...")
					WebUI.delay(2)
					//WebUI.closeBrowser()
				}
			} catch (Exception e) {
				KeywordUtil.markWarning("第 ${i} 次登入發生 Exception: ${e.getMessage()}")
				try {
					//WebUI.closeBrowser()
				} catch(Exception ignore) {}
			}
		}

		if (!loginSuccess) {
			WebUI.takeScreenshot()
			WebUI.closeBrowser()
			assert false : "登入失敗，已重試 ${maxRetry} 次"
		}
	}

	/**
	 * 嘗試取得有效的 WebDriver，如果 session 無效返回 null
	 */
	private static WebDriver getValidDriver() {
		try {
			WebDriver driver = DriverFactory.getWebDriver()
			// 嘗試用 driver 執行操作驗證 session
			driver.getCurrentUrl()
			return driver
		} catch (NoSuchSessionException e) {
			return null
		} catch (Exception e) {
			return null
		}
	}
}