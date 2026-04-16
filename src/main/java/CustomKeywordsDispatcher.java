import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObjectSupport;
import org.codehaus.groovy.runtime.InvokerHelper;

// 繼承 GroovyObjectSupport 讓我們可以攔截腳本裡的動態方法呼叫
public class CustomKeywordsDispatcher extends GroovyObjectSupport {
    private GroovyClassLoader classLoader;

    public CustomKeywordsDispatcher(GroovyClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        try {
            // name 的格式會是 "custom.AuthKeywords.login" (或類似你的腳本寫法)
            int lastDot = name.lastIndexOf('.');
            String className = name.substring(0, lastDot);
            String methodName = name.substring(lastDot + 1);

            // 動態載入我們預先編譯好的 Keyword 類別
            Class<?> clazz = classLoader.loadClass(className);
            Object instance = clazz.getDeclaredConstructor().newInstance();

            // 執行對應的方法並回傳結果
            System.out.println("🔧 [Keyword] 執行自定義關鍵字: " + name);
            return InvokerHelper.invokeMethod(instance, methodName, args);

        } catch (Exception e) {
            throw new RuntimeException("執行 Custom Keyword 失敗 [" + name + "]: " + e.getMessage(), e);
        }
    }
}