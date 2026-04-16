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

import java.sql.Connection
import java.sql.Statement
import java.sql.ResultSet

public class DBUtils {

	static List<Map<String, Object>> query(String sql) {
		List<Map<String,Object>> results = []
		Connection conn = DBManager.getConnection()
		Statement stmt = conn.createStatement()
		ResultSet rs = stmt.executeQuery(sql)
		def meta = rs.metaData
		int colCount = meta.columnCount

		while(rs.next()) {
			Map<String,Object> row = [:]
			for(int i=1;i<=colCount;i++) {
				row[meta.getColumnName(i)] = rs.getObject(i)
			}
			results.add(row)
		}
		rs.close()
		stmt.close()
		conn.close()
		return results
	}

	static int update(String sql) {
		Connection conn = DBManager.getConnection()
		Statement stmt = conn.createStatement()
		int affected = stmt.executeUpdate(sql)
		stmt.close()
		conn.close()
		return affected
	}
}