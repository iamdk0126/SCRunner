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

import java.util.Calendar

import internal.GlobalVariable

public class TimeControl {
	/**
	 * 檢查是否在禁止時間區間，如果是就暫停直到結束時間
	 * @param startHour 禁止開始小時 (0-23)
	 * @param startMinute 禁止開始分鐘 (0-59)
	 * @param endHour 禁止結束小時 (0-23)
	 * @param endMinute 禁止結束分鐘 (0-59)
	 */
	@Keyword
	static checkPauseTime(int startHour, int startMinute, int endHour, int endMinute) {
		Calendar now = Calendar.getInstance()
		int hour = now.get(Calendar.HOUR_OF_DAY)
		int minute = now.get(Calendar.MINUTE)

		// 轉換為「一天內的分鐘數」
		int currentMinutes = hour * 60 + minute
		int startMinutes = startHour * 60 + startMinute
		int endMinutes = endHour * 60 + endMinute

		if (currentMinutes >= startMinutes && currentMinutes < endMinutes) {
			println "⏸ 現在時間 ${hour}:${minute} 在禁止區間 (${startHour}:${startMinute}~${endHour}:${endMinute})，暫停執行..."

			int waitSeconds = (endMinutes - currentMinutes) * 60
			WebUI.delay(waitSeconds)

			println "▶️ 解除暫停，繼續執行。"
		}
	}
}
