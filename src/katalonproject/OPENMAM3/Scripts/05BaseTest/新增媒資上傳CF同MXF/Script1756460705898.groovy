import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.util.KeywordUtil as KeywordUtil
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.testobject.ConditionType as ConditionType
import com.kms.katalon.core.testobject.TestObject as TestObject
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.WebElement as WebElement
import java.time.LocalDate as LocalDate
import java.time.format.DateTimeFormatter as DateTimeFormatter
import java.util.Random as Random
import java.io.File as File
import java.sql.Connection as Connection
import java.sql.DriverManager as DriverManager
import java.sql.ResultSet as ResultSet
import java.sql.Statement as Statement
import java.util.ArrayList as ArrayList
import java.util.List as List
import java.io.FileWriter as FileWriter
import java.io.RandomAccessFile
import java.nio.ByteBuffer  // 新增：處理 UUID 轉換
import java.util.UUID      // 新增：產生唯一識別碼
import java.nio.file.*
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import custom.TimeControl as TimeControl
import custom.XmlReader as XmlReader
import custom.BrowserHelper
import custom.WebUIExtensions
import custom.TimecodeHelper
import custom.DropdownHelper
import custom.AntMultiSelect
import custom.MxfManager

// --- 常數設定 ---
String BASE_DRIVE_S = GlobalVariable.BASE_DRIVE_S
String VIDEO_DIR_D = GlobalVariable.LocalFolder
String specificVideoID = ""
String correspondingTitle = ""
String currentFilePath = ""
String subtitleFilePath = ""
String VideoID = videoParam
String Title = titleParam
String myRobotName = titleParam
List<Map<String, Object>> intervalList

// 資料庫路徑設定
String dbFile = GlobalVariable.CFDB_Folder + "/mxf_tasks.db"
MxfManager.setDbPath(dbFile)

// 1. 從 DB 領取任務 (status 0 -> 1)
def job = MxfManager.grabNextJob(myRobotName)
int currentSerial = MxfManager.getNextSerialNumber() 

String progtype = progParam
long ONE_HOUR_IN_SECONDS = 3600
int elapsedSeconds = 0
int maxWait = 7200 // 2 小時
int checkInterval = 30 

int randomX = (Title != '看公視說英語') ? (1000 + indexParam) : (8000 + indexParam)

TimeControl.checkPauseTime(0, 0, 0, 20) 

LocalDate today = LocalDate.now()
DateTimeFormatter formatter = DateTimeFormatter.ofPattern('yyyyMMdd')
String formattedDate = today.format(formatter)
String xmlFilePath = "${BASE_DRIVE_S}${formattedDate}-air.all.xml"

WebUI.comment('正在檢查今日的 Video ID 檔案: ' + xmlFilePath)

def result = CustomKeywords.'custom.XmlReader.getRandomVideoAndTitle'(xmlFilePath)
if (result == null) {
    WebUI.comment('XML 無可用資料，將在30Sec後重試。')
    WebUI.delay(30)
    return
} else {
    specificVideoID = result[0]
    correspondingTitle = result[1]
    WebUI.comment("抽到可用 Video ID: $specificVideoID, Title: $correspondingTitle")
}

if (GlobalVariable.metateam == 1) {
    correspondingTitle = progtype +"-"+correspondingTitle
} else if (GlobalVariable.metateam == 2) {
    correspondingTitle = progtype +"-"+ correspondingTitle 
}

// --- DB 檢查是否已存在 (MySQL) ---
boolean existsInDb = false
Connection conn = null
try {
    Class.forName('com.mysql.cj.jdbc.Driver')
    conn = DriverManager.getConnection("jdbc:mysql://${GlobalVariable.mysqlip}:3306/${GlobalVariable.mysqldbname}?serverTimezone=UTC&useSSL=false", GlobalVariable.mysqluser, GlobalVariable.mysqlpw)
    Statement stmt = conn.createStatement()
    ResultSet rs = stmt.executeQuery("SELECT 1 FROM assets WHERE asset_name = '${correspondingTitle}' LIMIT 1")
    existsInDb = rs.next()
} catch (Exception e) {
    e.printStackTrace()
} finally {
    if (conn) conn.close()
}

