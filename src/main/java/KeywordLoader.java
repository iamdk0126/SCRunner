import groovy.lang.GroovyClassLoader;
import java.io.File;
import java.nio.file.Files;

public class KeywordLoader {
    private GroovyClassLoader classLoader;

    public KeywordLoader() {
        // 建立一個專屬的 Groovy 類別載入器
        this.classLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
    }

    public void loadKeywords(String projectRoot) {
        File keywordsDir = new File(projectRoot, "Keywords");
        if (!keywordsDir.exists() || !keywordsDir.isDirectory()) {
            System.out.println("⚠️ 找不到 Keywords 資料夾，略過載入。");
            return;
        }

        System.out.println("🔍 開始預先編譯 Custom Keywords...");
        scanAndCompile(keywordsDir);
        System.out.println("✅ Custom Keywords 編譯完成！");
    }

    private void scanAndCompile(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanAndCompile(file); // 繼續往下挖
            } else if (file.getName().endsWith(".groovy")) {
                compileKeyword(file);
            }
        }
    }

    private void compileKeyword(File groovyFile) {
        try {
            String content = Files.readString(groovyFile.toPath());

            // 1. 阻擋原廠套件與變數
            content = content.replaceAll("import\\s+(static\\s+)?com\\.kms\\.katalon\\..*", "// 阻擋 Katalon 套件");
            content = content.replaceAll("import\\s+(static\\s+)?internal\\..*", "// 阻擋 Katalon 變數");

            // 2. 🌟 關鍵：註解掉 @Keyword 標籤 (因為原廠套件被砍了，保留這行會報錯)
            content = content.replaceAll("@Keyword", "// @Keyword");

            // 3. 編譯並註冊到我們專屬的 ClassLoader 裡面
            classLoader.parseClass(content, groovyFile.getName());

        } catch (Exception e) {
            System.err.println("❌ 編譯 Keyword 發生錯誤: " + groovyFile.getName());
            e.printStackTrace();
        }
    }

    public GroovyClassLoader getClassLoader() {
        return classLoader;
    }
}