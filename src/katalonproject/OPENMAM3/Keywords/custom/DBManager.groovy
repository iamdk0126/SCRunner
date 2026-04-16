package custom

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows

import internal.GlobalVariable

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection


public class DBManager {
	private static HikariDataSource dataSource = null
	private static int activeCount = 0
	private static final Object lock = new Object()

	// 初始化連線池
	static void initPool(String jdbcUrl, String user, String pass) {
		synchronized(lock) {
			if (dataSource == null) {
				println "[DBManager] 初始化連線池..."
				HikariConfig config = new HikariConfig()
				config.setJdbcUrl(jdbcUrl)
				config.setUsername(user)
				config.setPassword(pass)
				config.setMaximumPoolSize(10)
				dataSource = new HikariDataSource(config)
			}
			activeCount++
			println "[DBManager] 活動計數: ${activeCount}"
		}
	}

	// 取得連線
	static Connection getConnection() {
		if (dataSource == null) {
			throw new RuntimeException("DBManager 尚未初始化連線池")
		}
		return dataSource.getConnection()
	}

	// 減少活動計數，最後一個 Test Suite 關閉池
	static void releasePool() {
		synchronized(lock) {
			activeCount--
			println "[DBManager] 減少計數: ${activeCount}"
			if (activeCount <= 0 && dataSource != null) {
				println "[DBManager] 關閉連線池..."
				dataSource.close()
				dataSource = null
			}
		}
	}
}