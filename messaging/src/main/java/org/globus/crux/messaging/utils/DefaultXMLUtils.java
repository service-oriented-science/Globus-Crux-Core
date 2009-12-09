package org.globus.crux.messaging.utils;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public class DefaultXMLUtils extends XMLUtils {
    private DocumentBuilderFactory dbf;
    private ReentrantLock dbfLock;
    private XPathFactory xpathFac;
    public Map<String, XPathExpression> xpathMap = new HashMap<String, XPathExpression>();


    Document parse(String message) {
        dbfLock.lock();
        try {
            return dbf.newDocumentBuilder().parse(message);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } finally{
            dbfLock.unlock();
        }
    }

    public NodeList evaluateXPathExpression(String xpathString, Document doc, NamespaceContext context) {
        XPathExpression xpression = getXPathExpression(xpathString, context);
        try {
            return (NodeList) xpression.evaluate(doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public DefaultXMLUtils(){
        dbf = DocumentBuilderFactory.newInstance();
        dbfLock = new ReentrantLock();
        xpathFac = XPathFactory.newInstance();
        org.apache.xml.security.Init.init();
    }

    public XPathExpression getXPathExpression(String xpathString, NamespaceContext context){
        if(xpathMap.get(xpathString) != null){
            return xpathMap.get(xpathString);
        }
        XPath xpath = xpathFac.newXPath();
        xpath.setNamespaceContext(context);
        try {
            XPathExpression xpress = xpath.compile(xpathString);
            xpathMap.put(xpathString, xpress);
            return xpath.compile(xpathString);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public Document createDocument(){
        dbfLock.lock();
        try {
            return dbf.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }finally{
            dbfLock.unlock();
        }
    }
}
