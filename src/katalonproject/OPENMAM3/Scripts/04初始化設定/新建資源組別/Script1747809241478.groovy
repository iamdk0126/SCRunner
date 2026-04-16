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

// 1. Calculate the count (number of items in ResourceTeam)
List resourceList = GlobalVariable.ResourceTeam

int count = resourceList.size()

println('Total items in ResourceTeam: ' + count)

// 2. Create the for loop from 1 to count
for (int i = 1; i <= count; i++) {
    println('Loop iteration: ' + i)

    // Access the item from the list. Since lists are 0-indexed,
    // and our loop variable 'i' is 1-indexed, we use 'i-1' for the index.
    String currentTeam = resourceList[(i - 1)]

    println('Processing team: ' + currentTeam)

    // --- Start of your repeatable test case actions ---
	WebUI.callTestCase(findTestCase('05BaseTest/系統設定'), [:], FailureHandling.CONTINUE_ON_FAILURE)

    // Might not need to repeat if already on page
    if (WebUI.waitForElementNotVisible(findTestObject('系統設定/資源組別'), 2)) {
        WebUI.click(findTestObject('系統設定/使用者設定'))
    }
    
    WebUI.click(findTestObject('系統設定/資源組別'))

    WebUI.click(findTestObject('Object Repository/ResourceTeam/新增資源組別'))

    // Use the currentTeam from the loop
    WebUI.setText(findTestObject('Object Repository/ResourceTeam/新增資源組別Name'), currentTeam)

    WebUI.click(findTestObject('Object Repository/ResourceTeam/新增資源組別建立')) // --- End of your repeatable test case actions ---
    // Optional: Add a small delay or verification step here
    // WebUI.delay(1)
    // WebUI.verifyElementPresent(findTestObject('Some_Object_Indicating_Success_For_' + currentTeam), 5)
    // If after adding one team, you need to go back to a starting point to add the next,
    // add those navigation steps here. For example:
    // WebUI.click(findTestObject('Link_To_Go_Back_To_Resource_Team_List_Or_Dashboard'))
}

println('Loop finished.')



WebUI.click(findTestObject('系統設定/平台登入'))

WebUI.check(findTestObject('系統設定/平台登入頁/啟用自動登出時間'))

