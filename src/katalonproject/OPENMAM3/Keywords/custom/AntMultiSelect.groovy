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

public class AntMultiSelect {

	/**
	 * 從 meta.csv 讀取多選資料
	 *
	 * @param fieldTitle 欄位 title，例如：頻道
	 * @param csvFile    CSV 檔名，例如：meta.csv
	 * @param rowIndex   第幾列（從 1 開始）
	 * @param column     CSV 欄位（可以是 1 / "A" / "G" / "AT" 等）
	 */
	@Keyword
	static void selectByCsvColumnFromLocal(
			String fieldTitle,
			String csvFile,
			int rowIndex,
			def column
	) {
		// =========================
		// 欄位轉 index
		// =========================
		int columnIndex = columnToIndex(column)

		// =========================
		// 組 CSV 完整路徑
		// =========================
		String csvPath = Paths.get(GlobalVariable.LocalFolder, csvFile).toString()
		File file = new File(csvPath)
		if (!file.exists()) {
			println("CSV 檔案不存在：${csvPath}")
			return
		}

		// =========================
		// 讀 CSV
		// =========================
		List<String> lines = file.readLines('UTF-8')
		if (rowIndex > lines.size()) {
			println("CSV 行數不足，rowIndex=${rowIndex}")
			return
		}

		String line = lines[rowIndex - 1]  // rowIndex 從 1 開始
		def columns = line.split(',')
		if (columnIndex >= columns.size()) {
			println("CSV 欄位不足，column=${column}")
			return
		}

		String cellValue = columns[columnIndex].trim()
		if (!cellValue) {
			println("【${fieldTitle}】CSV 無資料，略過")
			return
		}

		// 支援 ; 分隔（結尾多 ; 不會出錯）
		List<String> options = cellValue.split(';').collect { it.trim() }.findAll { it }

		// =========================
		// 找到 ant-select 的 input（真正可點）
		// =========================
		TestObject selectInput = new TestObject()
		selectInput.addProperty(
				"xpath",
				ConditionType.EQUALS,
				"//label[@title='${fieldTitle}']" +
				"/ancestor::div[contains(@class,'ant-form-item')]" +
				"//input[contains(@class,'ant-select-selection-search-input')]"
				)

		WebUI.waitForElementClickable(selectInput, 10)
		WebUI.click(selectInput)
		WebUI.delay(0.5)

		// =========================
		// 多選點擊
		// =========================
		options.each { optionText ->
			// 已選取的不再點（避免取消）
			TestObject selectedItem = new TestObject()
			selectedItem.addProperty(
					"xpath",
					ConditionType.EQUALS,
					"//span[contains(@class,'ant-select-selection-item') and @title='${optionText}']"
					)

			if (WebUI.verifyElementPresent(selectedItem, 1, FailureHandling.OPTIONAL)) {
				println("已選取，略過：${optionText}")
				return
			}

			TestObject dropdownOption = new TestObject()
			dropdownOption.addProperty(
					"xpath",
					ConditionType.EQUALS,
					"//div[contains(@class,'ant-select-item-option') and @title='${optionText}']"
					)

			WebUI.waitForElementClickable(dropdownOption, 5)
			WebUI.click(dropdownOption)
			WebUI.delay(0.3)
		}

		WebUI.sendKeys(selectInput, Keys.chord(Keys.ESCAPE))
	}

	/**
	 * 將欄位轉成 index
	 * 支援數字（1、2、3…）及英文字母（A、B、G、AT…）
	 * 回傳 index 從 0 開始
	 */
	private static int columnToIndex(def column) {
		if (column instanceof Integer) {
			return column - 1   // 從 1 開始轉 0
		}

		String col = column.toString().trim().toUpperCase()
		int index = 0
		col.each { c ->
			index = index * 26 + (c.charAt(0) - 'A'.charAt(0) + 1)
		}
		return index - 1
	}
}
