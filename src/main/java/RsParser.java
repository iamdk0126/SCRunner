import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import java.io.File;
import java.util.List;

public class RsParser {
    public static KatalonTestObject parse(File rsFile, String objectId) throws Exception {
        SAXReader reader = new SAXReader();
        Document document = reader.read(rsFile);

        KatalonTestObject testObj = new KatalonTestObject();
        testObj.objectId = objectId;

        // 1. 取得 Katalon 的定位方法
        Node methodNode = document.selectSingleNode("//selectorMethod");
        testObj.selectorType = methodNode.getText();

        // 2. 根據方法抓取路徑
        if (testObj.selectorType.equals("XPATH") || testObj.selectorType.equals("CSS")) {
            String xpathQuery = String.format("//selectorCollection/entry[key='%s']/value", testObj.selectorType);
            Node valueNode = document.selectSingleNode(xpathQuery);
            if (valueNode != null) {
                testObj.selectorValue = valueNode.getText();
            }
        } else if (testObj.selectorType.equals("BASIC")) {
            testObj.selectorValue = buildXpathFromBasic(document);
        }

        return testObj;
    }

    // 將 BASIC 的屬性轉換為 XPATH
    private static String buildXpathFromBasic(Document document) {
        StringBuilder xpath = new StringBuilder("//*");
        List<Node> properties = document.selectNodes("//webElementProperties[isSelected='true']");

        boolean hasCondition = false;
        for (Node propNode : properties) {
            String name = propNode.selectSingleNode("name").getText();
            String value = propNode.selectSingleNode("value").getText();

            if (name.equals("tag")) {
                xpath.replace(2, 3, value);
            } else if (name.equals("text")) {
                xpath.append(hasCondition ? " and " : "[");
                xpath.append(String.format("(text() = '%s' or . = '%s')", value, value));
                hasCondition = true;
            } else {
                xpath.append(hasCondition ? " and " : "[");
                xpath.append(String.format("@%s='%s'", name, value));
                hasCondition = true;
            }
        }
        if (hasCondition) xpath.append("]");
        return xpath.toString();
    }
}