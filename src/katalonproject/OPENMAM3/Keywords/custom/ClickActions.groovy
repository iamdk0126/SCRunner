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

import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.exception.StepFailedException // 導入 StepFailedException 以便在重試失敗時拋出

import internal.GlobalVariable

public class ClickActions {
	// 類別名稱需與檔名相同

	/**
	 * 先等待指定的 Test Object 變為可點擊狀態，然後再執行點擊操作。
	 * 使用指定的超時時間和重試次數。
	 * @param to (TestObject) 要操作的測試物件。
	 * @param timeout (int) 等待元素可點擊的最長秒數。
	 * @param retries (int) 點擊失敗後的重試次數。
	 */
	@Keyword // 標記這是一個 Katalon 關鍵字
	def ClickA(TestObject to, int timeout, int retries) {

		println("ClickA: Waiting for element '${to.getObjectId()}' to be clickable (Timeout: ${timeout} seconds), with ${retries} retries.")

		int currentRetry = 0
		boolean clickedSuccessfully = false

		while (currentRetry <= retries) {
			try {
				// 步驟 1: 等待元素變為可點擊狀態
				// waitForElementClickable 會在超時前持續檢查元素是否 present, visible 且 enabled
				WebUI.waitForElementClickable(to, timeout, FailureHandling.STOP_ON_FAILURE) // 這裡讓它在等待失敗時就停止

				println("ClickA: Element '${to.getObjectId()}' is clickable. Proceeding to click.")

				// 步驟 2: 點擊元素
				WebUI.click(to, FailureHandling.STOP_ON_FAILURE) // 這裡讓它在點擊失敗時就停止

				println("ClickA: Successfully clicked element '${to.getObjectId()}'.")
				clickedSuccessfully = true
				break // 成功點擊，跳出迴圈
			} catch (Exception e) {
				println("ClickA: Failed to click element '${to.getObjectId()}' (Attempt ${currentRetry + 1}/${retries + 1}). Error: ${e.getMessage()}")
				currentRetry++
				if (currentRetry <= retries) {
					println("ClickA: Retrying...")
					// 可以選擇在這裡加入短暫的等待，例如 Thread.sleep(500)
				} else {
					println("ClickA: All retry attempts failed for element '${to.getObjectId()}'.")
					// 當所有重試都失敗後，拋出 StepFailedException
					throw new StepFailedException("Failed to click element '${to.getObjectId()}' after ${retries} retries. Error: ${e.getMessage()}")
				}
			}
		}

		if (!clickedSuccessfully) {
			// 如果迴圈結束但沒有成功點擊，表示在迴圈內部已經拋出異常，或者在某些極端情況下未被捕獲
			// 為了確保萬無一失，再次檢查並拋出
			throw new StepFailedException("Failed to click element '${to.getObjectId()}' after ${retries} retries.")
		}
	}

	/**
	 * 先等待指定的 Test Object 變為可點擊狀態，然後再執行點擊操作。
	 * 使用專案設定的預設超時時間和預設重試次數。
	 * @param to (TestObject) 要操作的測試物件。
	 */
	@Keyword // 標記這是一個 Katalon 關鍵字
	def ClickA(TestObject to) {
		// 從專案設定獲取預設的 Wait for Element Timeout 時間
		int defaultTimeout = RunConfiguration.getExecutionGeneralTimeout()
		// 設定一個預設的重試次數，例如 2 次
		int defaultRetries = 2 // 您可以根據需求修改這個預設值

		println("ClickA: Using default project timeout (${defaultTimeout} seconds) and default retries (${defaultRetries} times).")

		// 直接呼叫上面有指定超時時間和重試次數的 ClickA 方法
		this.ClickA(to, defaultTimeout, defaultRetries)
	}

	/**
	 * 先等待指定的 Test Object 變為可點擊狀態，然後再執行點擊操作。
	 * 使用指定的超時時間和預設重試次數。
	 * @param to (TestObject) 要操作的測試物件。
	 * @param timeout (int) 等待元素可點擊的最長秒數。
	 */
	@Keyword // 標記這是一個 Katalon 關鍵字
	def ClickA(TestObject to, int timeout) {
		int defaultRetries = 2 // 您可以根據需求修改這個預設值
		println("ClickA: Using specified timeout (${timeout} seconds) and default retries (${defaultRetries} times).")
		this.ClickA(to, timeout, defaultRetries)
	}
}