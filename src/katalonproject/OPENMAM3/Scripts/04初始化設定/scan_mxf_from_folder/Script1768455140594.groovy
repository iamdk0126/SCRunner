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
import com.kms.katalon.core.configuration.RunConfiguration as RunConfiguration
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import custom.MxfManager as MxfManager


// 設定 DB 存放在專案目錄下的 Data 夾內，並掃描目標目錄
String dbFile = GlobalVariable.DB_Folder + '/mxf_tasks.db'

String videoFolder = GlobalVariable.MXF_Folder


// 執行節目DB初始化
MxfManager.initAndScanMxf(dbFile, videoFolder, true)
// 打印前 5 筆
MxfManager.printDbContent(0, 20)
MxfManager.initCounterTable() // 確保序號表存在
//MxfManager.resetCounter(186) //reset upload counter 從1開始

String CFdbFile = GlobalVariable.CFDB_Folder + '/mxf_tasks.db'

String CFvideoFolder = GlobalVariable.CFMXF_Folder

// 執行短帶CFDB初始化
MxfManager.initAndScanMxf(CFdbFile, CFvideoFolder, true)
// 打印前 5 筆
MxfManager.printDbContent(0, 20)
MxfManager.initCounterTable() // 確保序號表存在
//MxfManager.resetCounter(1) //reset upload counter 從1開始


// 設定 DB 存放在專案目錄下的 Data 夾內，並掃描目標目錄
String PTVdbFile = GlobalVariable.PTVDB_Folder + '/mxf_tasks.db'

String PTVvideoFolder = GlobalVariable.PTVMXF_Folder


// 執行節目PTVDB初始化
MxfManager.initAndScanMxf(PTVdbFile, PTVvideoFolder, true)
// 打印前 5 筆
MxfManager.printDbContent(0, 20)
MxfManager.initCounterTable() // 確保序號表存在
//MxfManager.resetCounter(8) //reset upload counter 從1開始
