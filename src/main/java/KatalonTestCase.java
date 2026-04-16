import java.io.File;

public class KatalonTestCase {
    private String testCaseId;   // 例如: "Test Cases/登入1"
    private File scriptFile;     // 指向 Scripts/登入1/ScriptXXXXXXXX.groovy

    public KatalonTestCase(String testCaseId, File scriptFile) {
        this.testCaseId = testCaseId;
        this.scriptFile = scriptFile;
    }

    public String getTestCaseId() { return testCaseId; }
    public File getScriptFile() { return scriptFile; }

    @Override
    public String toString() {
        return "TestCase: " + testCaseId + " -> " + (scriptFile != null ? scriptFile.getName() : "MISSING");
    }
}