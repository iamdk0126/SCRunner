 /*
主要是做「自動化上傳影片」任務：
這個腳本會根據每天產生的 *-air.all.txt 檔案，隨機選擇一筆 Video ID + Title，檢查是否已經存在於資料庫（MySQL）。
如果還沒存在 → 用這筆資料建立媒資、填入 Video ID、上傳一個隨機 .mxf 影片檔，並進行儲存。
如果已經存在 → 就再抽一筆，直到找到新的。
如果找不到新資料，或是檔案不存在/沒有影片檔，就會延遲一小時再重試。
這個流程會重複執行 indexParam 次（外部參數決定）。

主要流程拆解
初始化設定
BASE_DRIVE_S 與 VIDEO_DIR_D 來自 GlobalVariable（全域參數）。
totalLoopsToRun = indexParam → 外部傳入的執行次數。
驗證 totalLoopsToRun > 0，否則停止。
進入主迴圈 (while)
每回合最多執行一次「影片上傳」。
若失敗，延遲 1 小時再重試，不會增加計數器。
檢查當日 Video ID 檔案
路徑：BASE_DRIVE_S + yyyyMMdd-air.all.txt
若檔案不存在或空檔 → delay 1 小時後重試。
讀取檔案內容
每行格式：videoId \t title
去除空白行，存入 rawAllInfoFileLines。
隨機挑選 Video ID / Title
從 List 隨機取一筆。
連線到 MySQL，查詢 assets 資料表，看 asset_name = titleCandidate 是否存在。
若已存在 → 丟掉這筆，繼續抽。
若不存在 → 就選定這組 Video ID 與 Title。
檢查影片檔
從 VIDEO_DIR_D 目錄中隨機選一個 .mxf 檔案。
若沒有任何檔案 → delay 1 小時重試。
UI 自動化操作（Katalon WebUI）
在「主畫面」點選 新增媒資 → 新增檔案 → 選擇模板1 → 建立。
填入 Video ID。
點選 同步文稿 → 儲存。
到 未公開媒資 搜尋 Title，進入編輯頁面。
上傳影片檔（隨機選的 .mxf）。
呼叫另一個測試案例 05BaseTest/編輯媒資分析VideoID選擇媒資分類（帶入 Video ID）。
再次儲存。
上傳成功後，計數器 executedLoops++。
錯誤處理
任何 Exception 都會記錄 Warning，但不會中斷整體流程。
完成後
印出完成訊息：已成功完成指定的 n 次上傳任務。
 */ 
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
import custom.TimeControl

// --- 常數設定 ---
String BASE_DRIVE_S = GlobalVariable.BASE_DRIVE_S

String VIDEO_DIR_D = GlobalVariable.LocalFolder

long ONE_HOUR_IN_SECONDS = 3600

Random randomGenerator = new Random()

Random rand = new Random()

'集數隨機1~100'
int randomX = 1 + rand.nextInt(99)

TimeControl.checkPauseTime(0, 0, 0, 20)  // 00:00 ~ 00:20 暫停

LocalDate today = LocalDate.now()

DateTimeFormatter formatter = DateTimeFormatter.ofPattern('yyyyMMdd')

String formattedDate = today.format(formatter)

// 改成 XML 檔案
String xmlFilePath = "${BASE_DRIVE_S}${formattedDate}-air.all.xml"


WebUI.comment('正在檢查今日的 Video ID 檔案: ' + xmlFilePath)

// --- 隨機選一筆 XML Video ---
def result = CustomKeywords.'custom.XmlReader.getRandomVideoAndTitle'(xmlFilePath)
if (result == null) {
    WebUI.comment('XML 無可用資料，將在30Sec後重試。' + result)
    //WebUI.delay(ONE_HOUR_IN_SECONDS)
	WebUI.delay(30)
} else {
    specificVideoID = result[0]
    correspondingTitle = result[1]

WebUI.comment("抽到可用 Video ID: $specificVideoID, Title: $correspondingTitle")
}
// --- DB 檢查是否已存在 ---
boolean existsInDb = false
Connection conn = null
Statement stmt = null
ResultSet rs = null

try {
    Class.forName('com.mysql.cj.jdbc.Driver')
    conn = DriverManager.getConnection(
        "jdbc:mysql://${GlobalVariable.mysqlip}:3306/${GlobalVariable.mysqldbname}?serverTimezone=UTC&useSSL=false",
        GlobalVariable.mysqluser,
        GlobalVariable.mysqlpw
    )
    stmt = conn.createStatement()
    rs = stmt.executeQuery("SELECT 1 FROM assets WHERE asset_name = '${correspondingTitle}' LIMIT 1")
    existsInDb = rs.next()
} catch (Exception e) {
    e.printStackTrace()
} finally {
    if (rs) rs.close()
    if (stmt) stmt.close()
    if (conn) conn.close()
}

