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

// --- 接收參數 (使用 .toLong() 避免 IDE 解析錯誤) ---
String account = accountParam
String specifickeyword = keywordParam
long endTime = (endTimeParam != null) ? endTimeParam.toLong() : 0

// --- 時間檢查工具 (子程式內部使用) ---
def checkTime = { long targetEndTime, String stage ->
	long currentTime = System.currentTimeMillis()
	long remaining = targetEndTime - currentTime
	if (remaining < 0) remaining = 0
	
	// 使用 .toLong() 確保整數運算，防止 BigDecimal 導致的解析崩潰
	long totalSec = (remaining / 1000).toLong()
	long min = (totalSec / 60).toLong()
	long sec = (totalSec % 60).toLong()
	
	String timeStr = String.format("%02d:%02d", min, sec)
	println("【時間監控】階段：${stage}，剩餘時間：${timeStr}")
	WebUI.comment("剩餘時間：${timeStr} (${stage})")
	return remaining
}

// 1. 進入公開媒資頁前檢查時間
if (checkTime(endTime, "準備進入公開媒資") < 30000) {
	println("【警告】時間不足(剩餘不到30秒)，放棄回調申請。")
	return false
}

WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資'))

// 2. 執行搜尋
WebUI.callTestCase(findTestCase('05BaseTest/未公開搜尋媒資'), [('keywordParam') : specifickeyword], FailureHandling.STOP_ON_FAILURE)

// 3. 準備執行回調動作
if (checkTime(endTime, "準備點擊媒資回調") < 20000) {
	println("【警告】時間不足(剩餘不到20秒)，跳過回調動作。")
	return false
}

boolean result = WebUI.callTestCase(findTestCase('05BaseTest/未公開媒資送播'), [('keywordParam'):specifickeyword], FailureHandling.STOP_ON_FAILURE)

// 4. 確認回調清單 (最後確認步驟)
if (result) {
	if (checkTime(endTime, "回調成功，確認清單") > 10000) {
		WebUI.callTestCase(findTestCase('05BaseTest/送播清單'), [:], FailureHandling.STOP_ON_FAILURE)
	} else {
		println("【警告】時間不足，略過進入清單確認。")
	}
}

// --- 在 return 前印出完成訊息 ---
println('【完成】已完成: 未公開媒資送播')
WebUI.comment('已完成: 未公開媒資送播')

return result