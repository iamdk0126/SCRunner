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
import com.kms.katalon.core.testobject.ConditionType

import internal.GlobalVariable

public class TableKeywords {
	/**
	 * 點擊指定名稱前面的 checkbox
	 * @param name 欄位顯示的名稱文字
	 */
	@Keyword
	def clickCheckboxByName(String name) {
		// 建立動態 TestObject
		TestObject dynamicCheckbox = new TestObject("dynamicCheckbox")

		// 動態建立 XPath
		String xpath = "//td[.//span[text()='" + name + "']]/preceding-sibling::td//input[@type='checkbox']"
		println("Generated XPath: " + xpath)

		// 加入屬性
		dynamicCheckbox.addProperty("xpath", ConditionType.EQUALS, xpath)

		// 等待元素出現再點擊
		WebUI.waitForElementPresent(dynamicCheckbox, 10)
		WebUI.check(dynamicCheckbox)
	}
}
