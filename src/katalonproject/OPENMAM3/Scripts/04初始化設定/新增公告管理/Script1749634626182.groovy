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

String Team = teamParam



int count = 2




if (!(WebUI.waitForElementVisible(findTestObject('系統設定/使用者管理'), 2, FailureHandling.OPTIONAL))) {
	
	WebUI.callTestCase(findTestCase('05BaseTest/系統設定'), [:], FailureHandling.CONTINUE_ON_FAILURE)

}



WebUI.click(findTestObject('系統設定/公告管理'))

WebUI.delay(2)

'指向第2個資源組別'
//WebUI.click(findTestObject('ResourceTeam/Team1'))
CustomKeywords.'custom.TeamKeywords.clickTeam'(Team)

// 2. Create the for loop from 1 to count
for (int i = 1; i <= count; i++) {
    println('Loop iteration (1-indexed): ' + i)

    
    // --- Start of your repeatable test case actions ---
    WebUI.click(findTestObject('系統設定/公告管理頁/新增公告'))

    // 使用從 Map 中取出的當前 Code 和 TypeDescription
    WebUI.setText(findTestObject('系統設定/公告管理頁/公告標題'), Team+"公告標題"+ "$i")

	WebUI.click(findTestObject('系統設定/公告管理頁/公告內容'))
	
	WebUI.sendKeys(findTestObject('系統設定/公告管理頁/公告內容'), "這是公告"+"$i"+"內容")

    WebUI.click(findTestObject('系統設定/公告管理頁/建立'))

    WebUI.delay(2)
	
	WebUI.click(findTestObject('系統設定/公告管理頁/上架'))
	
	WebUI.delay(5)
}

println('Loop finished.')

