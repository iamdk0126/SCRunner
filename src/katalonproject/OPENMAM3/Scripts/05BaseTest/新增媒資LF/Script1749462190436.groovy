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
import java.io.File as File
import java.util.Random as Random
import java.io.FileFilter as FileFilter

// 2. 設定您的目標資料夾路徑
// 注意：在 Windows 路徑中，反斜線 '\' 需要寫成兩個 '\\'
String folderPath = GlobalVariable.LocalFolder

// 3. 建立資料夾物件並取得所有符合條件的檔案
File dir = new File(folderPath)

// 使用過濾器，只抓取所有結尾是 .MXF 的檔案 (忽略大小寫)
List<File> mxfFiles = dir.listFiles((({ def file ->
            file.name.toUpperCase().endsWith('.MXF')
        }) as FileFilter)).toList()

'點新增媒資'
WebUI.click(findTestObject('主畫面/新增媒資'))

WebUI.click(findTestObject('主畫面/新增媒資頁/建立媒資'))

'點模板'
WebUI.click(findTestObject('主畫面/新增媒資頁/模板1'))

WebUI.delay(2)

'點建立'
WebUI.click(findTestObject('主畫面/新增媒資頁/建立'))

WebUI.delay(2)

// 4. 檢查是否有找到任何 .MXF 檔案
if (mxfFiles.isEmpty()) {
    // 如果沒有找到檔案，就標記測試失敗並停止，避免後續步驟出錯
    WebUI.comment(('錯誤：在資料夾 ' + folderPath) + ' 中找不到任何 .MXF 檔案。')

    KeywordUtil.markFailedAndStop('指定的資料夾內找不到任何 .MXF 檔案。' // 5. 如果有找到檔案，就從檔案清單中隨機選取一個
        // 產生一個 0 到 (檔案數量-1) 之間的隨機索引
        ) // 根據隨機索引取得檔案
    // 6. 取得該隨機檔案的絕對路徑
    // (可選) 在日誌中印出選擇了哪個檔案，方便除錯
    // 7. 使用 WebUI.uploadFile 關鍵字上傳選中的檔案
    //    請將 'Page_YourPage/input_fileUpload' 換成您自己的測試物件 (Test Object)
    //WebUI.uploadFile(findTestObject('Page_YourPage/input_fileUpload'), randomFilePath)
} else {
    Random rand = new Random()

    int randomIndex = rand.nextInt(mxfFiles.size())

    File randomFile = mxfFiles[randomIndex]

    String randomFilePath = randomFile.getAbsolutePath()

    WebUI.comment('本次隨機選中的檔案是: ' + randomFilePath)

    WebUI.uploadFile(findTestObject('主畫面/新增媒資頁/上傳影片'), randomFilePath)

    WebUI.waitForElementVisible(findTestObject('主畫面/編輯媒資頁/主檔刪除'), 60, FailureHandling.OPTIONAL)
}

'點儲存'
WebUI.click(findTestObject('主畫面/編輯媒資頁/儲存'))

WebUI.delay(2)

println('已完成呼叫 新增Local一筆隨機媒資。')
WebUI.comment('已完成呼叫 新增Local一筆隨機媒資。')

