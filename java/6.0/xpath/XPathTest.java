import java.io.*;
import javax.xml.xpath.*;
import org.w3c.dom.*;
import org.xml.sax.*;

public class XPathTest {
    public static void main(String[] args) throws Exception {

        XPath xpath = XPathFactory.newInstance().newXPath();

        String xml = "<foo><bar>1111</bar><bar>aaaa</bar></foo>";

        Node node = (Node)xpath.evaluate("/foo/bar", new InputSource(new StringReader(xml)), XPathConstants.NODE);

        System.out.println(node.getTextContent());

    }
}