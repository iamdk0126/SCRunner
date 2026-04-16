public class Main {
    public static void main(String[] args) {
        System.out.println("=== 🚀 輕量化 Katalon 執行器 (動態目錄映射測試) ===");

        String projectRoot = "C:\\Users\\gouhwalee\\Documents\\OPENMAM3"; // 換成你的路徑

        try {
            // 1. 載入物件庫與全域變數
            ObjectRepositoryLoader objLoader = new ObjectRepositoryLoader();
            objLoader.loadRepository(projectRoot);

            ProfileLoader profileLoader = new ProfileLoader();
            profileLoader.loadProfile(projectRoot, "default");

            // 2. 🌟 載入所有測試案例 (建立 TC 與 Groovy 的對應)
            TestCaseLoader tcLoader = new TestCaseLoader();
            tcLoader.loadTestCases(projectRoot);

            // 🌟 新增：載入所有的 Custom Keywords
            KeywordLoader keywordLoader = new KeywordLoader();
            keywordLoader.loadKeywords(projectRoot);

            // 3. 初始化腳本引擎
            ScriptRunner runner = new ScriptRunner(objLoader, profileLoader, tcLoader, keywordLoader, projectRoot);

            // 4. 🎯 直接指定想要執行的 Test Case ID (不需要寫死 .groovy 檔名)
            String myTargetCase = "Test Cases/08二階審核/LTCase01登入登出";

            KatalonTestCase tc = tcLoader.getTestCase(myTargetCase);

            if (tc != null) {
                System.out.println("📍 找到目標: " + tc.getTestCaseId());
                System.out.println("📄 對應腳本: " + tc.getScriptFile().getAbsolutePath());

                // 執行
                runner.runScript(tc.getScriptFile(), null);
            } else {
                System.err.println("❌ 找不到測試案例: " + myTargetCase);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            WebUI.closeBrowser();
            System.out.println("=== 🎉 執行結束 ===");
        }
    }
}