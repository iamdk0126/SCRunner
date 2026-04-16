import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import groovy.lang.GroovyShell;

public class ProfileLoader {
    // 這裡用來存放所有的 GlobalVariable，Key 是變數名稱，Value 是實際的值
    private Map<String, Object> globalVariables = new HashMap<>();

    public void loadProfile(String projectPath, String profileName) {
        File glblFile = new File(projectPath, "Profiles/" + profileName + ".glbl");

        if (!glblFile.exists()) {
            System.err.println("⚠️ 找不到 Profile 檔案: " + glblFile.getAbsolutePath());
            return;
        }

        try {
            System.out.println("🔍 開始解析全域變數 Profile: " + profileName + ".glbl ...");
            SAXReader reader = new SAXReader();
            Document document = reader.read(glblFile);

            // 抓取所有的變數節點
            List<Node> entities = document.selectNodes("//GlobalVariableEntity");

            // 借用 Groovy 引擎來幫我們把字串安全地轉換成真實的 Java 物件
            GroovyShell shell = new GroovyShell();

            for (Node node : entities) {
                String name = node.selectSingleNode("name").getText();
                String initValue = node.selectSingleNode("initValue").getText();

                // 將 Katalon 儲存的格式 (如 'my_string' 或 10) 轉換為真正的物件
                Object realValue = shell.evaluate(initValue);

                globalVariables.put(name, realValue);
            }

            System.out.println("✅ 載入完成！共讀取了 " + globalVariables.size() + " 個全域變數。");

        } catch (Exception e) {
            System.err.println("❌ 解析 Profile 失敗: " + e.getMessage());
        }
    }

    // 未來我們準備把它注入到腳本引擎中
    public Map<String, Object> getGlobalVariables() {
        return globalVariables;
    }
}