import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.util.KeywordUtil as KeywordUtil
import internal.GlobalVariable as GlobalVariable
import java.io.File as File

// --- 使用 GlobalVariable 定義的目錄 ---
String VIDEO_DIR_D = GlobalVariable.LocalFolder

String filePath = VIDEO_DIR_D + '\\logo.png'

// --- 檢查檔案是否存在 ---
File logoFile = new File(filePath)

if (!(logoFile.exists())) {
    KeywordUtil.markFailedAndStop('找不到檔案: ' + filePath)
}

// --- 上傳檔案 ---
WebUI.comment('準備上傳檔案: ' + filePath)

WebUI.uploadFile(findTestObject('系統設定/回調位置頁/更換浮水印'), filePath)

WebUI.comment('檔案上傳完成。')

