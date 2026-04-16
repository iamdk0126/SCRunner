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
import com.kms.katalon.core.util.KeywordUtil as KeywordUtil
import internal.GlobalVariable as GlobalVariable
import java.time.LocalDate as LocalDate
import java.time.format.DateTimeFormatter as DateTimeFormatter
import org.openqa.selenium.Keys as Keys
import java.io.File as File
import java.io.IOException as IOException
import java.util.Random as Random
import java.io.FileFilter as FileFilter
import java.util.Map as Map
import java.util.HashMap as HashMap

// --- 1. 從外部參數設定總執行次數 ---
int totalLoopsToRun = indexParam
if (totalLoopsToRun <= 0) {
    KeywordUtil.markFailedAndStop("錯誤: 傳入的執行次數 (indexParam) 必須大於 0。")
}
WebUI.comment("--- 自動化腳本已啟動，目標執行次數: " + totalLoopsToRun + " ---")

// --- 2. 初始化計數器與隨機數產生器 ---
int executedLoops = 0
Random randomGenerator = new Random()

// --- 3. 建立迴圈，直到完成指定的執行次數 ---
while (executedLoops < totalLoopsToRun) {
    // --- 準備當天的日期與檔案路徑 ---
    LocalDate today = LocalDate.now()
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern('yyyyMMdd')
    String formattedDate = today.format(formatter)
    String videoIdFilePath = ('S:\\' + formattedDate) + '.txt'
    String allInfoFilePath = ('S:\\' + formattedDate) + '-air.all.txt' // Video ID 完整資訊檔案
    String usedVideoIdFilePath = ('S:\\' + formattedDate) + '_used.txt'
    
    File videoIdFile = new File(videoIdFilePath)
    File allInfoFile = new File(allInfoFilePath) // 創建 File 物件
    File usedVideoIdFile = new File(usedVideoIdFilePath)

    WebUI.comment("正在檢查今日的 ID 檔案: " + videoIdFilePath)

    // --- 檢查主要 Video ID 檔案是否存在且有內容 ---
    if (!videoIdFile.exists() || videoIdFile.length() == 0) {
        WebUI.comment("今日無可用 ID 或檔案不存在。腳本將在一小時後重試。")
        WebUI.delay(3600) // 等待一小時
        continue // 繼續 while 迴圈的下一次迭代
    }

    // --- 檢查並讀取 Video ID 和 Title 的對應檔案 ---
    Map<String, String> videoIdToTitleMap = new HashMap<String, String>()
    if (!allInfoFile.exists() || allInfoFile.length() == 0) {
        KeywordUtil.markWarning("警告: 找不到或檔案 '" + allInfoFilePath + "' 為空。將無法記錄 Video ID 對應的 Title。")
        // 腳本仍會繼續執行，只是無法記錄 Title
    } else {
        try {
            allInfoFile.readLines('UTF-8').each { line ->
                // 先 trim 移除行首行尾空白，然後用 Tab 鍵 '\t' 分割成兩部分：VideoID 和其餘的 Title
                def parts = line.trim().split('\t', 2)
                if (parts.length == 2) {
                    videoIdToTitleMap.put(parts[0].trim(), parts[1].trim())
                } else if (parts.length == 1 && !parts[0].trim().isEmpty()) {
                    // 如果只有 VideoID 而沒有 Title，也記錄下來，Title 為空字串
                    videoIdToTitleMap.put(parts[0].trim(), "")
                }
            }
            WebUI.comment("已成功讀取 Video ID 資訊檔案: " + allInfoFilePath)
        } catch (IOException e) {
            KeywordUtil.markWarning("讀取 Video ID 資訊檔案 '" + allInfoFilePath + "' 時發生錯誤: " + e.getMessage() + "。將無法記錄 Video ID 對應的 Title。")
        }
    }


    // --- 讀取待處理 Video ID ---
    List<String> allVideoIds
    try {
        allVideoIds = videoIdFile.readLines('UTF-8').findAll { !it.trim().isEmpty() }
        if (allVideoIds.isEmpty()) {
            WebUI.comment("待處理 ID 檔案內容為空，將在一小時後重試。")
            WebUI.delay(3600)
            continue
        }
    } catch (IOException e) {
        KeywordUtil.logInfo("讀取待處理 ID 檔案時發生錯誤: " + e.getMessage() + "，將在一小時後重試。")
        WebUI.delay(3600)
        continue
    }
    
    // --- 取得影片檔案 ---
    File videoDir = new File('D:\\20Clips')
    List<File> mxfFiles = videoDir.listFiles((({ def file ->
        file.isFile() && file.name.toLowerCase().endsWith('.mxf')
    }) as FileFilter)).toList()

    if (mxfFiles.isEmpty()) {
        KeywordUtil.markWarning('警告: 在目錄 D:\\20Clips\\ 中找不到任何 .MXF 檔案! 流程將暫停一小時。')
        WebUI.delay(3600)
        continue
    }

    // --- 4. 處理檔案中的第一個可用 ID ---
    String videoIdToProcess = allVideoIds.get(0)
    String specificVideoID = videoIdToProcess.trim()
    // 取得對應的 Title，如果沒有則為 "N/A"
    String correspondingTitle = videoIdToTitleMap.get(specificVideoID) ?: "N/A" 

    // 隨機選取一個 MXF 檔案
    File randomMxfFile = mxfFiles[randomGenerator.nextInt(mxfFiles.size())]
    String currentFilePath = randomMxfFile.getAbsolutePath()
    
    WebUI.comment("準備處理第 " + (executedLoops + 1) + " 筆任務。")
    WebUI.comment("使用 Video ID: " + specificVideoID)
    WebUI.comment("對應 Title: " + correspondingTitle) // 顯示對應的 Title
    WebUI.comment("隨機選擇檔案: " + currentFilePath)

    boolean isSuccess = false
    try {
        // --- UI 操作 ---
        '點新增媒資'
        WebUI.click(findTestObject('主畫面/新增媒資'))
        WebUI.click(findTestObject('主畫面/新增媒資頁/建立媒資'))
        '點模板'
        WebUI.click(findTestObject('主畫面/新增媒資頁/模板1'))
        WebUI.delay(2)
        '點建立'
        WebUI.click(findTestObject('主畫面/新增媒資頁/建立'))
        // 執行操作
        WebUI.setText(findTestObject('主畫面/編輯媒資頁/item1_videoid'), specificVideoID)
        WebUI.callTestCase(findTestCase('05BaseTest/編輯媒資分析VideoID選擇媒資分類'), [('VideoIDParam') : specificVideoID], FailureHandling.CONTINUE_ON_FAILURE)
        WebUI.delay(2)
        '點同步文稿'
        WebUI.click(findTestObject('主畫面/編輯媒資頁/同步文稿'))
        WebUI.delay(2)
        '執行上傳動作'
        WebUI.uploadFile(findTestObject('主畫面/新增媒資頁/上傳影片'), currentFilePath)
        WebUI.waitForElementVisible(findTestObject('主畫面/編輯媒資頁/主檔刪除'), 60, FailureHandling.OPTIONAL)
        '點儲存'
        WebUI.click(findTestObject('主畫面/編輯媒資頁/儲存'))
        WebUI.delay(2)
        
        WebUI.comment("ID: " + specificVideoID + " 上傳成功。")
        isSuccess = true

    } catch (Exception e) {
        KeywordUtil.markWarning("處理 ID: " + specificVideoID + " 時發生錯誤: " + e.getMessage())
        // 發生錯誤時，isSuccess 保持 false，不會更新計數器和檔案
    }

    // --- 5. 如果上傳成功，則更新計數器和 ID 檔案 ---
    if (isSuccess) {
        executedLoops++ // 成功完成一次，計數器加 1
        
        // 從列表中移除已使用的第一個 ID
        List<String> remainingIds = allVideoIds.drop(1)
        
        try {
            // 將剩下的 ID 寫回原始檔案
            videoIdFile.write(remainingIds.join('\n'), 'UTF-8')
            WebUI.comment("已成功處理 ID，並更新來源檔案。")

            // 將已使用的 ID 和 Title 寫入 _used.txt 檔案
            // 格式為 "VideoID,Title"
            usedVideoIdFile.append(specificVideoID + "," + correspondingTitle + '\n', 'UTF-8')
            WebUI.comment("已將 Video ID 和 Title: " + specificVideoID + ", " + correspondingTitle + " 記錄到 " + usedVideoIdFilePath)

        } catch (IOException e) {
            KeywordUtil.markWarning("更新 ID 檔案或寫入已使用 ID 檔案時發生錯誤: " + e.getMessage())
        }
    }
} // --- while 迴圈結束 ---

WebUI.comment("--- 自動化腳本執行完畢 ---")
println("已成功完成指定的 " + totalLoopsToRun + " 次上傳任務。")