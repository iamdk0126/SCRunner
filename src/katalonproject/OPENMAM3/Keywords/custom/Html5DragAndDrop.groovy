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
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import org.openqa.selenium.WebElement
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.By

import internal.GlobalVariable

public class Html5DragAndDrop {
	@Keyword
	static boolean dragAndDrop(TestObject source, TestObject target) {

		WebElement srcElement = WebUI.findWebElement(source, 10)
		WebElement tgtElement = WebUI.findWebElement(target, 10)

		String js = """
            function fireDragEvent(type, elem, dataTransfer) {
                var evt = document.createEvent('CustomEvent');
                evt.initCustomEvent(type, true, true, null);
                evt.dataTransfer = dataTransfer;
                elem.dispatchEvent(evt);
            }

            var src = arguments[0];
            var tgt = arguments[1];

            var dataTransfer = {
                data: {},
                setData: function(type, val) { this.data[type] = val; },
                getData: function(type) { return this.data[type]; }
            };

            fireDragEvent('dragstart', src, dataTransfer);
            fireDragEvent('dragenter', tgt, dataTransfer);
            fireDragEvent('dragover', tgt, dataTransfer);
            fireDragEvent('drop', tgt, dataTransfer);
            fireDragEvent('dragend', src, dataTransfer);
        """

		WebUI.executeJavaScript(js, Arrays.asList(srcElement, tgtElement))
	}
	
}
