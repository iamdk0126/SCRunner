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
import com.kms.katalon.core.testobject.ConditionType

public class TeamKeywords {
	/**
	 * 點擊指定的 Team (ex: "Team1", "Team2", "Team3")
	 * @param teamName e.g. "Team1"
	 */
	@Keyword
	def clickTeam(String teamName) {
		
		// 從 teamName 裡取出數字 (Team1 -> 1, Team2 -> 2)
		String teamNumber = teamName.replaceAll("\\D+", "")  // 把非數字移除
		
		if (GlobalVariable.ForceTeam) { teamNumber = GlobalVariable.ForceTeamNumber } //可用來強制 第幾個資源組
		

		// 動態建立 XPath
		String xpath = "//div[@data-node-key='" + teamNumber + "']"

		// 動態建立 TestObject
		TestObject dynamicTeam = new TestObject(teamName)
		dynamicTeam.addProperty("xpath", ConditionType.EQUALS, xpath)

		// 執行點擊
		WebUI.click(dynamicTeam)
	}
}
