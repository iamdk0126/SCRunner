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

String Team = teamParam
String TextTypeName = "WarningCard"

// 1. 獲取 TextType Map
//Map textTypeMap = GlobalVariable.Team1TextType // 改名並確認類型是 Map
Map textTypeMap = GlobalVariable.metaClass.getProperty(GlobalVariable, TextTypeName) // 改名並確認類型是 Map

int count = textTypeMap.size()

println('Total items in TextType: ' + count)

// 將 Map 的鍵轉換為 List
List<String> keyList = new ArrayList(textTypeMap.keySet())

if (!(WebUI.waitForElementVisible(findTestObject('系統設定/使用者管理'), 2, FailureHandling.OPTIONAL))) {
	
	WebUI.callTestCase(findTestCase('05BaseTest/系統設定'), [:], FailureHandling.CONTINUE_ON_FAILURE)

}

// --- 2. 前置導航操作 (保持原樣) ---
if (!WebUI.waitForElementVisible(findTestObject('系統設定/其他媒資設定'), 2, FailureHandling.OPTIONAL)) {
	
	if (!WebUI.waitForElementVisible(findTestObject('系統設定/媒資資訊'), 2, FailureHandling.OPTIONAL)) {
		
			WebUI.click(findTestObject('系統設定/媒資設定'))
		}
	
		WebUI.click(findTestObject('系統設定/媒資資訊'))
	}

WebUI.click(findTestObject('系統設定/其他媒資設定'))

WebUI.delay(2)

'指向第1個資源組別'
//WebUI.click(findTestObject('ResourceTeam/Team1'))
CustomKeywords.'custom.TeamKeywords.clickTeam'(Team)

// 2. Create the for loop from 1 to count
for (int i = 1; i <= count; i++) {
    println('Loop iteration (1-indexed): ' + i)

    // 從 keyList 中獲取當前的 key (注意索引是 i-1)
    String currentCode = keyList.get(i - 1)

    // 從 map 中根據 key 獲取對應的 value
    String currentTypeDescription = textTypeMap.get(currentCode)

    println((('Processing Code: ' + currentCode) + ', Type: ') + currentTypeDescription)

    // --- Start of your repeatable test case actions ---
    WebUI.click(findTestObject('系統設定/其他媒資設定頁/警示卡設定頁/新增'))

    // 使用從 Map 中取出的當前 Code 和 TypeDescription
	WebUIExtensions.setTextByLabel('選項名稱', currentCode)
	
	WebUIExtensions.setTextByLabel('備註文字', currentTypeDescription)

    WebUI.click(findTestObject('系統設定/其他媒資設定頁/警示卡設定頁/儲存'))

    WebUI.delay(2)
}

println('Loop finished.')

