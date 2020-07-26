package demo;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @ClassName Demo
 * @Description TODO
 * @Author 17126
 * @Date 2020/6/9 17:11
 */
public class Demo {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        FileInputStream stream = new FileInputStream("a.xml");
        Document document = db.parse(stream);
        Element element = document.getDocumentElement();
        System.out.println(element);
        Node firstChild = element.getFirstChild();
        Node nextSibling = firstChild.getNextSibling();
        System.out.println(nextSibling);
        NodeList childNodes = nextSibling.getChildNodes();
        for (int i = 0;i<childNodes.getLength();i++) {
            Node item = childNodes.item(i);
            System.out.println(item);
        }
    }
}
