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
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
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
import custom.FromCSV

// --- 常數設定 ---
String BASE_DRIVE_S = GlobalVariable.BASE_DRIVE_S
String VIDEO_DIR_D = GlobalVariable.LocalFolder
String specificVideoID = ""
String correspondingTitle = ""
String currentFilePath = ""
String subtitleFilePath = ""
String tgaFileName = ""
String VideoID = videoParam
String Title = titleParam
String myRobotName = titleParam
int rowIndex = rowIndexParam
String csvname = GlobalVariable.csvname

//String tabM = FromCSV.getValue(csvname, rowIndex, 'M') //節目編號
//String tabN = FromCSV.getValue(csvname, rowIndex, 'N') //節目名稱
String tabAE = FromCSV.getValue(csvname, rowIndex, 'AE') //集數

//Title = tabN
//VideoID = tabM


// 設定 SQLite 任務 DB 路徑
String dbFile = GlobalVariable.PTVMXF_Folder + "/mxf_tasks.db"
MxfManager.setDbPath(dbFile)

// --- 3. 領取任務與處理 ---
def job = MxfManager.grabNextJob(myRobotName)
int currentSerial = MxfManager.getNextSerialNumber() 

String progtype = progParam
long ONE_HOUR_IN_SECONDS = 3600
int elapsedSeconds = 0
int maxWait = 86400 // 24 小時
int checkInterval = 30 

int randomX = (Title != '看公視說英語') ? (1000 + indexParam) : (8000 + indexParam)

TimeControl.checkPauseTime(0, 0, 0, 20) 

LocalDate today = LocalDate.now()
DateTimeFormatter formatter = DateTimeFormatter.ofPattern('yyyyMMdd')
String formattedDate = today.format(formatter)
String xmlFilePath = "${BASE_DRIVE_S}${formattedDate}-air.all.xml"

WebUI.comment('正在檢查今日的 Video ID 檔案: ' + xmlFilePath)

// --- 隨機選一筆 XML Video ---
def result = CustomKeywords.'custom.XmlReader.getRandomVideoAndTitle'(xmlFilePath)
if (result == null) {
    WebUI.comment('XML 無可用資料，將在 30 秒後重試。')
    WebUI.delay(30)
    return
} else {
    specificVideoID = result[0]
    correspondingTitle = result[1]
    WebUI.comment("抽到可用 Video ID: $specificVideoID, Title: $correspondingTitle")
}

if (GlobalVariable.metateam == 1) {
    correspondingTitle = progtype + ": " + correspondingTitle
} else if (GlobalVariable.metateam == 2) {
    correspondingTitle = Title
}

// --- MySQL 檢查是否已存在 ---
boolean existsInDb = false
Connection conn = null
try {
    Class.forName('com.mysql.cj.jdbc.Driver')
    conn = DriverManager.getConnection("jdbc:mysql://${GlobalVariable.mysqlip}:3306/${GlobalVariable.mysqldbname}?serverTimezone=UTC&useSSL=false", GlobalVariable.mysqluser, GlobalVariable.mysqlpw)
    Statement stmt = conn.createStatement()
    ResultSet rs = stmt.executeQuery("SELECT 1 FROM assets WHERE asset_name = '${correspondingTitle}' LIMIT 1")
    existsInDb = rs.next()
    rs.close()
    stmt.close()
} catch (Exception e) {
    e.printStackTrace()
} finally {
    if (conn) conn.close()
}

if (existsInDb) {
    WebUI.comment("Title 已存在資料庫，延遲 30 秒再重試")
    WebUI.delay(30)
    return
}

boolean isSuccess = false

