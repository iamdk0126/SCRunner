import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import custom.WebUIExtensions

String keywordStr = keywordParam
String account = accountParam

// --- 改用 .toLong() 確保相容性 ---
long endTime = (endTimeParam != null) ? endTimeParam.toLong() : 0

WebUI.comment("【核准檢查】申請人:" + account + " ,關鍵字:"+ keywordStr)

// --- 1. 進入待審清單頁面 ---
WebUIExtensions.retryClick(findTestObject('Header/待審清單'))
WebUIExtensions.retryClick(findTestObject('檔案管理頁/回調媒資'))
WebUIExtensions.retrySetText(findTestObject('檔案管理頁/媒資歸檔頁/輸入申請人'), account)
WebUIExtensions.retrySetText(findTestObject('檔案管理頁/媒資歸檔頁/輸入關鍵字'), keywordStr)

// --- 2. 循環搜尋直到出現結果或超時 ---
boolean isFound = false
while (true) {
	long remaining = endTime - System.currentTimeMillis()
	
	// 再次確保使用 .toLong() 避免 BigDecimal 導致的 mod() 錯誤
	long totalSec = (remaining / 1000).toLong()
	long min = (totalSec / 60).toLong()
	long sec = (totalSec % 60).toLong()
	
	WebUI.comment("【核准等待中】剩餘時間：${String.format("%02d:%02d", min, sec)}")
	
	// 【優先檢查】若剩餘時間不到 30 秒，主動撤退
	if (remaining < 30000) {
		WebUI.comment("【時間警告】總執行時間將至，停止等待核准。")
		println("【警告】由於時間不足，放棄核准動作。")
		return false
	}

	WebUIExtensions.retryClick(findTestObject('檔案管理頁/媒資歸檔頁/搜尋'))
	WebUI.delay(5)

	// 檢查按鈕
	if (WebUI.verifyElementClickable(findTestObject('檔案管理頁/媒資歸檔頁/核准1'), FailureHandling.OPTIONAL)) {
		WebUI.comment("找到『核准』按鈕，可以進入核准流程。")
		isFound = true
		break
	}

	WebUI.comment("目前沒有資料，10 秒後再次搜尋。")
	WebUI.delay(10)
}

// --- 3. 執行退回點擊 ---
if (isFound) {
	WebUIExtensions.retryClick(findTestObject('檔案管理頁/媒資歸檔頁/退回1'))
	WebUIExtensions.retrySetText(findTestObject('檔案管理頁/媒資歸檔頁/退回輸入審核說明'), '這是輸入退回審核理由')
	WebUIExtensions.retryClickClose(findTestObject('檔案管理頁/媒資歸檔頁/退回審核說明退回'))
	WebUI.delay(2)
}

// --- 4. 清理現場 ---
WebUIExtensions.retryClick(findTestObject('Header/OPENMAM'))
WebUIExtensions.retryClick(findTestObject('主畫面/公開媒資'))
WebUIExtensions.retryClick(findTestObject('主畫面/公開媒資頁/重置'))
WebUIExtensions.retryClick(findTestObject('主畫面/公開媒資頁/搜尋'))

WebUI.comment('已完成呼叫 回調審核退回')
return true

