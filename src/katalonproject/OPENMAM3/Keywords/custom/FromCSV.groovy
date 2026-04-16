package custom

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling
import internal.GlobalVariable
import org.openqa.selenium.Keys
import java.nio.file.Paths

public class FromCSV {

	/**
	 * 從 CSV 讀取多選資料並操作 Ant Design 下拉選單
	 */
	@Keyword
	static void MultiSelect(String fieldTitle, String csvFile, int rowIndex, def column) {
		String cellValue = getValue(csvFile, rowIndex, column)
		if (!cellValue) {
			println("【${fieldTitle}】CSV 無資料，略過操作")
			return
		}

		List<String> options = cellValue.split(';').collect { it.trim() }.findAll { it }

		TestObject selectInput = new TestObject()
		selectInput.addProperty("xpath", ConditionType.EQUALS,
				"//label[@title='${fieldTitle}']/ancestor::div[contains(@class,'ant-form-item')]//input[contains(@class,'ant-select-selection-search-input')]")

		WebUI.waitForElementClickable(selectInput, 10)
		WebUI.click(selectInput)
		WebUI.delay(0.5)

		options.each { optionText ->
			TestObject selectedItem = new TestObject()
			selectedItem.addProperty("xpath", ConditionType.EQUALS,
					"//span[contains(@class,'ant-select-selection-item') and @title='${optionText}']")

			if (WebUI.verifyElementPresent(selectedItem, 1, FailureHandling.OPTIONAL)) {
				return
			}

			TestObject dropdownOption = new TestObject()
			dropdownOption.addProperty("xpath", ConditionType.EQUALS,
					"//div[contains(@class,'ant-select-item-option') and @title='${optionText}']")

			WebUI.waitForElementClickable(dropdownOption, 5)
			WebUI.click(dropdownOption)
			WebUI.delay(0.3)
		}
		WebUI.sendKeys(selectInput, Keys.chord(Keys.ESCAPE))
	}

	/**
	 * 從 CSV 讀取指定欄位的內容並回傳字串
	 * 修正版：處理標頭中含換行符號的問題
	 */
	@Keyword
	static String getValue(String csvFile, int rowIndex, def column) {
		int columnIndex = columnToIndex(column)
		String csvPath = Paths.get(GlobalVariable.LocalFolder, csvFile).toString()
		File file = new File(csvPath)

		if (!file.exists()) {
			println("CSV 檔案不存在：${csvPath}")
			return ""
		}

		// 讀取全文並使用解析器處理 (解決雙引號內有換行與逗號的問題)
		String content = file.getText('UTF-8')
		List<List<String>> allRows = parseCSV(content)

		if (rowIndex > allRows.size()) {
			println("CSV 行數不足 (目標行: ${rowIndex}, 實際總行數: ${allRows.size()})")
			return ""
		}

		List<String> rowData = allRows[rowIndex - 1]

		if (columnIndex >= rowData.size()) {
			println("CSV 欄位不足 (目標索引: ${columnIndex}, 實際欄位數: ${rowData.size()})")
			return ""
		}

		// 取得資料並清除多餘空白或隱藏字元
		String result = rowData[columnIndex]
		return result ? result.trim() : ""
	}

	/**
	 * 將欄位名稱轉成 index (A=0, AA=26...)
	 */
	private static int columnToIndex(def column) {
		if (column instanceof Integer) {
			return column - 1
		}

		String col = column.toString().trim().toUpperCase()
		int index = 0
		for (int i = 0; i < col.length(); i++) {
			index = index * 26 + (col.charAt(i) - 'A'.charAt(0) + 1)
		}
		return index - 1
	}

	/**
	 * 核心解析器：正確處理 CSV 格式
	 * 即使欄位內包含 \n (換行) 或 , (逗號) 也能正確解析
	 */
	private static List<List<String>> parseCSV(String text) {
		List<List<String>> rows = []
		List<String> currentRow = []
		StringBuilder sb = new StringBuilder()
		boolean inQuotes = false

		char[] chars = text.toCharArray()
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i]

			if (c == '"') {
				// 處理連在一起的雙引號 (CSV 的轉義方式: "")
				if (inQuotes && i + 1 < chars.length && chars[i+1] == '"') {
					sb.append('"')
					i++
				} else {
					inQuotes = !inQuotes
				}
			} else if (c == ',' && !inQuotes) {
				// 遇到逗號且不在引號內，結束一個欄位
				currentRow.add(sb.toString())
				sb.setLength(0)
			} else if (c == '\n' && !inQuotes) {
				// 遇到換行且不在引號內，結束一列
				currentRow.add(sb.toString())
				sb.setLength(0)
				rows.add(new ArrayList<>(currentRow))
				currentRow.clear()
			} else if (c == '\r' && !inQuotes) {
				// 忽略 Windows 換行符的 \r
			} else {
				sb.append(c)
			}
		}

		// 處理最後剩下的資料
		if (sb.length() > 0 || !currentRow.isEmpty()) {
			currentRow.add(sb.toString())
			rows.add(currentRow)
		}

		return rows
	}
}