if (existsInDb) {
    //WebUI.comment("Title 已存在資料庫，延遲一小時再重試")
    //WebUI.delay(ONE_HOUR_IN_SECONDS)
	WebUI.comment("Title 已存在資料庫，延遲30Sec再重試")
	WebUI.delay(30)
}

// --- 取得隨機影片檔 ---
File videoDir = new File(VIDEO_DIR_D)

if (!videoDir.exists()) {
	KeywordUtil.markWarning("目錄 $VIDEO_DIR_D 不存在！流程將暫停一小時。")
	WebUI.delay(ONE_HOUR_IN_SECONDS)
	return
}

List<File> mxfFiles = videoDir.listFiles({ file -> file.isFile() && file.name.toLowerCase().endsWith('.mxf') } as FileFilter).toList()

if (mxfFiles.isEmpty()) {
    KeywordUtil.markWarning("警告: 在目錄 $VIDEO_DIR_D 找不到任何 .MXF 檔案! 流程將暫停一小時。")
    WebUI.delay(ONE_HOUR_IN_SECONDS)
	return
}

File randomMxfFile = mxfFiles[randomGenerator.nextInt(mxfFiles.size())]
String currentFilePath = randomMxfFile.getAbsolutePath()


//specificVideoID = '0930'

WebUI.comment('使用 Video ID: ' + specificVideoID)
WebUI.comment('對應 Title: ' + correspondingTitle)
WebUI.comment('隨機選擇檔案: ' + currentFilePath)
boolean isSuccess = false

