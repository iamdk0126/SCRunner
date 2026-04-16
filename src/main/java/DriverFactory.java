import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.Collections;

public class DriverFactory {
    // 使用 ThreadLocal 確保多執行緒執行時，瀏覽器不會互相干擾
    private static ThreadLocal<WebDriver> threadLocalDriver = new ThreadLocal<>();

    public static void startBrowser(boolean headless) {
        ChromeOptions options = new ChromeOptions();

        // 解決一些跨域或安全性限制的常見參數
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-notifications");

        // 🌟 反爬蟲偽裝術：隱藏自動化特徵
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        // 偽裝 User-Agent (假裝自己是普通使用者的瀏覽器)
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        if (headless) {
            options.addArguments("--headless=new"); // 使用新版無頭模式
            options.addArguments("--window-size=1920,1080"); // 確保無頭模式下解析度夠大
        }

        // Selenium 4 內建 Selenium Manager，會自動下載對應版本的 ChromeDriver！
        WebDriver driver = new ChromeDriver(options);

        // 視窗最大化
        if (!headless) {
            driver.manage().window().maximize();
        }

        threadLocalDriver.set(driver);
    }

    public static WebDriver getDriver() {
        return threadLocalDriver.get();
    }

    public static void quitBrowser() {
        if (getDriver() != null) {
            getDriver().quit();
            threadLocalDriver.remove();
        }
    }
}