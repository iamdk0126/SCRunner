import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import internal.GlobalVariable as GlobalVariable
import java.time.LocalDate as LocalDate
import java.time.format.DateTimeFormatter as DateTimeFormatter
import java.io.File as File
import java.io.IOException as IOException
import java.util.ArrayList as ArrayList

/*
// --- 在腳本開始時載入 S:\PublicList.txt 的 Title 到 GlobalVariable.UnlistedPublic ---
// 確保 GlobalVariable.UnlistedPublic 已在 Project Settings 中定義為 List<String> 類型，且預設值為 new ArrayList<String>()
if (GlobalVariable.UnlistedPublic == null || !(GlobalVariable.UnlistedPublic instanceof List)) {
	GlobalVariable.UnlistedPublic = new ArrayList<String>()
	WebUI.comment("GlobalVariable.UnlistedPublic 已初始化為空的 ArrayList。")
} else {
	// 清空現有的列表，確保每次運行都重新載入，避免重複數據
	GlobalVariable.UnlistedPublic.clear()
	WebUI.comment("GlobalVariable.UnlistedPublic 已清空，準備重新載入。")
}

// 獲取當前日期以構建檔案路徑 (此處日期已不再用於檔案名，但保留無妨)
LocalDate today = LocalDate.now()
DateTimeFormatter formatter = DateTimeFormatter.ofPattern('yyyyMMdd')
String formattedDate = today.format(formatter)
String usedVideoIdFilePath = ('S:\\PublicList.txt')
File usedVideoIdFileForLoading = new File(usedVideoIdFilePath) // 使用不同的變數名以區分後續的寫入操作

WebUI.comment("嘗試從 '" + usedVideoIdFilePath + "' 載入已處理的 Title 到 GlobalVariable.UnlistedPublic。")

if (usedVideoIdFileForLoading.exists() && usedVideoIdFileForLoading.length() > 0) {
	try {
		usedVideoIdFileForLoading.readLines('UTF-8').each { line ->
			// 如果檔案現在只儲存 Title，則直接將每行內容作為 Title 添加
			String title = line.trim()
			if (!title.isEmpty()) { // 確保不是空行
				GlobalVariable.UnlistedPublic.add(title)
			}
		}
		WebUI.comment("已成功載入 " + GlobalVariable.UnlistedPublic.size() + " 個 Title 到 GlobalVariable.UnlistedPublic。")
		println("載入的 Titles: " + GlobalVariable.UnlistedPublic) // 更好地顯示載入的內容

	} catch (IOException e) {
		KeywordUtil.markWarning("載入檔案 '" + usedVideoIdFilePath + "' 時發生錯誤: " + e.getMessage() + "。")
	}
} else {
	WebUI.comment("檔案 '" + usedVideoIdFilePath + "' 不存在或為空，GlobalVariable.UnlistedPublic 將保持為空。")
}
// --- 載入 GlobalVariable.UnlistedPublic 結束 ---
*/
// 您想測試的特定帳號
//使用無列管公開媒資全域參數 List
List<String> unlistedPublicList = GlobalVariable.UnlistedPublic

String specifickeyword

if ((unlistedPublicList != null) && !(unlistedPublicList.isEmpty())) {
    Random random = new Random()

    int randomIndex = random.nextInt(unlistedPublicList.size())

    specifickeyword = unlistedPublicList.get(randomIndex)

    println('本次使用的關鍵字為： ' + specifickeyword)

    WebUI.comment('本次使用的關鍵字為： ' + specifickeyword // 也輸出到報告
        // 如果列表為空或為 null，可以設定一個預設值，或進行錯誤處理
        )
} else {
	
	if (GlobalVariable.DenyRestoreSameFile) {
		specifickeyword = CustomKeywords.'custom.dbHelper.getRandomAssetNameNotRestoreWithoutCopyright'(1, GlobalVariable.metateam)
	} else {
		specifickeyword = CustomKeywords.'custom.dbHelper.getRandomAssetNameWithoutCopyright'(1, GlobalVariable.metateam)
	}
    println('GlobalVariable.UnlistedPublic 列表為空或未設定，由db取得關鍵字：' + specifickeyword)

    WebUI.comment('GlobalVariable.UnlistedPublic 列表為空或未設定，由db取得關鍵字：' + specifickeyword)
}

return specifickeyword

WebUI.comment('完成選擇公開無列管媒資關鍵字')