try {
    // --- UI 操作 ---
	if (WebUI.verifyElementClickable(findTestObject('主畫面/新增媒資'), FailureHandling.OPTIONAL)) {
		WebUI.click(findTestObject('主畫面/新增媒資'))
	} else {
		return
	}
    
	
	WebUI.delay(2)
	/*
	if (WebUI.verifyElementClickable(findTestObject('主畫面/新增媒資頁/建立媒資'), FailureHandling.OPTIONAL)) {
		WebUI.click(findTestObject('主畫面/新增媒資頁/建立媒資'))
	}*/
	
	//WebUIExtensions.retryClick(findTestObject('主畫面/新增媒資頁/建立媒資'))
    WebUIExtensions.retryClick(findTestObject('主畫面/新增媒資頁/模板1'))

    WebUI.delay(2)

    WebUIExtensions.retryClick(findTestObject('主畫面/新增媒資頁/建立'))
	/*
	WebUI.click(findTestObject('主畫面/編輯媒資頁/輸入Title'))
	
	WebUI.delay(2)
	
	// 模擬 Ctrl + A + Backspace
	WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/輸入Title'), Keys.chord(Keys.CONTROL,'a'))
	WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/輸入Title'), Keys.chord(Keys.BACK_SPACE))
	
	WebUI.delay(2)
	
	WebUI.setText(findTestObject('主畫面/編輯媒資頁/輸入Title'), correspondingTitle, FailureHandling.OPTIONAL)
	
	WebUI.delay(2)
*/

    WebUIExtensions.retrySetText(findTestObject('主畫面/編輯媒資頁/節目編號'), specificVideoID)

    WebUI.delay(2)

    //WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/同步文稿'))

    //WebUI.delay(2)
	
	WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/輸入Title'))
	
	WebUI.delay(2)
	
	// 模擬 Ctrl + A + Backspace
	WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/輸入Title'), Keys.chord(Keys.CONTROL,'a'))
	WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/輸入Title'), Keys.chord(Keys.BACK_SPACE))
	
	WebUI.delay(2)
	
	//WebUIExtensions.retrySetText(findTestObject('主畫面/編輯媒資頁/輸入Title'), correspondingTitle)
	WebUIExtensions.setTextByLabel('名稱', correspondingTitle)
	
	WebUI.delay(2)
	

    WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/儲存'))
	//WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/取消'))

    WebUI.delay(2)
/*
    if (WebUI.verifyElementClickable(findTestObject('主畫面/編輯媒資頁/儲存'), FailureHandling.OPTIONAL)) {
        WebUI.click(findTestObject('主畫面/編輯媒資頁/儲存'))
    }*/
	
	
    WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資'))
/*
    if (WebUI.verifyElementPresent(findTestObject('主畫面/未公開媒資頁/搜尋媒資'), 1, FailureHandling.OPTIONAL)) {
        WebUI.click(findTestObject('主畫面/未公開媒資頁/搜尋媒資'))
    }*/
	WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/搜尋媒資'))
    //WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/輸入關鍵字'))

    //WebUIExtensions.retrySetText(findTestObject('主畫面/未公開媒資頁/輸入關鍵字'), correspondingTitle)
	WebUIExtensions.setTextByLabel('關鍵字', correspondingTitle)
	
    'search'
    WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/搜尋'))

    //WebUI.click(findTestObject('主畫面/未公開媒資頁/編輯媒資1'))
	
    if (WebUI.verifyElementClickable(findTestObject('主畫面/未公開媒資頁/編輯媒資1'), FailureHandling.OPTIONAL)) {
        WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/編輯媒資1'))
		
		//WebUIExtensions.retrySetText(findTestObject('主畫面/編輯媒資頁/節目名稱'), correspondingTitle)
		WebUIExtensions.setTextByLabel('節目名稱', correspondingTitle)
		
		
		WebUIExtensions.retrySetText(findTestObject('主畫面/編輯媒資頁/集數'), "$randomX") //String.valueOf(randomX)
		//WebUIExtensions.setTextByLabel('集數', "10") 
		
		//WebUIExtensions.retrySetText(findTestObject('主畫面/編輯媒資頁/音軌設定'), 'LRLR----')	
		WebUIExtensions.setTextByLabel('音軌設定', 'LRLR----')
		
		WebUI.comment('specificVideoID=' + specificVideoID)
		if (specificVideoID != '') {
			WebUI.callTestCase(findTestCase('05BaseTest/編輯媒資分析VideoID選擇媒資分類'), [('VideoIDParam') : specificVideoID], FailureHandling.CONTINUE_ON_FAILURE) } 
		else { 
			WebUI.comment('specificVideoID 為 null，跳過編輯媒資分析測試案例')
		}
		WebUI.delay(2)
		
		//WebUI.click(findTestObject('主畫面/編輯媒資頁/用途下拉'))
		
		//DropdownHelper.selectVisibleDropdownOptionScroll('播放')
		DropdownHelper.selectDropdownOptionByLabel('用途','播放')
		
		
		//WebUI.click(findTestObject('主畫面/編輯媒資頁/類型下拉'))
		
		//DropdownHelper.selectVisibleDropdownOptionScroll('節目')
		DropdownHelper.selectDropdownOptionByLabel('類型','節目')
		
		WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/video'))

		WebUI.uploadFile(findTestObject('主畫面/編輯媒資頁/upload_video'), currentFilePath)

		WebUI.waitForElementVisible(findTestObject('主畫面/編輯媒資頁/主檔刪除'), 60, FailureHandling.OPTIONAL)

		WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/儲存'))

		WebUI.delay(2)

		if (WebUI.verifyElementClickable(findTestObject('主畫面/編輯媒資頁/儲存'), FailureHandling.OPTIONAL)) {
			WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/儲存'))
			//WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/取消'))
		}
    
		WebUI.comment(('ID: ' + specificVideoID) + ' 上傳成功。')

		isSuccess = true
	}
	WebUI.delay(2)
	
	if (WebUI.verifyElementClickable(findTestObject('主畫面/未公開媒資頁/編輯媒資1'), FailureHandling.OPTIONAL)) {
		WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/編輯媒資1'))
			
		TimecodeHelper.inputTimecode(findTestObject('主畫面/編輯媒資頁/timecode起'), '00:00:00:00')
		
		String tapeValue = TimecodeHelper.getTimecode(findTestObject('主畫面/編輯媒資頁/時長'))
		
		TimecodeHelper.inputTimecode(findTestObject('主畫面/編輯媒資頁/timecode迄'), tapeValue)
		
		WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/儲存'))
		
		WebUI.delay(2)
		
		if (WebUI.verifyElementClickable(findTestObject('主畫面/編輯媒資頁/儲存'), FailureHandling.OPTIONAL)) {
			WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/儲存'))					
		}
	}
	
	
}
catch (Exception e) {
    KeywordUtil.markWarning((('處理 ID: ' + specificVideoID) + ' 時發生錯誤: ') + e.getMessage())
} 

WebUI.comment('--- 自動化腳本執行完畢 ---')

