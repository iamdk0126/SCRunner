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
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.NodeList
import java.util.Random
import java.io.File

public class XmlVideoPicker {
	/**
	 * 從指定 XML 檔案中隨機挑選一筆 video-id 與 title
	 * @param xmlFilePath XML 檔完整路徑
	 * @return Map [videoId: xxx, title: yyy]，找不到會回傳 null
	 */
	static Map pickRandomVideo(String xmlFilePath) {
		File xmlFile = new File(xmlFilePath)
		if (!xmlFile.exists() || xmlFile.length() == 0) return null

		def docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
		def doc = docBuilder.parse(xmlFile)
		doc.getDocumentElement().normalize()

		NodeList videoNodes = doc.getElementsByTagName("fields") // 每筆媒資對應一個 <fields>
		if (videoNodes.getLength() == 0) return null

		Random random = new Random()
		int idx = random.nextInt(videoNodes.getLength())
		def fieldsNode = videoNodes.item(idx)

		NodeList fList = fieldsNode.getElementsByTagName("f")
		String videoId = null
		String title = null

		for (int i = 0; i < fList.getLength(); i++) {
			def fNode = fList.item(i)
			def idAttr = fNode.getAttributes().getNamedItem("id")?.getTextContent()
			if (idAttr == "video-id") videoId = fNode.getTextContent().trim()
			if (idAttr == "title") videoId = fNode.getTextContent().trim()
		}

		// 如果找到 title 與 video-id
		if (videoId && title) {
			return [videoId: videoId, title: title]
		}

		return null
	}
}
