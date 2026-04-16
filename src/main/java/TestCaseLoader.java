import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TestCaseLoader {
    private Map<String, KatalonTestCase> testCaseCache = new HashMap<>();
    private String projectRoot;

    public void loadTestCases(String projectRoot) {
        this.projectRoot = projectRoot;
        File tcDir = new File(projectRoot, "Test Cases");

        if (!tcDir.exists()) {
            System.err.println("❌ 找不到 Test Cases 資料夾");
            return;
        }

        System.out.println("🔍 開始掃描測試案例...");
        scanTCFiles(tcDir, "");
        System.out.println("✅ 掃描完成！共載入 " + testCaseCache.size() + " 個測試案例。");
    }

    private void scanTCFiles(File dir, String relativePath) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                // 遞迴掃描子目錄
                scanTCFiles(file, relativePath + file.getName() + "/");
            } else if (file.getName().endsWith(".tc")) {
                // 1. 取得 Case ID (例如: Test Cases/登入1)
                String caseName = file.getName().replace(".tc", "");
                String testCaseId = "Test Cases/" + relativePath + caseName;

                // 2. 尋找對應的 Scripts 檔案
                // 規則: Scripts/[相對路徑+Case名稱]/ScriptXXXXXXXX.groovy
                File scriptDir = new File(projectRoot, "Scripts/" + relativePath + caseName);
                File groovyFile = findGroovyInDir(scriptDir);

                if (groovyFile != null) {
                    testCaseCache.put(testCaseId, new KatalonTestCase(testCaseId, groovyFile));
                } else {
                    System.err.println("⚠️ 找不到腳本實作: " + testCaseId);
                }
            }
        }
    }

    private File findGroovyInDir(File dir) {
        if (!dir.exists() || !dir.isDirectory()) return null;
        File[] files = dir.listFiles((d, name) -> name.endsWith(".groovy"));
        return (files != null && files.length > 0) ? files[0] : null;
    }

    public KatalonTestCase getTestCase(String testCaseId) {
        return testCaseCache.get(testCaseId);
    }

    public Map<String, KatalonTestCase> getAllTestCases() {
        return testCaseCache;
    }
}