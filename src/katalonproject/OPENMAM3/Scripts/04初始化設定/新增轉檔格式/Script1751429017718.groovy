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
import custom.DropdownHelper

String Team = teamParam

// 獲取 MediaType List
List CharNameList = GlobalVariable.CharName
int count = CharNameList.size()
println('Total items in CharName: ' + count)

// 確保進入 系統設定 頁面
if (!(WebUI.waitForElementVisible(findTestObject('系統設定/使用者管理'), 2, FailureHandling.OPTIONAL))) {
    WebUI.callTestCase(findTestCase('05BaseTest/系統設定'), [:], FailureHandling.CONTINUE_ON_FAILURE)
}

// 進入回調位置
if (!(WebUI.waitForElementVisible(findTestObject('系統設定/回調設定'), 2, FailureHandling.OPTIONAL))) {
    WebUI.click(findTestObject('系統設定/回調送播'))
}
WebUI.click(findTestObject('系統設定/回調設定'))
WebUI.delay(2)

// 指向第1個資源組別
CustomKeywords.'custom.TeamKeywords.clickTeam'(Team)
WebUI.delay(2)

// internal_vantage === 迴圈跑所有組合 (2 format × 4 resolution × 2 watermark) ===
for (String format in GlobalVariable.transform_format) {
    for (String resolution in GlobalVariable.transform_resolution) {
        for (boolean watermark : [true, false]) {

            WebUI.click(findTestObject('系統設定/回調位置頁/新增轉檔格式'))
			
			DropdownHelper.selectDropdownOptionByLabel('轉檔機','internal_vantage')
			
			WebUIExtensions.setTextByLabel('Bitrate', '10')
			
			WebUIExtensions.setTextByLabel('格式', format)
			
			if (GlobalVariable.DOMAIN == 'OPEN') {WebUIExtensions.setTextByLabel('Workflow ID', GlobalVariable.WorkflowID)}
			   else if (GlobalVariable.DOMAIN == 'PTS') {WebUIExtensions.setTextByLabel('Workflow ID', '38f5f71c-1996-49d1-93c1-cf2692232717')}
								
			/*WebUIExtensions.setTextByLabel('節目名稱', correspondingTitle)

            WebUI.click(findTestObject('系統設定/回調位置頁/新增轉檔格式頁/選擇格式'))
            CustomKeywords.'custom.DropdownHelper.selectVisibleDropdownOptionScroll'(format)

            WebUI.click(findTestObject('系統設定/回調位置頁/新增轉檔格式頁/選擇解析度'))
            CustomKeywords.'custom.DropdownHelper.selectVisibleDropdownOptionScroll'(resolution)
*/
			DropdownHelper.selectDropdownOptionByLabel('解析度',resolution)
			
            if (watermark) {
                WebUI.check(findTestObject('系統設定/回調位置頁/新增轉檔格式頁/添加浮水印'))
            } else {
                WebUI.uncheck(findTestObject('系統設定/回調位置頁/新增轉檔格式頁/添加浮水印'))
            }

            WebUI.click(findTestObject('系統設定/回調位置頁/新增轉檔格式頁/建立'))
            WebUI.delay(2)

            println("新增轉檔組合完成: format=${format}, resolution=${resolution}, watermark=${watermark}")
			WebUI.comment("新增轉檔組合完成: format=${format}, resolution=${resolution}, watermark=${watermark}")
        }
    }
}
/*
// external_vantage 


			WebUI.click(findTestObject('系統設定/回調位置頁/新增轉檔格式'))
			
			DropdownHelper.selectDropdownOptionByLabel('轉檔機','external_vantage')
			
			WebUIExtensions.setTextByLabel('Bitrate', '10')
			
			WebUIExtensions.setTextByLabel('格式', 'External')
			
			WebUIExtensions.setTextByLabel('Workflow ID', GlobalVariable.WorkflowID_external)
								
			WebUI.click(findTestObject('系統設定/回調位置頁/新增轉檔格式頁/建立'))
			WebUI.delay(2)

			println("新增轉檔 external vantage 完成")
			WebUI.comment("新增轉檔 external vantage 完成")

// external_vantage
			
			
WebUI.click(findTestObject('系統設定/回調位置頁/新增轉檔格式'))
						
DropdownHelper.selectDropdownOptionByLabel('轉檔機','external_amberfin')
						
WebUIExtensions.setTextByLabel('Bitrate', '10')
						
WebUIExtensions.setTextByLabel('格式', 'External')

WebUIExtensions.setTextByLabel('Input Watchfolder 位置', 'Inputs')

WebUIExtensions.setTextByLabel('Output Watchfolder 位置', 'Outputs')

											
WebUI.click(findTestObject('系統設定/回調位置頁/新增轉檔格式頁/建立'))
WebUI.delay(2)
			
println("新增轉檔 external amberfin 完成")
WebUI.comment("新增轉檔 external amberfin 完成")

			*/

// 上傳浮水印

if (GlobalVariable.Watermark) {
	WebUI.callTestCase(findTestCase('05BaseTest/上傳浮水印'), [:], FailureHandling.OPTIONAL)
}
println('轉檔設定完成.')
WebUI.comment('轉檔設定完成.')
