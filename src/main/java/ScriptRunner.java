import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration; // 🌟 新增：編譯器設定
import java.io.File;
import java.nio.file.Files;
import java.util.Collections; // 🌟 新增
import java.util.Map;

public class ScriptRunner {
    private ObjectRepositoryLoader repoLoader;
    private ProfileLoader profileLoader;
    private TestCaseLoader tcLoader;
    private KeywordLoader keywordLoader;
    private String projectRoot;

    public ScriptRunner(ObjectRepositoryLoader repoLoader, ProfileLoader profileLoader, TestCaseLoader tcLoader, KeywordLoader keywordLoader, String projectRoot) {
        this.repoLoader = repoLoader;
        this.profileLoader = profileLoader;
        this.tcLoader = tcLoader;
        this.keywordLoader = keywordLoader;
        this.projectRoot = projectRoot;
        WebUI.setScriptRunner(this);
    }

    public void runScript(File groovyFile, Map<String, Object> passedVariables) throws Exception {
        System.out.println("\n▶️ 開始執行腳本: " + groovyFile.getName());

        String scriptContent = Files.readString(groovyFile.toPath());

        // 阻擋 Katalon 原廠套件與變數
        scriptContent = scriptContent.replaceAll("import\\s+(static\\s+)?com\\.kms\\.katalon\\..*", "// 阻擋 Katalon 原廠套件");
        scriptContent = scriptContent.replaceAll("import\\s+(static\\s+)?internal\\..*", "// 阻擋 Katalon 原廠變數");

        Binding binding = new Binding();
        binding.setVariable("WebUI", WebUI.class);
        binding.setVariable("GlobalVariable", profileLoader.getGlobalVariables());
        binding.setVariable("FailureHandling", FailureHandling.class);

        // 🌟 把我們剛剛寫的攔截器注入進去，讓腳本認識 "CustomKeywords"
        binding.setVariable("CustomKeywords", new CustomKeywordsDispatcher(keywordLoader.getClassLoader()));

        if (passedVariables != null) {
            for (Map.Entry<String, Object> entry : passedVariables.entrySet()) {
                binding.setVariable(entry.getKey(), entry.getValue());
            }
        }

        binding.setVariable("findTestObject", new Closure<KatalonTestObject>(this) {
            public KatalonTestObject doCall(String objectId) {
                return repoLoader.getObject(objectId);
            }
        });

        binding.setVariable("findTestCase", new Closure<File>(this) {
            public File doCall(String testCaseId) {
                KatalonTestCase tc = tcLoader.getTestCase(testCaseId);
                if (tc != null) {
                    return tc.getScriptFile();
                }
                System.err.println("❌ 找不到 Test Case: " + testCaseId);
                return null;
            }
        });

        /* ---------------------------------------------------------
         * 🌟 關鍵升級：動態編譯 Custom Keywords
         * ---------------------------------------------------------*/
        CompilerConfiguration config = new CompilerConfiguration();
        File keywordsDir = new File(projectRoot, "Keywords");

        if (keywordsDir.exists() && keywordsDir.isDirectory()) {
            // 將 Keywords 資料夾加入 Groovy 的尋找路徑中
            config.setClasspathList(Collections.singletonList(keywordsDir.getAbsolutePath()));
        } else {
            System.err.println("⚠️ 找不到 Keywords 資料夾，若腳本無自定義關鍵字可忽略。");
        }

        // 🌟 初始化引擎時，把 config 傳進去
        GroovyShell shell = new GroovyShell(keywordLoader.getClassLoader(), binding);

        // 執行腳本
        shell.evaluate(scriptContent);
    }
}