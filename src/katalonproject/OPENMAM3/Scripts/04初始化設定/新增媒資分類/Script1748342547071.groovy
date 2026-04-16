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
String MediaTypeName = Team + "MediaType"


// 1. 獲取 MediaType List
//List MediaTypeList = GlobalVariable.Team1MediaType
List MediaTypeList = GlobalVariable.metaClass.getProperty(GlobalVariable, MediaTypeName)

int count = MediaTypeList.size()

println('Total items in MediaType: ' + count)

//WebUIExtensions.clickWithTrigger(findTestObject('系統設定/使用者管理'),findTestObject('05BaseTest/系統設定'))

if (!(WebUI.waitForElementVisible(findTestObject('系統設定/使用者管理'), 2, FailureHandling.OPTIONAL))) {
	
	WebUI.callTestCase(findTestCase('05BaseTest/系統設定'), [:], FailureHandling.CONTINUE_ON_FAILURE)

}

//WebUIExtensions.clickWithTrigger(findTestObject('系統設定/媒資分類'),findTestObject('系統設定/媒資設定'))

if (!WebUI.waitForElementVisible(findTestObject('系統設定/媒資分類'), 2, FailureHandling.OPTIONAL)) {
	
		WebUI.click(findTestObject('系統設定/媒資設定'))
	}

WebUI.click(findTestObject('系統設定/媒資分類'))

WebUI.delay(2)

'指向第?個資源組別'
//WebUI.click(findTestObject('ResourceTeam/Team1'))
CustomKeywords.'custom.TeamKeywords.clickTeam'(Team)

WebUI.delay(2)

// 2. Create the for loop from 1 to count
for (int i = 1; i <= count; i++) {
    println('Loop iteration (1-indexed): ' + i)

    // 從 keyList 中獲取當前的 key (注意索引是 i-1)
    String currentType = MediaTypeList.get(i - 1)

    println('Processing MediaType: ' + currentType)

    WebUI.click(findTestObject('系統設定/新增媒資分類'))

    // --- Start of your repeatable test case actions ---
    WebUI.click(findTestObject('系統設定/新增媒資分類名稱'))

    // 使用從 Map 中取出的當前 Code 和 TypeDescription
    WebUI.setText(findTestObject('系統設定/新增媒資分類名稱'), currentType)

    WebUI.click(findTestObject('系統設定/新增媒資分類建立'))
	// WebUI.verifyElementPresent(findTestObject('Some_Object_Indicating_Success_For_' + currentTeam), 5)
	// If after adding one team, you need to go back to a starting point to add the next,
	// add those navigation steps here. For example:
	// WebUI.click(findTestObject('Link_To_Go_Back_To_Resource_Team_List_Or_Dashboard'))
	
    WebUI.delay(2)
}

println('Loop finished.')

