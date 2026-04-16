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
import java.util.List

String account = accountParam

// ----------------------
// 主要邏輯：重試直到找到可點擊按鈕
// ----------------------
int maxRetry = 5
String keyword1 = null
boolean foundClickableButton = false

for (int attempt = 1; attempt <= maxRetry; attempt++) {
	println("【偵錯】第 ${attempt} 次嘗試，隨機取得未公開媒資名稱...")

	// 隨機讀取一筆未公開媒資名稱
	keyword1 = CustomKeywords.'custom.dbHelper.getRandomAssetName'(0,1)
	if (keyword1 == null || keyword1.isEmpty()) {
		println("【偵錯】取得的媒資名稱為空，跳過本次嘗試。")
		continue
	}

	// 進入未公開媒資頁
	WebUI.click(findTestObject('主畫面/未公開媒資'))

	if (WebUI.verifyElementPresent(findTestObject('主畫面/未公開媒資頁/搜尋媒資'),1,FailureHandling.OPTIONAL)) {
		WebUI.click(findTestObject('主畫面/未公開媒資頁/搜尋媒資'))
	}
	WebUI.click(findTestObject('主畫面/未公開媒資頁/輸入關鍵字'))
	WebUI.setText(findTestObject('主畫面/未公開媒資頁/輸入關鍵字'), keyword1)

	'search'
	WebUI.click(findTestObject('主畫面/未公開媒資頁/搜尋'))

	println('【偵錯】腳本開始，目標：尋找第一個『可點擊』送出按鈕對應的名稱 (依據 ID 規則)。')

	int maxButtons = 10
	for (int i = 1; i <= maxButtons; i++) {
		String buttonId = 'submit-asset-btn-' + i
		println("【偵錯】正在檢查 ID 為 '${buttonId}' 的按鈕...")

		TestObject currentButton = new TestObject(buttonId)
		currentButton.addProperty('id', ConditionType.EQUALS, buttonId)

		try {
			List<WebElement> foundButtons = WebUI.findWebElements(currentButton, 1)
			if (foundButtons.size() > 0) {
				WebElement button = foundButtons.get(0)
				if (button.isDisplayed() && button.isEnabled()) {
					println("【偵錯】步驟 2: 成功！找到第一個『可見』且『可點擊』的按鈕: ${buttonId}")
					try {
						println("【偵錯】準備相對定位...")
						String relativeXPathToName = "ancestor::tr[1]//td[4]/span"
						WebElement nameElement = button.findElement(By.xpath(relativeXPathToName))
						keyword1 = nameElement.getText().trim()
						println("【偵錯】步驟 3: 成功！找到對應名稱: '${keyword1}'")
						foundClickableButton = true
						break
					} catch (Exception e) {
						println("【偵錯】步驟 3: 失敗！找到按鈕但無法定位名稱欄位: " + e.toString())
						continue
					}
				} else {
					println("【偵錯】按鈕 '${buttonId}' 不可點擊 -> 可見: ${button.isDisplayed()}, 可用: ${button.isEnabled()}")
				}
			} else {
				println("【偵錯】ID 為 '${buttonId}' 的按鈕不存在於頁面上。")
			}
		} catch (Exception e) {
			println("【偵錯】尋找按鈕過程發生錯誤: " + e.toString())
			break
		}
	}

	if (foundClickableButton) {
		break
	} else {
		println("【偵錯】本次嘗試未找到可點擊的按鈕，準備換下一個媒資名稱。")
	}
}

println("【偵錯】腳本主要邏輯執行完畢。")
if (foundClickableButton && keyword1 != null && !keyword1.isEmpty()) {
	println("【最終結果】成功！ keyword1 的值為: '${keyword1}'")
} else {
	println("【最終結果】失敗！經過 ${maxRetry} 次嘗試，未能找到任何『可點擊』的送出按鈕或對應名稱。")
	keyword1 = ""
}

// ----------------------
// 後續流程：送出歸檔 + 呼叫測試案例
// ----------------------
String specifickeyword = keyword1
boolean testCaseExecuted = false

if (keyword1 != null && !keyword1.isEmpty()) {
	println("【偵錯】條件符合：keyword1 不為空。開始呼叫測試案例。")
	try {
		if (WebUI.verifyElementClickable(findTestObject('主畫面/未公開媒資頁/歸檔送出1'), FailureHandling.OPTIONAL)) {
			WebUI.waitForElementClickable(findTestObject('主畫面/未公開媒資頁/歸檔送出1'), 2)

			'點送出'
			WebUI.click(findTestObject('主畫面/未公開媒資頁/歸檔送出1'))

			WebUI.delay(2)

			WebUI.waitForElementClickable(findTestObject('主畫面/未公開媒資頁/送出歸檔頁/送出歸檔送出'), 2)

			'(送出歸檔)點送出'
			WebUI.click(findTestObject('主畫面/未公開媒資頁/送出歸檔頁/送出歸檔送出'))
		}

		WebUI.callTestCase(findTestCase('05BaseTest/歸檔送出核准'),
			[('accountParam') : account, ('keywordParam') : specifickeyword],
			FailureHandling.STOP_ON_FAILURE)

		println("【偵錯】測試案例「歸檔送出核准」已成功執行。")
		testCaseExecuted = true
	} catch (Exception e) {
		println("【錯誤】呼叫測試案例「歸檔送出核准」失敗: " + e.getMessage())
		testCaseExecuted = false
	}
} else {
	println("【偵錯】條件不符：keyword1 為空。跳過呼叫測試案例及後續所有動作。")
}

println("已完成 Case9 - 未公開媒資 (歸檔-審核-通過)")
WebUI.comment("已完成 Case9 - 未公開媒資 (歸檔-審核-通過)")
