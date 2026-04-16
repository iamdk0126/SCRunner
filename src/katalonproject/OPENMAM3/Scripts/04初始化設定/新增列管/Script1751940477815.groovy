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
import java.util.ArrayList as ArrayList
import custom.WebUIExtensions
import custom.DropdownHelper

String Team = teamParam
String CRName = Team + "CR"
String CRStartName = Team + "CRStartDate"
String CREndName = Team + "CREndDate"

// 1. 獲取 MediaType List
List StartDateList = GlobalVariable.metaClass.getProperty(GlobalVariable, CRStartName)
println(StartDateList)
List EndDateList = GlobalVariable.metaClass.getProperty(GlobalVariable, CREndName)
println(EndDateList)
List CRList = GlobalVariable.metaClass.getProperty(GlobalVariable, CRName)
println(CRList)
int count = StartDateList.size()

println('Total CopyRight in StartDateList: ' + count)

if (!(WebUI.waitForElementVisible(findTestObject('系統設定/使用者管理'), 2, FailureHandling.OPTIONAL))) {
	
	WebUI.callTestCase(findTestCase('05BaseTest/系統設定'), [:], FailureHandling.CONTINUE_ON_FAILURE)

}

if (!WebUI.waitForElementVisible(findTestObject('系統設定/列管資訊'), 2, FailureHandling.OPTIONAL)) {
	
	
		WebUI.click(findTestObject('系統設定/媒資設定'))
	}

WebUI.click(findTestObject('系統設定/列管資訊'))

WebUI.delay(2)

'指向第2個資源組別'
//WebUI.click(findTestObject('ResourceTeam/Team1'))
CustomKeywords.'custom.TeamKeywords.clickTeam'(Team)

WebUI.delay(2)

// 2. Create the for loop from 1 to count
for (int i = 1; i <= count; i++) {
    println('Loop iteration (1-indexed): ' + i)

    // 從 keyList 中獲取當前的 key (注意索引是 i-1)
    String currentCRStartDate = StartDateList.get(i - 1)

    String currentCREndDate = EndDateList.get(i - 1)

    WebUI.click(findTestObject('系統設定/列管資訊頁/新增項目'))
	
	WebUIExtensions.setTextByLabel('名稱', CRList[i-1])

    //WebUI.setText(findTestObject('系統設定/列管資訊頁/輸入列管資訊名稱'), CRList[i-1])

    WebUI.click(findTestObject('系統設定/列管資訊頁/清除開始日期'))

    WebUI.sendKeys(findTestObject('系統設定/列管資訊頁/輸入開始日期'), currentCRStartDate + Keys.ENTER)

    WebUI.delay(2)

    WebUI.click(findTestObject('系統設定/列管資訊頁/清除結束日期'))

    WebUI.sendKeys(findTestObject('系統設定/列管資訊頁/輸入結束日期'), currentCREndDate + Keys.ENTER)

    WebUI.delay(2)

    WebUI.click(findTestObject('系統設定/列管資訊頁/建立'))

    WebUI.delay(2)
}

println('Loop finished.')

