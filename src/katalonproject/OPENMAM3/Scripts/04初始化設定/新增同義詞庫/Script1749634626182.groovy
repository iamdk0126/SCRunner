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
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import java.util.ArrayList as ArrayList
import custom.WebUIExtensions

String BASE_DRIVE_S = GlobalVariable.BASE_DRIVE_S

String Team = teamParam


// --- 導航步驟 (維持不變) ---
//WebUI.click(findTestObject('Header/個人頭像'))

//WebUI.click(findTestObject('Header/個人頭像下拉選單/系統設定'), FailureHandling.OPTIONAL)
WebUI.callTestCase(findTestCase('05BaseTest/系統設定'), [:], FailureHandling.CONTINUE_ON_FAILURE)

if (!WebUI.waitForElementVisible(findTestObject('系統設定/同義詞庫'), 2, FailureHandling.OPTIONAL)) {
	
	if (!WebUI.waitForElementVisible(findTestObject('系統設定/媒資搜尋'), 2, FailureHandling.OPTIONAL)) {
		
			WebUI.click(findTestObject('系統設定/媒資設定'))
		}
	
		WebUI.click(findTestObject('系統設定/媒資搜尋'))
	}

WebUI.click(findTestObject('系統設定/同義詞庫'))



WebUI.delay(2)

'指向第2個資源組別'
//WebUI.click(findTestObject('ResourceTeam/Team1'))
CustomKeywords.'custom.TeamKeywords.clickTeam'(Team)

// 動態輸入框生成
TestObject makeInput(int index) {
    TestObject to = new TestObject("input_" + index)
    to.addProperty("id", ConditionType.EQUALS, "options_" + index)
    return to
}

// 讀取 CSV
String csvPath = "${BASE_DRIVE_S}/news_synonyms.csv"

// 確認檔案存在
File f = new File(csvPath)

// 讀取所有行（UTF-8）
List<String> lines = f.readLines("UTF-8")

for (int r = 0; r < lines.size(); r++) {

    // 空行跳過
    if (lines[r].trim().isEmpty()) {
        continue
    }

    // 開啟新增頁面（依你的 UI）
    WebUI.click(findTestObject('系統設定/同義詞庫頁/新增詞彙'))

    // CSV 分割
    List<String> values = lines[r].split(",")

    for (int c = 0; c < values.size(); c++) {
        String word = values[c].trim()

        if (word == "") continue

        // 第三個開始要按「增加詞彙」
        if (c >= 2) {
            WebUI.click(findTestObject('系統設定/同義詞庫頁/增加詞彙'))
            WebUI.delay(0.2)
        }

        TestObject inputField = makeInput(c)
        WebUI.setText(inputField, word)
    }

    // 按建立
    WebUI.click(findTestObject('系統設定/同義詞庫頁/建立'))
    WebUI.delay(0.8)
}

println('Loop finished.')

