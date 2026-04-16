import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.io.File;
import java.util.Map;

public class WebUI {

    public static void openBrowser(String url) {
        System.out.println("🌐 [WebUI] 開啟瀏覽器並導向: " + url);
        DriverFactory.startBrowser(false);
        if (url != null && !url.isEmpty()) {
            DriverFactory.getDriver().get(url);
        }
    }

    public static void navigateToUrl(String url) {
        System.out.println("🔗 [WebUI] 導向網址: " + url);
        DriverFactory.getDriver().get(url);
    }

    // 🌟 強化版 Click：遇到遮擋自動切換 JS 點擊
    public static void click(KatalonTestObject testObject) {
        System.out.println("🖱️ [WebUI] 點擊物件: " + testObject.objectId);
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(10));

        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(testObject.toSeleniumBy(null)));
        try {
            // 嘗試正常點擊
            element.click();
        } catch (Exception e) {
            // 如果被遮擋報錯，啟動備用方案：JavaScript 強制點擊
            System.out.println("⚠️ [WebUI] 正常點擊被遮擋，啟動 JS 強制點擊...");
            JavascriptExecutor executor = (JavascriptExecutor) DriverFactory.getDriver();
            executor.executeScript("arguments[0].click();", element);
        }
    }

    public static void setText(KatalonTestObject testObject, String text) {
        System.out.println("⌨️ [WebUI] 輸入文字 [" + text + "] 到物件: " + testObject.objectId);
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(10));

        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(testObject.toSeleniumBy(null)));
        element.clear();
        element.sendKeys(text);
    }

    // 🌟 新增功能：直接對目標模擬按下鍵盤 Enter 鍵
    public static void sendKeysEnter(KatalonTestObject testObject) {
        System.out.println("⏎ [WebUI] 對物件送出 Enter 鍵: " + testObject.objectId);
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(10));

        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(testObject.toSeleniumBy(null)));
        element.sendKeys(Keys.ENTER);
    }

    public static void delay(int seconds) {
        System.out.println("⏳ [WebUI] 等待 " + seconds + " 秒...");
        try { Thread.sleep(seconds * 1000L); } catch (Exception ignored) {}
    }

    public static void closeBrowser() {
        System.out.println("🛑 [WebUI] 關閉瀏覽器");
        DriverFactory.quitBrowser();
    }

    // 建立一個靜態變數，讓 WebUI 可以操作 ScriptRunner
    private static ScriptRunner currentRunner;

    public static void setScriptRunner(ScriptRunner runner) {
        currentRunner = runner;
    }

    // 🌟 神奇的 callTestCase 誕生了！
    public static void callTestCase(File scriptFile, Map<String, Object> variables, FailureHandling handling) {
        if (scriptFile == null) {
            System.err.println("⚠️ [WebUI] 無法執行 callTestCase：目標腳本不存在。");
            return;
        }

        System.out.println("🔄 [WebUI] 進入子腳本呼叫 (callTestCase) -> " + scriptFile.getParentFile().getName());

        try {
            // 遞迴呼叫 ScriptRunner 來跑子腳本！
            currentRunner.runScript(scriptFile, variables);
            System.out.println("↩️ [WebUI] 子腳本執行完畢，返回主腳本。");
        } catch (Exception e) {
            System.err.println("❌ [WebUI] 子腳本執行失敗：" + e.getMessage());
            if (handling == FailureHandling.STOP_ON_FAILURE) {
                throw new RuntimeException("測試因 STOP_ON_FAILURE 終止");
            }
        }
    }

    // 🌟 支援腳本中的註解輸出
    public static void comment(String message) {
        System.out.println("💬 [WebUI 註解] " + message);
    }

}