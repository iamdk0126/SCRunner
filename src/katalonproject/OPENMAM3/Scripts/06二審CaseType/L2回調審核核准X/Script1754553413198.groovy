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

//String keywordStr = keywordParam // 使用在 'Variables' 頁籤定義的變數

String account = accountParam

int executionTimeInMinutes = GlobalVariable.executionTimeInhours * 60

'點檔案管理'
WebUI.click(findTestObject('Header/待審清單'))

WebUI.click(findTestObject('檔案管理頁/回調媒資'))

//WebUI.setText(findTestObject('檔案管理頁/媒資歸檔頁/輸入申請人'), account)

//WebUI.setText(findTestObject('檔案管理頁/媒資歸檔頁/輸入關鍵字'), keywordStr)

if (executionTimeInMinutes > 0) {
	// 依執行時間執行
	println('腳本將依據設定的執行時間 (' + executionTimeInMinutes + ' 分鐘) 執行。')
	def startTime = System.currentTimeMillis()
	def endTime = startTime + (executionTimeInMinutes * 60 * 1000)
	int iterationCount = 0



		while (true) {
			WebUI.click(findTestObject('檔案管理頁/媒資歸檔頁/搜尋'))
			WebUI.comment("已點擊搜尋，等待結果...")

			// 檢查「全部勾選」是否可點擊 (給 5 秒等待時間)
			if (WebUI.waitForElementClickable(findTestObject('檔案管理頁/媒資歸檔頁/勾選1'), 5, FailureHandling.OPTIONAL)) {
				WebUI.comment("找到『勾選1』按鈕，可以進入核准流程。")
				break
			}

    // 沒找到 → 等 10 秒再繼續
			WebUI.comment("目前沒有資料，10 秒後再次搜尋。")
			WebUI.delay(10)
			if (System.currentTimeMillis() > endTime) {
				break
			}		
			
		}

		if (WebUI.waitForElementClickable(findTestObject('檔案管理頁/媒資歸檔頁/勾選1'), 5, FailureHandling.OPTIONAL)) {
			WebUI.click(findTestObject('檔案管理頁/媒資歸檔頁/勾選1'))
		}

		if (WebUI.waitForElementClickable(findTestObject('檔案管理頁/媒資歸檔頁/核准1'), 5, FailureHandling.OPTIONAL)) {
	
			WebUI.click(findTestObject('檔案管理頁/媒資歸檔頁/核准1'))

			WebUI.setText(findTestObject('檔案管理頁/媒資歸檔頁/核准輸入審核說明'), '這是輸入審核同意理由')

			WebUI.click(findTestObject('檔案管理頁/媒資歸檔頁/核准審核說明核准'))
		}

		if (!WebUI.waitForElementClickable(findTestObject('檔案管理頁/媒資歸檔頁/核准輸入審核說明'), 5, FailureHandling.OPTIONAL)) {
	
			WebUI.click(findTestObject('檔案管理頁/媒資歸檔頁/核准審核說明核准'))
		}

	}

WebUI.delay(2)

'openmam logo'
if (WebUI.waitForElementClickable(findTestObject('Header/OPENMAM'), 10,FailureHandling.OPTIONAL)) {
	WebUI.click(findTestObject('Header/OPENMAM'))
}

WebUI.delay(2)

println('已完成呼叫 回調審核核准')
WebUI.comment('已完成呼叫 回調審核核准')

