import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ObjectRepositoryLoader {
    // 這裡就是我們的「物件字典」，Key 是 Katalon 的路徑，Value 是我們剛剛寫的解析物件
    private Map<String, KatalonTestObject> objectCache = new HashMap<>();
    private String baseRepoPath;

    // 啟動載入程序
    public void loadRepository(String projectPath) {
        File repoDir = new File(projectPath, "Object Repository");
        if (!repoDir.exists() || !repoDir.isDirectory()) {
            System.err.println("❌ 找不到 Object Repository 資料夾！請確認路徑: " + repoDir.getAbsolutePath());
            return;
        }

        baseRepoPath = repoDir.getAbsolutePath();
        System.out.println("🔍 開始掃描物件庫...");
        scanDirectory(repoDir);
        System.out.println("✅ 掃描完成！共載入 " + objectCache.size() + " 個測試物件。");
    }

    // 遞迴掃描資料夾（會自動鑽進子資料夾）
    private void scanDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file); // 如果是資料夾，繼續往下挖
            } else if (file.getName().endsWith(".rs")) {
                parseAndStore(file); // 如果是 .rs 檔，進行解析
            }
        }
    }

    // 解析並存入字典
    private void parseAndStore(File rsFile) {
        try {
            String absolutePath = rsFile.getAbsolutePath();

            // 計算出 Katalon 需要的相對路徑 (例如: 系統設定/使用者管理)
            // 1. 拔掉根目錄路徑
            String relativePath = absolutePath.substring(baseRepoPath.length() + 1);
            // 2. 拔掉 .rs 副檔名
            relativePath = relativePath.replace(".rs", "");
            // 3. 把 Windows 的反斜線(\) 統一代換成 Katalon 腳本用的斜線(/)
            String objectId = relativePath.replace("\\", "/");

            // 呼叫我們剛剛寫的 RsParser！
            KatalonTestObject obj = RsParser.parse(rsFile, objectId);
            objectCache.put(objectId, obj);

        } catch (Exception e) {
            System.err.println("⚠️ 解析失敗 (略過): " + rsFile.getName() + " - 原因: " + e.getMessage());
        }
    }

    // 未來給腳本呼叫用的方法 (等同於 Katalon 的 findTestObject)
    public KatalonTestObject getObject(String objectId) {
        return objectCache.get(objectId);
    }
}