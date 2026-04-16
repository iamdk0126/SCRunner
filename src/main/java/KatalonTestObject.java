import org.openqa.selenium.By;
import java.util.Map;

public class KatalonTestObject {
    public String objectId;      // 記錄物件名稱
    public String selectorType;  // XPATH, CSS, BASIC
    public String selectorValue; // 實際的定位字串

    // 將 Katalon 格式轉換為 Selenium 格式
    public By toSeleniumBy(Map<String, String> variables) {
        String finalValue = selectorValue;

        // 如果有變數 (如 ${id})，在這裡進行替換
        if (variables != null && !variables.isEmpty()) {
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                finalValue = finalValue.replace("${" + entry.getKey() + "}", entry.getValue());
            }
        }

        if (selectorType.equals("CSS")) {
            return By.cssSelector(finalValue);
        } else {
            // XPATH 和轉換後的 BASIC 都走這裡
            return By.xpath(finalValue);
        }
    }
}