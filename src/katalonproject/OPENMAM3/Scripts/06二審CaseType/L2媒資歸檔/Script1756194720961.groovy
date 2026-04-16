import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By as By
import org.openqa.selenium.WebElement as WebElement
import com.kms.katalon.core.testobject.ConditionType as ConditionType
import java.io.FileWriter
import java.io.BufferedWriter
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.ArrayList
import java.util.List
import custom.WebUIExtensions

String account = accountParam
// --- 修正：使用 .toLong() 提升 IDE 相容性 ---
long endTime = (endTimeParam != null) ? endTimeParam.toLong() : 0

// ----------------------
// 主要邏輯：重試搜尋，直到找到「大小 > 0」且「按鈕可用」的列
// ----------------------
int maxRetry = 100
String keyword1 = null
boolean foundClickableButton = false

for (int attempt = 1; attempt <= maxRetry; attempt++) {
	// --- 修正：改用 .toLong() 避免 BigDecimal 導致的 mod 錯誤 ---
    long remaining = endTime - System.currentTimeMillis()
    long totalSec = (remaining / 1000).toLong()
    long min = (totalSec / 60).toLong()
    long sec = (totalSec % 60).toLong()
	WebUI.comment("【歸檔搜尋】第 ${attempt} 次嘗試，剩餘時間：${String.format("%02d:%02d", min, sec)}")
	
	if (remaining < 60000) { // 剩不到 1 分鐘
		println("【警告】時間不足，放棄搜尋。")
		break
	}
    
	// --- 修正：檢查剩餘時間 (改用 .toLong() 相容語法) ---
	if (System.currentTimeMillis() > (endTime - 60000)) {
		WebUI.comment("時間即將用盡，安全退出歸檔搜尋流程")
		break
	}
	println("【偵錯】第 ${attempt} 次嘗試，隨機取得未公開媒資名稱...")

	// 隨機讀取一筆未公開媒資名稱
	keyword1 = CustomKeywords.'custom.dbHelper.getRandomAssetName'(0, GlobalVariable.metateam)
	
	if (keyword1 == null || keyword1.isEmpty()) {
		println("【偵錯】取得的媒資名稱為空，跳過本次嘗試。")
		continue
	}

	// 進入未公開媒資頁並搜尋
	WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資'))

	if (WebUI.verifyElementPresent(findTestObject('主畫面/未公開媒資頁/搜尋媒資'), 1, FailureHandling.OPTIONAL)) {
		WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/搜尋媒資'))
	}
	
	WebUIExtensions.retrySetText(findTestObject('主畫面/未公開媒資頁/輸入關鍵字'), keyword1)
	WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/搜尋'))
	WebUI.delay(1) // 等待表格更新

	// --- 核心修改：遍歷表格列進行條件判斷 ---
	// 抓取頁面上所有具有資料的 tr
	List<WebElement> rows = WebUI.findWebElements(new TestObject().addProperty("xpath", ConditionType.EQUALS, "//tr[@data-row-key]"), 2)
	println("【偵錯】搜尋關鍵字 '${keyword1}' 後，找到 ${rows.size()} 筆結果列")

	for (WebElement row : rows) {
		List<WebElement> cells = row.findElements(By.tagName("td"))
		if (cells.size() < 5) continue // 防止索引越界
		
		// 1. 檢查「大小」欄位 (第 5 個 td, index 4)
		String sizeValue = cells.get(4).getText().trim().replace(",", "")
		println("【偵錯】檢查列資料 - 大小: ${sizeValue}")

		if (sizeValue != "0" && !sizeValue.isEmpty()) {
			// 2. 檢查該列內的「送出」按鈕
			try {
				WebElement submitBtn = row.findElement(By.xpath(".//button[contains(@id, 'submit-asset-btn')]"))
				
				if (submitBtn.isDisplayed() && submitBtn.isEnabled()) {
					println("【偵錯】成功！找到符合條件的媒資：'${keyword1}'，大小為 ${sizeValue}")
					
					// 執行點擊
					submitBtn.click()
					foundClickableButton = true
					break // 跳出列遍歷
				}
			} catch (Exception e) {
				println("【偵錯】該列找不到送出按鈕或按鈕狀態異常。")
			}
		} else {
			println("【偵錯】大小為 0，不符合歸檔條件。")
		}
	}

	if (foundClickableButton) break // 跳出重試迴圈
}

// ----------------------
// 後續流程：送出歸檔（確認視窗操作）
// ----------------------
if (foundClickableButton) {
	// 等待彈出的確認頁面並點擊最終送出
	WebUI.delay(1)
	if (WebUI.waitForElementClickable(findTestObject('主畫面/未公開媒資頁/送出歸檔頁/送出歸檔送出'), 5, FailureHandling.OPTIONAL)) {
		println("【最終步驟】點擊確認視窗的送出按鈕")
		WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/送出歸檔頁/送出歸檔送出'))
		
		println('已完成 尋找到size不為0 媒資 按下送出button')
		WebUI.comment('已完成 尋找到size不為0 媒資 按下送出button')
	}
} else {
	println("【最終結果】失敗！經過 ${maxRetry} 次嘗試，仍未找到大小不為 0 且可點擊的列。")
	keyword1 = null
}

// 結束處確保 return null 讓主程式知道沒成功
return foundClickableButton ? keyword1 : null