try {
    // --- UI 新增媒資操作 ---
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
	WebUI.comment("Title =" + Title)
	WebUI.comment("集數 =" + tabAE)
	WebUI.comment("currentSerial =  ${currentSerial}")
    
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

        // 重要：記錄原始長度用於還原，取代舊有的 byte 值記錄
        long originalFileSize = -1

        if (Title != '看公視說英語') { randomX = 1000 + currentSerial }
        else { randomX = 8000 + currentSerial }
        
        // 編輯標題與中繼資料
        WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/輸入Title'))
        WebUI.delay(1)
        WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/輸入Title'), Keys.chord(Keys.CONTROL, 'a'))
        WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/輸入Title'), Keys.chord(Keys.BACK_SPACE))
        
        if (GlobalVariable.metateam == 2) {
			//randomX = tabAE
            correspondingTitle = correspondingTitle + " 第 " + tabAE + " 集"
            WebUIExtensions.setTextByLabel('媒資名稱', correspondingTitle)
            DropdownHelper.selectDropdownOptionByLabel('類型', progtype)
            WebUIExtensions.setTextByLabel('集數', tabAE)
            WebUIExtensions.setTextByLabel('音軌設定', 'LRLR----')
            DropdownHelper.selectDropdownOptionByLabel('吸菸卡', '吸菸卡 吸菸有害健康')
            DropdownHelper.selectDropdownOptionByLabel('警示卡', '制式版5 危險動作警示')
            DropdownHelper.selectDropdownOptionByLabel('公視LOGO', '公視LOGO左邊')
            DropdownHelper.selectDropdownOptionByLabel('雙語CH1', '國')
            DropdownHelper.selectDropdownOptionByLabel('雙語CH2', '台')
            DropdownHelper.selectDropdownOptionByLabel('雙語CH3', '英')
            DropdownHelper.selectDropdownOptionByLabel('雙語CH4', '客(四縣腔)')
            DropdownHelper.selectDropdownOptionByLabel('節目級別', '普')
            AntMultiSelect.selectByCsvColumnFromLocal('側標', 'meta.csv', 2, 'AT')
            WebUIExtensions.setTextByLabel('節目名稱', Title)
            WebUI.delay(5)
            DropdownHelper.selectDropdownOptionByLabel('節目名稱', Title)// + " (ID:" + VideoID + ")")
        } else {
            WebUIExtensions.setTextByLabel('媒資名稱', correspondingTitle)
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
                // A. 檔案改名
                Files.move(originalPath, targetPath, StandardCopyOption.REPLACE_EXISTING)
                
                // B. 使用 UUID 追加法修改 SHA256
                RandomAccessFile raf = new RandomAccessFile(targetPath.toFile(), "rw")
                try {
                    originalFileSize = raf.length()
                    UUID uuid = UUID.randomUUID()
                    ByteBuffer bb = ByteBuffer.wrap(new byte[16])
                    bb.putLong(uuid.getMostSignificantBits())
                    bb.putLong(uuid.getLeastSignificantBits())
                    
                    raf.seek(originalFileSize) // 跳到尾巴
                    raf.write(bb.array())      // 追加 16 bytes 的 UUID
                    KeywordUtil.logInfo("SHA256 已變更 (UUID 追加)，原始長度: ${originalFileSize}")
                } finally {
                    raf.close()
                }
                
                // C. 執行上傳
                WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/video'))
                WebUI.uploadFile(findTestObject('主畫面/編輯媒資頁/upload_video'), targetPath.toString())
                
                KeywordUtil.logInfo("長效上傳開始，預計超時設定：24 小時...")
                while (elapsedSeconds < maxWait) {
                    boolean stillUploading = WebUI.verifyElementPresent(findTestObject('主畫面/編輯媒資頁/上傳中取消'), 5, FailureHandling.OPTIONAL)
                    if (!stillUploading) {
                        KeywordUtil.logInfo("偵測到上傳完成，任務標記結束: " + targetPath.toString())
                        WebUI.delay(5)
                        WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/radio1'))
                        break
                    }
                    elapsedSeconds += checkInterval
                    WebUI.delay(checkInterval)
                }
                
                if (elapsedSeconds >= maxWait) {
                    KeywordUtil.markError("檔案上傳超過 24 小時，強制中斷。")
                }
                
                // D. 生成與上傳 TGA (使用變造後的 targetPath)
                String tgaFullPath = CustomKeywords.'custom.WebUIExtensions.captureFrameToTga'(targetPath.toString())
                WebUI.comment("tgaPath = $tgaFullPath")
                
                if (tgaFullPath != "") {
                    File tgaFile = new File(tgaFullPath)
                    tgaFileName = tgaFile.getName()
                    GlobalVariable.UploadTgaPath = tgaFullPath
                    
                    WebUI.comment("取得 TGA 純檔名: " + tgaFileName)
                
                    TestObject imgBtn = new TestObject("ImgDropdownBtn").addProperty("xpath", ConditionType.EQUALS, "//button[contains(., '圖片')]")
                    WebUI.click(imgBtn)
                    WebUI.delay(1)
                
                    TestObject tgaInput = new TestObject("TgaFileInput").addProperty("xpath", ConditionType.EQUALS, "//input[@type='file' and @accept='.tga']")
                    WebUI.uploadFile(tgaInput, tgaFullPath)
                } else {
                    WebUI.comment("⚠️ 無法取得 TGA 檔名，擷取可能失敗")
                }
                
                KeywordUtil.logInfo("上傳成功：序號 ${currentSerial}")
                
            } catch (Exception e) {
                KeywordUtil.markError("上傳過程發生異常: " + e.message)
            } finally {
                // --- 檔案還原處理 ---
                
                // 1. 還原內容 (將檔案裁剪回原始長度，移除 UUID)
                if (Files.exists(targetPath) && originalFileSize != -1) {
                    RandomAccessFile rafCleanup = new RandomAccessFile(targetPath.toFile(), "rw")
                    try {
                        rafCleanup.setLength(originalFileSize)
                        KeywordUtil.logInfo("檔案內容已還原 (已切除追加 UUID)。")
                    } finally {
                        rafCleanup.close()
                    }
                }
                // 2. 還原檔名
                if (Files.exists(targetPath)) {
                    Files.move(targetPath, originalPath, StandardCopyOption.REPLACE_EXISTING)
                    KeywordUtil.logInfo("檔案檔名已還原為原本名稱。")
                }
                // 3. 歸還 SQLite 任務狀態
                MxfManager.resetStatus(dbId)
                KeywordUtil.logInfo("DB 任務 ID ${dbId} 狀態已重置。")
            }
        
        } else {
            KeywordUtil.logInfo("目前資料庫中已無待處理 (status=0) 的媒資。")
        }
    }   
        
    WebUI.delay(2)
    WebUIExtensions.waitforprocessing()
    
    // 設定下拉選單並儲存
    DropdownHelper.selectDropdownOptionByLabel('節目LOGO', tgaFileName)
    WebUI.click(findTestObject('主畫面/編輯媒資頁/儲存'))
    WebUI.delay(2)

    if (WebUI.verifyElementClickable(findTestObject('主畫面/編輯媒資頁/儲存'), FailureHandling.OPTIONAL)) {
        WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/儲存'))
    }
    
    WebUI.comment(('ID: ' + specificVideoID) + ' 上傳成功。')
    isSuccess = true
    WebUI.delay(10)
    
    if (GlobalVariable.metateam == 2) {
        WebUIExtensions.retryClick(findTestObject('主畫面/公開媒資'))
        WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資'))
        WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/搜尋媒資'))
        WebUIExtensions.setTextByLabel('關鍵字', correspondingTitle)
        WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/搜尋'))
    }
    
    if (Math.random() < 0.7) {   
        WebUI.callTestCase(findTestCase('05BaseTest/編輯列管'), [:], FailureHandling.CONTINUE_ON_FAILURE)
        WebUI.comment("執行了：編輯列管")
    } else {
        WebUI.comment("略過：編輯列管")
    }
}
catch (Exception e) {
    KeywordUtil.markWarning((('處理 ID: ' + specificVideoID) + ' 時發生錯誤: ') + e.getMessage())
} 

return correspondingTitle