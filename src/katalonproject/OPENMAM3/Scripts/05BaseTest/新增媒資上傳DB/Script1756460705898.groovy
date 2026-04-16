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

String dbFile = GlobalVariable.DB_Folder + "/mxf_tasks.db"



// 必須先指定路徑，否則 grabNextJob 會找不到資料庫或使用預設值
MxfManager.setDbPath(dbFile)

String progtype = progParam

long ONE_HOUR_IN_SECONDS = 3600

int elapsedSeconds = 0
int maxWait = 7200 // 2 小時
int checkInterval = 30 // 每 0.5 分鐘回報一次進度

Random randomGenerator = new Random()

Random rand = new Random()

'集數隨機1000+'
int randomX = 0

if (Title != '看公視說英語') { randomX = 1000 + indexParam  }
else { 	randomX = 8000 + indexParam }

//int randomX = 1000 + indexParam //rand.nextInt(100)

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

String chapter1 = correspondingTitle

if (GlobalVariable.metateam == 1) {
	correspondingTitle = progtype +": "+correspondingTitle
	
	} else
if (GlobalVariable.metateam == 2) {
		correspondingTitle = /*progtype +": "+*/ Title
	}

//correspondingTitle = progtype +" "+ Title+": "+correspondingTitle

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

/*
// --- 取得隨機影片檔 ---
File videoDir = new File(VIDEO_DIR_D)

if (!videoDir.exists()) {
	KeywordUtil.markWarning("目錄 $VIDEO_DIR_D 不存在！流程將暫停一小時。")
	WebUI.delay(ONE_HOUR_IN_SECONDS)
	return
}

// 取得所有子目錄
List<File> subDirs = videoDir.listFiles().findAll { it.isDirectory() }

if (subDirs.isEmpty()) {
	KeywordUtil.markWarning("在 $VIDEO_DIR_D 底下找不到任何子目錄，流程暫停一小時。")
	WebUI.delay(ONE_HOUR_IN_SECONDS)
	return
}

// 隨機選一個子目錄
File randomDir = subDirs[new Random().nextInt(subDirs.size())]
KeywordUtil.logInfo("🎲 隨機選取子目錄: ${randomDir.name}")

// 找出該子目錄裡的所有影片檔（支援 .mxf / .mp4）
List<File> videoFiles = randomDir.listFiles().findAll { 
	it.isFile() && (it.name.toLowerCase().endsWith('.mxf') || it.name.toLowerCase().endsWith('.mp4'))
}

if (videoFiles.isEmpty()) {
	KeywordUtil.markWarning("在子目錄 ${randomDir.name} 找不到任何 .mxf 或 .mp4 檔案，流程暫停一小時。")
	WebUI.delay(ONE_HOUR_IN_SECONDS)
	return
}

// 隨機選取一個影片檔
File randomVideoFile = videoFiles[new Random().nextInt(videoFiles.size())]
currentFilePath = randomVideoFile.getAbsolutePath()

KeywordUtil.logInfo("🎬 選取影片檔案: ${currentFilePath}")

// --- 取得字幕檔（同一子目錄）---
List<File> subtitleFiles = randomDir.listFiles().findAll {
	it.isFile() && (it.name.toLowerCase().endsWith('.txt') || it.name.toLowerCase().endsWith('.srt'))
}


if (subtitleFiles.isEmpty()) {
	KeywordUtil.markWarning("⚠️ 在子目錄 ${randomDir.name} 找不到字幕檔 (.txt/.srt)")
} else {
	// 隨機挑選一個字幕檔（或你可以改成比對同名）
	File randomSubtitleFile = subtitleFiles[new Random().nextInt(subtitleFiles.size())]
	subtitleFilePath = randomSubtitleFile.getAbsolutePath()
	KeywordUtil.logInfo("✅ 選取字幕檔: ${subtitleFilePath}")

	// 設定 GlobalVariable.SelectedSubtitle（如果沒定義就動態建立）
	  if (!GlobalVariable.metaClass.hasProperty(GlobalVariable, 'SelectedSubtitle')) {
		GlobalVariable.metaClass.SelectedSubtitle = ""
	}
	GlobalVariable.SelectedSubtitle = subtitleFilePath
}
// --- 取得字幕檔（同一子目錄）---
*/
//specificVideoID = '0930'

