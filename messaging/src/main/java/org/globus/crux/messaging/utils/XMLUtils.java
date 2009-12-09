package org.globus.crux.messaging.utils;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpression;
import javax.xml.namespace.NamespaceContext;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public abstract class XMLUtils {

    public static XMLUtils newInstance(){
        return new DefaultXMLUtils();
    }

    abstract Document parse(String message);

    abstract NodeList evaluateXPathExpression(String xpathString, Document doc, NamespaceContext context);

    abstract XPathExpression getXPathExpression(String xpathString, NamespaceContext context);

    abstract Document createDocument();
}