if (existsInDb) {
    WebUI.comment("Title 已存在資料庫，延遲30Sec再重試")
    WebUI.delay(30)
    return
}

try {
    // --- UI 操作 ---
    if (WebUI.verifyElementClickable(findTestObject('主畫面/新增媒資'), FailureHandling.OPTIONAL)) {
        WebUI.click(findTestObject('主畫面/新增媒資'))
    } else {
        return
    }
    
    WebUI.delay(2)
    if (WebUI.verifyElementClickable(findTestObject('主畫面/新增媒資頁/iNews匯入'), FailureHandling.OPTIONAL)) {
        WebUIExtensions.retryClick(findTestObject('主畫面/新增媒資頁/建立媒資'))
    }
    
    WebUIExtensions.clickTableTitle(Title)
    WebUI.delay(2)
    WebUI.click(findTestObject('主畫面/新增媒資頁/建立'))
    
    if (job != null && currentSerial != -1) {
        int dbId = job[0]
        String mxfPath = job[1]
        KeywordUtil.logInfo("成功領取 DB 任務 [ID: ${dbId}]，路徑: ${mxfPath}")
        
        File fileObj = new File(mxfPath)
        String folderPath = fileObj.getParent() + File.separator
        String originalName = fileObj.getName()
        String serialName = "upload_" + String.format("%04d", currentSerial) + "_" + originalName
        Path originalPath = Paths.get(mxfPath)
        Path targetPath = Paths.get(folderPath + serialName)
    
        // 重要變數：記錄檔案原始長度用於還原
        long originalFileSize = -1

        WebUI.delay(2)
        WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/輸入Title'))
        WebUI.delay(2)
        WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/輸入Title'), Keys.chord(Keys.CONTROL,'a'))
        WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/輸入Title'), Keys.chord(Keys.BACK_SPACE))
        WebUIExtensions.setTextByLabel('媒資名稱', correspondingTitle)
        
        if (GlobalVariable.metateam == 2) {
            DropdownHelper.selectDropdownOptionByLabel('類型',progtype)
            DropdownHelper.selectDropdownOptionByLabel('短帶類別',"宣傳帶")
        }

        WebUI.delay(2)
        WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/儲存'))
        WebUI.delay(2)
        WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資'))
        WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/搜尋媒資'))
        WebUIExtensions.setTextByLabel('關鍵字', correspondingTitle)
        WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/搜尋'))

        if (WebUI.verifyElementClickable(findTestObject('主畫面/未公開媒資頁/編輯媒資1'), FailureHandling.OPTIONAL)) {
            WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/編輯媒資1'))
            WebUI.delay(2)

            try {
                // A. 改名
                Files.move(originalPath, targetPath, StandardCopyOption.REPLACE_EXISTING)
        
                // B. UUID 修改 SHA256 (追加 16 bytes)
                RandomAccessFile raf = new RandomAccessFile(targetPath.toFile(), "rw")
                try {
                    originalFileSize = raf.length()
                    UUID uuid = UUID.randomUUID()
                    ByteBuffer bb = ByteBuffer.wrap(new byte[16])
                    bb.putLong(uuid.getMostSignificantBits())
                    bb.putLong(uuid.getLeastSignificantBits())
                    
                    raf.seek(originalFileSize) // 跳至末尾
                    raf.write(bb.array())      // 追加隨機 UUID 
                    KeywordUtil.logInfo("SHA256 已變更 (UUID 追加)，原始長度: ${originalFileSize}")
                } finally {
                    raf.close()
                }
                
                WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/video'))
                WebUI.uploadFile(findTestObject('主畫面/編輯媒資頁/upload_video'), targetPath.toString())

                KeywordUtil.logInfo("長效上傳開始...")
                while (elapsedSeconds < maxWait) {
                    boolean stillUploading = WebUI.verifyElementPresent(findTestObject('主畫面/編輯媒資頁/上傳中取消'), 5, FailureHandling.OPTIONAL)
                    if (!stillUploading) {
                        KeywordUtil.logInfo("偵測到上傳完成！")
                        WebUI.delay(5)
                        WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/radio1'))
                        break
                    }
                    elapsedSeconds += checkInterval
                    WebUI.delay(checkInterval)
                }
                
                if (elapsedSeconds >= maxWait) {
                    KeywordUtil.markError("檔案上傳超過 2 小時，強制中斷。")
                }
                
            } catch (Exception e) {
                KeywordUtil.markError("上傳過程發生異常: " + e.message)
            } finally {
                // --- 還原處理 ---
                // 1. 還原內容 (裁剪掉追加的 16 bytes)
                if (Files.exists(targetPath) && originalFileSize != -1) {
                    RandomAccessFile rafCleanup = new RandomAccessFile(targetPath.toFile(), "rw")
                    try {
                        rafCleanup.setLength(originalFileSize)
                        KeywordUtil.logInfo("檔案內容已還原 (已截斷 UUID)。")
                    } finally {
                        rafCleanup.close()
                    }
                }
                // 2. 還原檔名
                if (Files.exists(targetPath)) {
                    Files.move(targetPath, originalPath, StandardCopyOption.REPLACE_EXISTING)
                    KeywordUtil.logInfo("檔案檔名已還原。")
                }
                // 3. 歸還 DB 狀態
                MxfManager.resetStatus(dbId)
            }
        }
    }
    
    WebUI.delay(2)
    WebUIExtensions.waitforprocessing()
    WebUI.click(findTestObject('主畫面/編輯媒資頁/儲存'))
    WebUI.delay(2)
    if (WebUI.verifyElementClickable(findTestObject('主畫面/編輯媒資頁/儲存'), FailureHandling.OPTIONAL)) {
        WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/儲存'))
    }
    
    WebUI.comment(('ID: ' + specificVideoID) + ' 上傳成功。')
    isSuccess = true

    WebUI.delay(2)
    if (GlobalVariable.metateam == 2) {
        if (WebUI.verifyElementClickable(findTestObject('主畫面/未公開媒資頁/編輯媒資1'), FailureHandling.OPTIONAL)) {
            WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/編輯媒資1'))
            
            // 設定時段檔次
            List<Map<String, String>> myData = [
                [period: "晨間06:00~11:59", count: "3"],
                [period: "午間12:00~17:59", count: "4"],
                [period: "晚間18:00~23:59", count: "5"]
            ]
            WebUIExtensions.setAirtimeTable(myData)
			
			// 定義 1 到 3 的範圍，然後呼叫 get() 隨機取得其中一個數字
			int showcliptime = (1..8).get(new Random().nextInt(8))
			
			switch (showcliptime) {
				case 1:
					WebUI.comment("執行第一種指令組合")
					// 設定有效頻道
					intervalList = [
						[channel: "公視", start: "2026-01-01", end: "2027-03-01", weeks: ["星期二", "星期三", "星期四"]],
						[channel: "小公視", start: "2026-01-01", end: "2027-03-01", weeks: ["星期二", "星期三", "星期四"]]
					]
					
					correspondingTitle = correspondingTitle + " 234"
					WebUI.comment("關鍵字: " + correspondingTitle)
					// 例如：設定特定的時碼或上傳特定的浮水印
					break
					
				case 2:
					WebUI.comment("執行第二種指令組合")
					// 設定有效頻道
					intervalList = [
						[channel: "公視", start: "2026-01-01", end: "2027-03-01", weeks: ["星期一", "星期二", "星期三"]],
						[channel: "小公視", start: "2026-01-01", end: "2027-03-01", weeks: ["星期一", "星期二", "星期三"]]
					]
					correspondingTitle = correspondingTitle + " 123"
					WebUI.comment("關鍵字: " + correspondingTitle)
					break
					
				case 3:
					WebUI.comment("執行第三種指令組合")
					// 設定有效頻道
					intervalList = [
						[channel: "公視", start: "2026-01-01", end: "2027-03-01", weeks: ["星期四", "星期五", "星期六", "星期日"]],
						[channel: "小公視", start: "2026-01-01", end: "2027-03-01", weeks: ["星期四", "星期五", "星期六", "星期日"]]
					]
					correspondingTitle = correspondingTitle + " 4567"
					WebUI.comment("關鍵字: " + correspondingTitle)
					break
					
				case 4:
					WebUI.comment("執行第四種指令組合")
					// 設定有效頻道
					intervalList = [
						[channel: "公視", start: "2026-01-01", end: "2027-03-01", weeks: ["星期一", "星期三", "星期五", "星期六", "星期日"]],
						[channel: "小公視", start: "2026-01-01", end: "2027-03-01", weeks: ["星期一", "星期三", "星期五", "星期六", "星期日"]]
					]
					correspondingTitle = correspondingTitle + " 13567"
					WebUI.comment("關鍵字: " + correspondingTitle)
					break
					
				case 5:
					WebUI.comment("執行第五種指令組合")
					// 設定有效頻道
					intervalList = [
						[channel: "公視", start: "2026-01-01", end: "2027-03-01", weeks: ["星期一", "星期二", "星期六", "星期日"]],
						[channel: "小公視", start: "2026-01-01", end: "2027-03-01", weeks: ["星期一", "星期二", "星期六", "星期日"]]
					]
					correspondingTitle = correspondingTitle + " 1267"
					WebUI.comment("關鍵字: " + correspondingTitle)
					break
					
				case 6:
					WebUI.comment("執行第六種指令組合")
					// 設定有效頻道
					intervalList = [
						[channel: "公視", start: "2026-01-01", end: "2027-03-01", weeks: ["星期五", "星期六", "星期日"]],
						[channel: "小公視", start: "2026-01-01", end: "2027-03-01", weeks: ["星期五", "星期六", "星期日"]]
					]
					correspondingTitle = correspondingTitle + " 567"
					WebUI.comment("關鍵字: " + correspondingTitle)
					break
					
				case 7:
					WebUI.comment("執行第七種指令組合")
					// 設定有效頻道
					intervalList = [
						[channel: "公視", start: "2026-01-01", end: "2027-03-01", weeks: ["星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"]],
						[channel: "小公視", start: "2026-01-01", end: "2027-03-01", weeks: ["星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"]]
					]
					correspondingTitle = correspondingTitle + " all"
					WebUI.comment("關鍵字: " + correspondingTitle)
					break
					
				case 8:
					WebUI.comment("執行第八種指令組合")
					// 設定有效頻道
					intervalList = [
						[channel: "公視", start: "2026-01-01", end: "2027-03-01", weeks: ["星期四", "星期五"]],
						[channel: "小公視", start: "2026-01-01", end: "2027-03-01", weeks: ["星期四", "星期五"]]
					]
					correspondingTitle = correspondingTitle + " 45"
					WebUI.comment("關鍵字: " + correspondingTitle)
					break
			}
			/*
            // 設定有效頻道
            List<Map<String, Object>> intervalList = [
                [channel: "公視", start: "2026-01-01", end: "2027-03-01", weeks: ["星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"]],
                [channel: "小公視", start: "2026-01-01", end: "2027-03-01", weeks: ["星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"]]
            ]
			
			*/
			
			WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/輸入Title'))
			WebUI.delay(2)
			WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/輸入Title'), Keys.chord(Keys.CONTROL,'a'))
			WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/輸入Title'), Keys.chord(Keys.BACK_SPACE))
			WebUIExtensions.setTextByLabel('媒資名稱', correspondingTitle)
			
            WebUIExtensions.setValidityIntervalTable(intervalList)
                
            WebUIExtensions.retryClickClose(findTestObject('主畫面/編輯媒資頁/儲存'))
            WebUI.delay(2)
            if (WebUI.verifyElementClickable(findTestObject('主畫面/編輯媒資頁/儲存'), FailureHandling.OPTIONAL)) {
                WebUIExtensions.retryClickClose(findTestObject('主畫面/編輯媒資頁/儲存'))                    
            }
        }
    }
}
catch (Exception e) {
    KeywordUtil.markWarning("處理 ID: ${specificVideoID} 時發生錯誤: " + e.getMessage())
} 

return correspondingTitle