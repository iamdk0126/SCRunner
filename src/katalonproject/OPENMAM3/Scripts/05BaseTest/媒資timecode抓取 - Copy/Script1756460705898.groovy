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
import java.io.RandomAccessFile
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
import custom.TimeControl
import custom.AntMultiSelect
import custom.MxfManager

/*
		WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/Page_/item1svg'))
		// 2. 等待 Drawer 載入完成 (可等待起始 Timecode 定位器出現)
		WebUI.waitForElementVisible(CustomKeywords.'custom.TimecodeHelper.createXPathObject'("//button[@title='E']"), 5)
		
		// 3. 取得這兩筆 Timecode 並存入變數
		String startTC = CustomKeywords.'custom.TimecodeHelper.getAssetStartTC'()
		String endTC = CustomKeywords.'custom.TimecodeHelper.getAssetEndTC'()
		WebUIExtensions.retryClick(findTestObject('主畫面/媒資資訊頁/收合'))
		// 4. 輸出或應用到其他地方
		WebUI.comment("取得的範圍是：從 ${startTC} 到 ${endTC}")
	*/	
		int maxRetries = 0  // 設定為 0 代表不限次數，設定 > 0 則為指定次數
		int retryCount = 0
		boolean isSuccess = false
		String startTC =""
		String endTC =""
		
		
		// 邏輯：(尚未成功) 且 (次數未滿 OR 不限次數)
		while (!isSuccess && (maxRetries == 0 || retryCount < maxRetries)) {
			try {
				WebUI.comment("開始第 ${++retryCount} 次嘗試...")
		
				// 1. 點擊進入媒資
				WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/Page_/item1svg'))
				
				// 2. 檢查 Timecode 按鈕是否出現 (Timeout 設短一點，縮短偵測循環)
				def tcButton = CustomKeywords.'custom.TimecodeHelper.createXPathObject'("//button[@title='E']")
				
				if (!WebUI.waitForElementVisible(tcButton, 5)) {
					// 如果沒出現，主動丟出 Exception 觸發 catch 區塊的「等待與收合」
					throw new Exception("Timecode 尚未生成")
				}
		
				// 3. 成功出現，抓取資料
				startTC = CustomKeywords.'custom.TimecodeHelper.getAssetStartTC'()
				endTC = CustomKeywords.'custom.TimecodeHelper.getAssetEndTC'()
				
				WebUI.comment("【成功】取得範圍：${startTC} ~ ${endTC}")
				
				// 4. 完成任務，收合並跳出迴圈
				WebUIExtensions.retryClick(findTestObject('主畫面/媒資資訊頁/收合'))
				isSuccess = true
		
			} catch (Exception e) {
				WebUI.comment("狀態：${e.message}。執行收合並等待 1 分鐘後重試...")
				
				// 失敗處理：嘗試收合以重置 UI 狀態 (避免 Drawer 卡住)
				try {
					WebUIExtensions.retryClick(findTestObject('主畫面/媒資資訊頁/收合'))
				} catch (Exception silent) {
					// 忽略收合失敗（可能本來就沒打開）
				}
		
				// 等待 1 分鐘
				WebUI.delay(60)
			}
		}
		
		
		if (WebUI.verifyElementClickable(findTestObject('主畫面/未公開媒資頁/編輯媒資1'), FailureHandling.OPTIONAL)) {
			WebUIExtensions.retryClick(findTestObject('主畫面/未公開媒資頁/編輯媒資1'))
			
			
			WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/段落資訊頁/新增'))
			
			double detectedFPS = CustomKeywords.'custom.TimecodeHelper.getAssetFPS'()
			WebUI.comment("取得的FPS是：從 ${detectedFPS}")
			//CustomKeywords.'custom.TimecodeHelper.splitAndSetSixSegments'(startTC, endTC, detectedFPS)
			CustomKeywords.'custom.TimecodeHelper.splitAndSetDynamicSegments'(startTC, endTC, detectedFPS, 8)  // 每8分鐘一段段落
			
		/*
			WebUIExtensions.retrySetText(findTestObject('主畫面/編輯媒資頁/段落資訊頁/段落標題'), "SEG1") // 原來是填變數chapter1
			
			TimecodeHelper.inputTimecode(findTestObject('主畫面/編輯媒資頁/段落資訊頁/timecode起'), '00:00:00:00')
		
			String tapeValue = TimecodeHelper.getTimecode(findTestObject('主畫面/編輯媒資頁/時長'))
		
			TimecodeHelper.inputTimecode(findTestObject('主畫面/編輯媒資頁/段落資訊頁/timecode迄'), tapeValue)
		*/	
			
		
			WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/儲存'))
		
		}
		
			



