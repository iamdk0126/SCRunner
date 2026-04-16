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

// [改動 1] 讀取 Map 並將其 "鍵" 轉換為 List 以便在 for 迴圈中使用
def templateMap = GlobalVariable.Team1TemplateMap

String tapeParam = "公視"

List keyList = new ArrayList(templateMap.keySet( // 取得所有 key 並放入 List
    ))

int count = keyList.size( // count 現在是 key 的數量
    )

println('Total items in TemplateMap: ' + count)

// --- 導航步驟 (維持不變) ---
WebUI.click(findTestObject('Header/個人頭像'))

WebUI.click(findTestObject('Header/個人頭像下拉選單/系統設定'), FailureHandling.OPTIONAL)
/*
WebUI.click(findTestObject('系統設定/媒資設定'))

WebUI.click(findTestObject('系統設定/使用者設定'))

WebUI.click(findTestObject('系統設定/使用者管理'))

WebUI.click(findTestObject('系統設定/媒資設定'))

WebUI.click(findTestObject('系統設定/媒資資訊'))
*/
if (!WebUI.waitForElementVisible(findTestObject('系統設定/預設模板'), 2, FailureHandling.OPTIONAL)) {
	
	if (!WebUI.waitForElementVisible(findTestObject('系統設定/媒資資訊'), 2, FailureHandling.OPTIONAL)) {
		
			WebUI.click(findTestObject('系統設定/媒資設定'))
		}
	
		WebUI.click(findTestObject('系統設定/媒資資訊'))
	}

WebUI.click(findTestObject('系統設定/預設模板'))

WebUI.delay(2)

'指向第2個資源組別'
WebUI.click(findTestObject('ResourceTeam/Team1'))

WebUI.delay(2)

// --- for 迴圈 (結構維持不變) ---
for (int i = 1; i <= count; i++) {
    println('Loop iteration (1-indexed): ' + i)

    // [改動 2] 從 keyList 中獲取當前的 key，並用它從 map 中找到對應的 value
    String currentType = keyList.get(i - 1 // currentType 現在是 Map 的 key (例如: '社會新聞')
        )

    def specificvalue = templateMap[currentType // specificvalue 是對應的 value (例如: '社會C')
    ]

    println('Processing Key (currentType): ' + currentType)

    println('Processing Value (specificvalue): ' + specificvalue)

    WebUI.click(findTestObject('系統設定/新增預設模板'))

    // --- 使用 key 和 value 執行操作 ---
    WebUI.click(findTestObject('系統設定/新增預設模板命名'))

    WebUI.setText(findTestObject('系統設定/新增預設模板命名'), currentType) // 使用 key 來命名
        
    WebUI.setText(findTestObject('新增模板/Page_/新增模板名稱'), '新聞名稱必填')
	
	WebUI.click(findTestObject('新增模板/Page_/div__ant-select-selector'))
	
	CustomKeywords.'custom.DropdownHelper.selectVisibleDropdownOptionScroll'(tapeParam)
	

	WebUI.click(findTestObject('新增模板/Page_/新增預設模板媒資分類下拉'))
	
	CustomKeywords.'custom.DropdownHelper.selectVisibleDropdownOptionScroll'(specificvalue)
	
	WebUI.delay(2)
	

    WebUI.click(findTestObject('系統設定/新增媒資分類建立'))

    WebUI.delay(2)
}

println('Loop finished.')

