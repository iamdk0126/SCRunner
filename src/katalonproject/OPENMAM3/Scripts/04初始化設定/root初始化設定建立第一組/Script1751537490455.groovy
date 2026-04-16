import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import internal.GlobalVariable as GlobalVariable

String Team = teamParam

WebUI.callTestCase(findTestCase('04初始化設定/新增欄位'), [('teamParam') : Team], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('04初始化設定/新增媒資分類'), [('teamParam') : Team], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('04初始化設定/新增預設模板'), [('teamParam') : Team], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('04初始化設定/新增列管'), [('teamParam') : Team], FailureHandling.CONTINUE_ON_FAILURE)

WebUI.callTestCase(findTestCase('04初始化設定/新增角色'), [('teamParam') : Team], FailureHandling.STOP_ON_FAILURE)


WebUI.callTestCase(findTestCase('04初始化設定/新增審核流程'), [('teamParam') : Team], FailureHandling.CONTINUE_ON_FAILURE)

WebUI.callTestCase(findTestCase('04初始化設定/新增轉檔格式'), [('teamParam') : Team], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('04初始化設定/新增公告管理'), [('teamParam') : Team], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('04初始化設定/新增iNews匯入'), [('teamParam') : Team], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('04初始化設定/新增iNews匯入元數據配對'), [('teamParam') : Team], FailureHandling.STOP_ON_FAILURE)

//WebUI.callTestCase(findTestCase('04初始化設定/新增同義詞庫'), [('teamParam') : Team], FailureHandling.STOP_ON_FAILURE)