WebUI.comment('使用 Video ID: ' + specificVideoID)
WebUI.comment('對應 Title: ' + correspondingTitle)
WebUI.comment('選擇隨機目錄下檔案: ' + currentFilePath)
WebUI.comment('選擇同一目錄下字幕檔: ' + subtitleFilePath)

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
	
	if (WebUI.verifyElementClickable(findTestObject('主畫面/新增媒資頁/iNews匯入'), FailureHandling.OPTIONAL)) {
		WebUIExtensions.retryClick(findTestObject('主畫面/新增媒資頁/建立媒資'))
	}
	
	
	//WebUIExtensions.retryClick(findTestObject('主畫面/新增媒資頁/建立媒資'))
    //WebUIExtensions.retryClick(findTestObject('主畫面/新增媒資頁/模板1'))
	
	WebUIExtensions.clickTableTitle(Title)

    WebUI.delay(2)

    WebUI.click(findTestObject('主畫面/新增媒資頁/建立'))
	
	//WebUIExtensions.setTextByLabel('iNews_VideoID', specificVideoID)
	def (dbId, mxfPath) = MxfManager.grabNextJob(myRobotName)
	
	if (Title != '看公視說英語') { randomX = 1000 + dbId  }
	else { 	randomX = 8000 + dbId }
	
	if (GlobalVariable.metateam == 2) {
	WebUIExtensions.retrySetText(findTestObject('主畫面/編輯媒資頁/集數'), "$randomX") //String.valueOf(randomX)
	
	WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/輸入Title'))
	
	WebUI.delay(2)
	
	// 模擬 Ctrl + A + Backspace
	WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/輸入Title'), Keys.chord(Keys.CONTROL,'a'))
	WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/輸入Title'), Keys.chord(Keys.BACK_SPACE))
	
	WebUI.delay(2)
	correspondingTitle = correspondingTitle+" 第 " + randomX + " 集"
	//WebUIExtensions.retrySetText(findTestObject('主畫面/編輯媒資頁/輸入Title'), correspondingTitle)
	WebUIExtensions.setTextByLabel('媒資名稱', correspondingTitle)
	}
	if (GlobalVariable.metateam == 1) {
		
	// 模擬 Ctrl + A + Backspace
	WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/輸入Title'), Keys.chord(Keys.CONTROL,'a'))
	WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/輸入Title'), Keys.chord(Keys.BACK_SPACE))
	WebUIExtensions.setTextByLabel('媒資名稱', correspondingTitle)		
	}

	
	
	
	
