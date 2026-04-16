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
 */ import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
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
import java.time.LocalDateTime as LocalDateTime
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
import java.text.SimpleDateFormat as SimpleDateFormat
import java.util.Date as Date
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
import custom.BrowserHelper as BrowserHelper
import custom.WebUIExtensions as WebUIExtensions
import custom.TimecodeHelper as TimecodeHelper
import custom.DropdownHelper as DropdownHelper

// --- 常數設定 ---
String Title = TitleParam

String channelOption = channelParam

String recordresolution = resolutionParam

long ONE_HOUR_IN_SECONDS = 3600

Random randomGenerator = new Random()

Random rand = new Random()

'集數隨機1~100'
int randomX = 1 + rand.nextInt(99)

//TimeControl.checkPauseTime(0, 0, 0, 20)  // 00:00 ~ 00:20 暫停
/*
LocalDate today = LocalDate.now()

DateTimeFormatter formatter = DateTimeFormatter.ofPattern('yyyyMMdd')

String formattedDate = today.format(formatter)
*/
// 定義格式
DateTimeFormatter nowformatter = DateTimeFormatter.ofPattern('yyyy-MM-dd HH:mm:ss')

WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/video'))

WebUIExtensions.retryClick(findTestObject('主畫面/編輯媒資頁/record_video'))

WebUIExtensions.setTextByLabel('檔案名稱', Title)

DropdownHelper.selectDropdownOptionByLabel('伺服器', GlobalVariable.OpenIngest)

WebUI.click(findTestObject('主畫面/編輯媒資頁/錄製影片頁/頻道'))

DropdownHelper.selectVisibleDropdownOptionScroll(channelOption)

DropdownHelper.selectDropdownOptionByLabel('參考來源時間碼', '無')

WebUI.click(findTestObject('主畫面/編輯媒資頁/錄製影片頁/input__record-duration'))

WebUI.setText(findTestObject('主畫面/編輯媒資頁/錄製影片頁/input__record-duration'), '00:01:00')

WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/錄製影片頁/input__record-duration'), Keys.chord(Keys.ENTER))

WebUI.delay(5)

//WebUIExtensions.setTextByLabel('錄製時長', '00:01:00')
// 取得當前時間並加上 5 分鐘
LocalDateTime nowPlus5 = LocalDateTime.now().plusMinutes(1)

// 格式化
String nowformattedTime = nowPlus5.format(nowformatter)

WebUI.comment('starttime :' + nowformattedTime)

WebUI.click(findTestObject('主畫面/編輯媒資頁/錄製影片頁/開始時間'))

WebUI.setText(findTestObject('主畫面/編輯媒資頁/錄製影片頁/開始時間'), nowformattedTime)

WebUI.sendKeys(findTestObject('主畫面/編輯媒資頁/錄製影片頁/開始時間'), Keys.chord(Keys.ENTER))

//WebUIExtensions.setTextByLabel('開始時間', nowformatter)
//WebUI.uploadFile(findTestObject('主畫面/編輯媒資頁/upload_video'), currentFilePath)
DropdownHelper.selectDropdownOptionByLabel('錄製解析度', recordresolution)

WebUI.delay(5)

WebUI.click(findTestObject('主畫面/編輯媒資頁/錄製影片頁/建立'))

