import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject

import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile

import internal.GlobalVariable as GlobalVariable

import com.kms.katalon.core.annotation.BeforeTestCase
import com.kms.katalon.core.annotation.BeforeTestSuite
import com.kms.katalon.core.annotation.AfterTestCase
import com.kms.katalon.core.annotation.AfterTestSuite
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.context.TestSuiteContext
import com.kms.katalon.core.util.KeywordUtil

class CleanupListener {
	/**
     * 在 Test Suite 開始前執行：大清掃
     */
    @BeforeTestSuite
    def sampleBeforeTestSuite(TestSuiteContext testSuiteContext) {
        KeywordUtil.logInfo("--- [BeforeTestSuite] 執行預防性大清理 ---")
        cleanOrphanProcesses()
    }

    /**
     * 在每個 Test Case 結束後執行：即時回收
     */
    @AfterTestCase
    def sampleAfterTestCase(TestCaseContext testCaseContext) {
        KeywordUtil.logInfo("--- [AfterTestCase] 執行例行性資源回收 ---")
        
        // 確保先透過 WebUI 正常關閉，如果已經關閉了，此指令會被忽略
        try { WebUI.closeBrowser() } catch (Exception e) { }
        
        // 強制殺掉該 Case 可能遺留的孤兒程序
        cleanOrphanProcesses()
        
        // 提醒 JVM 回收記憶體，減緩 UI 壓力
        System.gc()
    }

    /**
     * 核心清理邏輯：使用 PowerShell 精準殺掉沒有父程序的驅動與瀏覽器
     */
    def cleanOrphanProcesses() {
        try {
            // 清理無主 chromedriver
            def cleanDriver = 'powershell.exe "Get-CimInstance Win32_Process -Filter \\"name = \'chromedriver.exe\'\\" | Where-Object { (Get-Process -Id $_.ParentProcessId -ErrorAction SilentlyContinue) -eq $null } | Stop-Process -Force"'
            Runtime.getRuntime().exec(cleanDriver)

            // 清理無主 chrome
            def cleanChrome = 'powershell.exe "Get-CimInstance Win32_Process -Filter \\"name = \'chrome.exe\'\\" | Where-Object { (Get-Process -Id $_.ParentProcessId -ErrorAction SilentlyContinue) -eq $null } | Stop-Process -Force"'
            Runtime.getRuntime().exec(cleanChrome)
            
        } catch (Exception e) {
            KeywordUtil.logInfo("清理指令執行中斷 (可能目前無殘留): " + e.message)
        }
    }
}