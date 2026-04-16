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
import com.kms.katalon.core.testobject.ConditionType as ConditionType
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import custom.WebUIExtensions as WebUIExtensions
import custom.Html5DragAndDrop
import custom.DropdownHelper
import java.text.SimpleDateFormat

String keywordStr = keywordParam // 使用在 'Variables' 頁籤定義的變數
int index = indexParam 
String indexStr = "$index"
String timeStr = new SimpleDateFormat("HHmmss").format(new Date())  //取得現在時間

WebUIExtensions.retryClick(findTestObject('主畫面/公開媒資'))

'點addEDL1'
WebUIExtensions.retryClick(findTestObject('Header/EDL/edledit1'))

WebUIExtensions.retryClick(findTestObject('Header/EDL/edledit2'))

WebUIExtensions.retryClick(findTestObject('Header/EDL/edledit3'))

WebUI.delay(5)

WebUIExtensions.retryClick(findTestObject('Header/影片剪輯'))



//println('已完成呼叫 EDL')

//WebUI.dragAndDropToObject(findTestObject('Header/EDL/img1'), findTestObject('Header/EDL/時間軸'))

TestObject img1 = findTestObject('Header/EDL/img1')
TestObject img2 = findTestObject('Header/EDL/img2')
TestObject img3 = findTestObject('Header/EDL/img3')
TestObject dropZone = findTestObject('Header/EDL/時間軸')
Html5DragAndDrop.dragAndDrop(img2, dropZone)
WebUI.delay(2)
Html5DragAndDrop.dragAndDrop(img1, dropZone)
WebUI.delay(2)
Html5DragAndDrop.dragAndDrop(img3, dropZone)

//WebUIExtensions.retryClick(findTestObject('Header/EDL/playvideo'))
TestObject playButton = new TestObject('playButton')
playButton.addProperty('css', ConditionType.EQUALS, '.playerControls___1iXX9 button:nth-of-type(3)')
WebUI.click(playButton)

WebUI.delay(60)

WebUI.click(playButton)
//WebUIExtensions.retryClick(findTestObject('Header/EDL/playvideo'))
WebUI.delay(2)

WebUIExtensions.retryClick(findTestObject('Header/EDL/匯出'))

WebUI.delay(2)

WebUIExtensions.setTextByLabel('檔案命名', "EDL_"+ indexStr + "_" + timeStr)

WebUI.delay(2)

DropdownHelper.selectDropdownOptionByLabel('檔案格式','uhd, mxf')

WebUIExtensions.retryClick(findTestObject('Header/EDL/匯出檔案頁/匯出'))

WebUI.delay(2)

WebUIExtensions.retryClick(findTestObject('Header/EDL/trashcan'))

WebUIExtensions.retryClick(findTestObject('Header/EDL/trashcan'))

WebUIExtensions.retryClick(findTestObject('Header/EDL/trashcan'))

WebUIExtensions.retryClick(findTestObject('Header/EDL/clearall'))

WebUIExtensions.retryClick(findTestObject('Header/EDL/clearall_ok'))

WebUI.delay(2)

WebUIExtensions.retryClick(findTestObject('Header/OPENMAM'))
/*
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

WebUI.executeJavaScript(js, Arrays.asList(
		WebUI.findWebElement(img, 10),
		WebUI.findWebElement(dropZone, 10)
))
*/

println('已完成呼叫 EDL')