/*
    WebUI.delay(2)

	WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/輸入Title'))
	
	WebUI.delay(2)
	
	// 模擬 Ctrl + A + Backspace
	WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/輸入Title'), Keys.chord(Keys.CONTROL,'a'))
	WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/輸入Title'), Keys.chord(Keys.BACK_SPACE))
	
	WebUI.delay(2)
	
	//WebUIExtensions.retrySetText(findTestObject('主畫面/編輯媒資頁/輸入Title'), correspondingTitle)
	WebUIExtensions.setTextByLabel('媒資名稱', correspondingTitle)
*/	
	WebUI.delay(2)
	
	if (GlobalVariable.metateam == 2) {
		
		
		
		DropdownHelper.selectDropdownOptionByLabel('類型',progtype)
		WebUIExtensions.setTextByLabel('節目名稱', Title)
		WebUI.delay(2)
		WebUIExtensions.setTextByLabel('音軌設定', 'LRLR----')
		
		DropdownHelper.selectDropdownOptionByLabel('吸菸卡','吸菸卡 吸菸有害健康')
		
		DropdownHelper.selectDropdownOptionByLabel('警示卡','制式版1 吸菸警示')
		
		DropdownHelper.selectDropdownOptionByLabel('公視LOGO','公視LOGO左邊')
		
		DropdownHelper.selectDropdownOptionByLabel('雙語CH1','國')
		
		DropdownHelper.selectDropdownOptionByLabel('雙語CH2','台')
		
		DropdownHelper.selectDropdownOptionByLabel('雙語CH3','英')
		
		DropdownHelper.selectDropdownOptionByLabel('雙語CH4','客(四縣腔)')

		DropdownHelper.selectDropdownOptionByLabel('節目級別','普')
				
		AntMultiSelect.selectByCsvColumnFromLocal('側標','meta.csv',2,'AT') //從meta.csv第2列 第AT欄填到側標
				
		DropdownHelper.selectDropdownOptionByLabel('節目名稱',Title+" (ID:"+VideoID+")")
					
		}

	
	
	
	WebUI.delay(2)

    WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/儲存'))
	//WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/取消'))

    WebUI.delay(2)
	
    WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資'))

	WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/搜尋媒資'))

	WebUIExtensions.setTextByLabel('關鍵字', correspondingTitle)
	
    'search'
    WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/搜尋'))

	
    if (WebUI.verifyElementClickable(findTestObject('主畫面/未公開媒資頁/編輯媒資1'), FailureHandling.OPTIONAL)) {
        WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/編輯媒資1'))
		
		WebUI.delay(2)

		//WebUIExtensions.setTextByLabel('集數', "10") 
		
		//WebUIExtensions.retrySetText(findTestObject('主畫面/編輯媒資頁/音軌設定'), 'LRLR----')	
		//WebUIExtensions.setTextByLabel('音軌設定', 'LRLR----')
/*		
		WebUI.comment('specificVideoID=' + specificVideoID)
		if (specificVideoID != '') {
			WebUI.callTestCase(findTestCase('05BaseTest/編輯媒資分析VideoID選擇媒資分類'), [('VideoIDParam') : specificVideoID], FailureHandling.CONTINUE_ON_FAILURE) } 
		else { 
			WebUI.comment('specificVideoID 為 null，跳過編輯媒資分析測試案例')
		}
		WebUI.delay(2)
*/		
		//WebUI.click(findTestObject('主畫面/編輯媒資頁/用途下拉'))
		
		//DropdownHelper.selectVisibleDropdownOptionScroll('播出')
		//DropdownHelper.selectDropdownOptionByLabel('用途','播出')
		
		
		//WebUI.click(findTestObject('主畫面/編輯媒資頁/類型下拉'))
		
		//DropdownHelper.selectVisibleDropdownOptionScroll('節目')
		//DropdownHelper.selectDropdownOptionByLabel('類型',progtype)
		//AntMultiSelect.selectByCsvColumnFromLocal('側標','meta.csv',2,'AT') //從meta.csv第2列 第AT欄填到側標
		
		WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/video'))
		
		// 1. 從資料庫搶領一筆 status = 0 的媒資
		// 此方法會自動執行: 檢查 status=0 -> 設為 status=1 -> 回傳路徑
		
/*
		def (dbId, mxfPath) = MxfManager.grabNextJob(myRobotName)
		
		if (Title != '看公視說英語') { randomX = 1000 + dbId  }
		else { 	randomX = 8000 + dbId }		
		
		if (GlobalVariable.metateam == 2) {
		WebUIExtensions.retrySetText(findTestObject('主畫面/編輯媒資頁/集數'), "$randomX") //String.valueOf(randomX)
		
		WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/輸入Title'))
		
		WebUI.delay(2)
		
		// 模擬 Ctrl + A + Backspace
		WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/輸入Title'), Keys.chord(Keys.CONTROL,'a'))
		WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/輸入Title'), Keys.chord(Keys.BACK_SPACE))
		
		WebUI.delay(2)
		correspondingTitle = correspondingTitle+" 第 " + randomX + " 集"
		//WebUIExtensions.retrySetText(findTestObject('主畫面/編輯媒資頁/輸入Title'), correspondingTitle)
		WebUIExtensions.setTextByLabel('媒資名稱', correspondingTitle)
*/		
								
			}		

		//WebUI.uploadFile(findTestObject('主畫面/編輯媒資頁/upload_video'), currentFilePath)
		
		if (dbId != null ) {
			KeywordUtil.logInfo("成功領取任務，準備上傳檔案: " + mxfPath)
			WebUI.comment( "拿到任務 ID: " + dbId)
			WebUI.comment( "拿到檔案路徑: " + mxfPath)
			
			try {
				// 2. 指派給上傳物件 (假設你的 Test Object 叫做 upload_button)
				// 注意：upload file 的物件通常是 <input type="file">
				WebUI.uploadFile(findTestObject('主畫面/編輯媒資頁/upload_video'), mxfPath)
				
				KeywordUtil.logInfo("長效上傳開始，預計超時設定：2 小時...")
				
				while (elapsedSeconds < maxWait) {
					// 檢查「取消」按鈕是否還在
					boolean stillUploading = WebUI.verifyElementPresent(findTestObject('主畫面/編輯媒資頁/上傳中取消'), 5, FailureHandling.OPTIONAL)
					
					if (!stillUploading) {
						KeywordUtil.logInfo("偵測到上傳按鈕消失，上傳完成！")
						// 3. 上傳成功後，將 DB 狀態改為 2 (已完成)
						MxfManager.markAsDone(mxfPath)
						KeywordUtil.logInfo("任務完成並標記已結束: " + mxfPath)
						
						WebUI.delay(5)
						
						WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/radio1'))
						break
					}
					
					elapsedSeconds += checkInterval
					KeywordUtil.logInfo("已上傳約 ${elapsedSeconds / 60} 分鐘，檔案傳輸中...")
					WebUI.delay(checkInterval)
				}
				
				if (elapsedSeconds >= maxWait) {
					KeywordUtil.markError("檔案上傳超過 2 小時，強制中斷。")
				}
				
				
			} catch (Exception e) {
				// 如果執行失敗，你可以視需求決定是否要將 status 改回 0 (讓別人重跑)
				// 或維持 status = 1 方便人工排錯
				KeywordUtil.markError("上傳過程中發生錯誤: " + e.message)
			}
		
		} else {
			KeywordUtil.logInfo("目前資料庫中已無待處理 (status=0) 的媒資。")
		}
		
		

		WebUI.delay(5)
		
		if (WebUI.waitForElementVisible(findTestObject('主畫面/編輯媒資頁/主檔刪除'), 60, FailureHandling.OPTIONAL)) {
			WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/radio1'))
		}
		WebUI.delay(5)
		
		//WebUI.uploadFile(findTestObject('主畫面/編輯媒資頁/上傳字幕'), subtitleFilePath)
			
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
			
			
			WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/段落資訊頁/新增'))
			
			String tapeValue = TimecodeHelper.getTimecode(findTestObject('主畫面/編輯媒資頁/時長'))
			
			TimecodeHelper.setSegmentData(
			0,
			"SEG1",
			"00:00:00:00",
			tapeValue
		)
		/*
			WebUIExtensions.retrySetText(findTestObject('主畫面/編輯媒資頁/段落資訊頁/段落標題'), "SEG1") // 原來是填變數chapter1
			
			TimecodeHelper.inputTimecode(findTestObject('主畫面/編輯媒資頁/段落資訊頁/timecode起'), '00:00:00:00')
		
			String tapeValue = TimecodeHelper.getTimecode(findTestObject('主畫面/編輯媒資頁/時長'))
		
			TimecodeHelper.inputTimecode(findTestObject('主畫面/編輯媒資頁/段落資訊頁/timecode迄'), tapeValue)
		*/	
			
		
			WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/儲存'))
		
			WebUI.delay(2)
		
			if (WebUI.verifyElementClickable(findTestObject('主畫面/編輯媒資頁/儲存'), FailureHandling.OPTIONAL)) {
				WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/儲存'))					
			}
		}
	}
	if (Math.random() < 0.5) {   // 50% 機率設定有列管
		WebUI.callTestCase(findTestCase('05BaseTest/編輯列管'), [:], FailureHandling.CONTINUE_ON_FAILURE)
		WebUI.comment("執行了：編輯列管")
	} else {
		WebUI.comment("略過：編輯列管")
	}
}
catch (Exception e) {
    KeywordUtil.markWarning((('處理 ID: ' + specificVideoID) + ' 時發生錯誤: ') + e.getMessage())
} 

WebUI.comment('--- 自動化腳本執行完畢 ---')

