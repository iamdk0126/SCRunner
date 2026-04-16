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
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.Random
import java.io.File

public class XmlReader {
	/**
	 * 從指定 XML 檔案隨機取得一筆 video-id + title
	 * xmlPath: 完整檔案路徑
	 * return: [videoId, title] 或 null（找不到）
	 */
	static List<String> getRandomVideoAndTitle(String xmlPath) {
		File xmlFile = new File(xmlPath)
		if (!xmlFile.exists() || xmlFile.length() == 0) {
			return null
		}

		def xmlContent = xmlFile.text

		// 使用 Jsoup 容錯解析
		Document doc = Jsoup.parse(xmlContent, '', org.jsoup.parser.Parser.xmlParser())

		// 找出所有 <story> 底下的 <f id=video-id> 與 <f id=title>
		def stories = doc.select('story')

		if (stories.isEmpty()) {
			return null
		}

		List<Map> videoList = []

		stories.each { story ->
			def videoIdElem = story.select('f[id=video-id]').first()
			def titleElem = story.select('f[id=title]').first()
			if (videoIdElem != null && titleElem != null) {
				videoList.add([videoId: videoIdElem.text().trim(),
					title: titleElem.text().trim()])
			}
		}

		if (videoList.isEmpty()) {
			return null
		}

		// 隨機挑一筆
		Random random = new Random()
		int idx = random.nextInt(videoList.size())
		def picked = videoList[idx]

		return [picked.videoId, picked.title]
	}
	
	/**
	 * 從指定 XML 檔案依序取得前 N 筆 video-id + title
	 * xmlPath: XML 完整路徑
	 * n: 取前 N 筆
	 * return: List<Map> [[videoId:'...', title:'...'], ...]，找不到或 N=0 則回空 List
	 */
	static List<Map<String, String>> getFirstNVideoAndTitle(String xmlPath, int n) {
		if (n <= 0) return []

		File xmlFile = new File(xmlPath)
		if (!xmlFile.exists() || xmlFile.length() == 0) return []

		def xmlContent = xmlFile.text
		Document doc = Jsoup.parse(xmlContent, '', org.jsoup.parser.Parser.xmlParser())
		def stories = doc.select('story')
		if (stories.isEmpty()) return []

		List<Map<String, String>> videoList = []
		stories.each { story ->
			def videoIdElem = story.select('f[id=video-id]').first()
			def titleElem = story.select('f[id=title]').first()
			if (videoIdElem != null && titleElem != null) {
				videoList.add([videoId: videoIdElem.text().trim(), title: titleElem.text().trim()])
			}
		}

		return videoList.take(n)
	}
}
