/*
 * Copyright 1999-2006 University of Chicago
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.globus.wsrf.utils;

import java.io.InputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.xerces.impl.dv.util.Base64;

/**
 * Various XML utilities.
 */
public class XmlUtils {

    public static final String NS_URI_XMLNS = "http://www.w3.org/2000/xmlns/";

    private static Logger log =
            LoggerFactory.getLogger(XmlUtils.class.getName());

//    private static I18n i18n =
//        I18n.getI18n(Resources.class.getName());

    private static final DocumentBuilderFactory dbf = getDOMFactory();
    private static final TransformerFactory transFac = TransformerFactory.newInstance();

    public static String getElementAsString(Element e) {
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        try {
            Transformer trans;
            synchronized (transFac) {
                trans = transFac.newTransformer();
            }
            trans.transform(new DOMSource(e), result);
        } catch (TransformerException e1) {
            log.error("", e);
            return null;
        }
        return writer.toString();
    }

    public static String getDocumentAsString(Document e) {
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        try {
            Transformer trans;
            synchronized (transFac) {
                trans = transFac.newTransformer();
            }
            trans.transform(new DOMSource(e), result);
        } catch (TransformerException e1) {
            log.error("", e);
            return null;
        }
        return writer.toString();
    }

    private static DocumentBuilderFactory getDOMFactory() {
        DocumentBuilderFactory dbf;

        try {
            dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
        } catch (Exception e) {
            log.error("", e);
            dbf = null;
        }

        return (dbf);
    }

    /**
     * Creates an empty new XML document.
     *
     * @return a new document.
     * @throws ParserConfigurationException if construction problems occur
     */
    public static Document newDocument()
            throws ParserConfigurationException {
        synchronized (dbf) {
            return dbf.newDocumentBuilder().newDocument();
        }
    }

    /**
     * Creates a new document from the given input source.
     *
     * @param inp the input source from which to load the document.
     * @return the loaded XML document.
     * @throws ParserConfigurationException if construction problems occur
     * @throws SAXException                 if parsing problems occur
     * @throws IOException                  if I/O problems occur
     */
    public static Document newDocument(InputSource inp)
            throws ParserConfigurationException,
            SAXException,
            IOException {
        DocumentBuilder db;

        synchronized (dbf) {
            db = dbf.newDocumentBuilder();
        }

//        db.setErrorHandler(new XMLUtils.ParserErrorHandler());
        return db.parse(inp);
    }

    /**
     * Creates a new document from the given input stream.
     *
     * @param inp the input stream from which to load the document.
     * @return the loaded XML document.
     * @throws ParserConfigurationException if construction problems occur
     * @throws SAXException                 if parsing problems occur
     * @throws IOException                  if I/O problems occur
     */
    public static Document newDocument(InputStream inp)
            throws ParserConfigurationException,
            SAXException,
            IOException {
        return newDocument(new InputSource(inp));
    }

    
//    public static Document newDocument(String uri)
//            throws ParserConfigurationException,
//            SAXException,
//            IOException {
//        // call the authenticated version as there might be
//        // username/password info embeded in the uri.
//        return newDocument(uri, null, null);
//    }

//    public static Document newDocument(String uri,
//                                       String username,
//                                       String password)
//            throws ParserConfigurationException,
//            SAXException,
//            IOException {
//        InputSource ins = getInputSourceFromURI(uri, username, password);
//
//        Document doc = null;
//
//        try {
//            doc = newDocument(ins);
//        } finally {
//            if (ins.getByteStream() != null) {
//                ins.getByteStream().close();
//            } else if (ins.getCharacterStream() != null) {
//                ins.getCharacterStream().close();
//            }
//        }
//
//        return doc;
//    }


//    private static InputSource getInputSourceFromURI(String uri,
//                                                     String username,
//                                                     String password)
//            throws IOException {
//        URL wsdlurl = null;
//
//        try {
//            wsdlurl = new URL(uri);
//        } catch (MalformedURLException e) {
//            // we can't process it, it might be a 'simple' foo.wsdl
//            // let InputSource deal with it
//            return new InputSource(uri);
//        }
//
//        // if no authentication, just let InputSource deal with it
//        if ((username == null) && (wsdlurl.getUserInfo() == null)) {
//            return new InputSource(uri);
//        }
//
//        // if this is not an HTTP{S} url, let InputSource deal with it
//        if (!wsdlurl.getProtocol().startsWith("http")) {
//            return new InputSource(uri);
//        }
//
//        URLConnection connection = wsdlurl.openConnection();
//
//        // Does this work for https???
//        if (!(connection instanceof HttpURLConnection)) {
//            // can't do http with this URL, let InputSource deal with it
//            return new InputSource(uri);
//        }
//
//        HttpURLConnection uconn = (HttpURLConnection) connection;
//        String userinfo = wsdlurl.getUserInfo();
//        uconn.setRequestMethod("GET");
//        uconn.setAllowUserInteraction(false);
//        uconn.setDefaultUseCaches(false);
//        uconn.setDoInput(true);
//        uconn.setDoOutput(false);
//        uconn.setInstanceFollowRedirects(true);
//        uconn.setUseCaches(false);
//
//        // username/password info in the URL overrides passed in values
//        String auth = null;
//
//        if (userinfo != null) {
//            auth = userinfo;
//        } else if (username != null) {
//            auth = (password == null) ? username : (username + ":" + password);
//        }
//
//        if (auth != null) {
//            uconn.setRequestProperty(
//                    "Authorization",
//                    "Basic " +
//                            XMLUtils.base64encode(auth.getBytes(XMLUtils.getEncoding()))
//            );
//        }
//
//        uconn.connect();
//
//        return new InputSource(uconn.getInputStream());
//    }

    public static String base64encode(byte[] bytes) {
        return Base64.encode(bytes);
    }


    public static String getPrefix(String uri, Node e) {
        while (e != null && (e.getNodeType() == Element.ELEMENT_NODE)) {
            NamedNodeMap attrs = e.getAttributes();
            for (int n = 0; n < attrs.getLength(); n++) {
                Attr a = (Attr) attrs.item(n);
                String name;
                if ((name = a.getName()).startsWith("xmlns:") &&
                        a.getNodeValue().equals(uri)) {
                    return name.substring(6);
                }
            }
            e = e.getParentNode();
        }
        return null;
    }

    public static QName getFullQNameFromString(String str, Node e) {
        return getQNameFromString(str, e, true);
    }

    private static QName getQNameFromString(String str, Node e, boolean defaultNS) {
        if (str == null || e == null)
            return null;

        int idx = str.indexOf(':');
        if (idx > -1) {
            String prefix = str.substring(0, idx);
            String ns = getNamespace(prefix, e);
            if (ns == null) {
                log.warn("Cannot obtain namespaceURI for prefix: " + prefix);
                return null;
            }
            return new QName(ns, str.substring(idx + 1));
        } else {
            if (defaultNS) {
                String ns = getNamespace(null, e);
                if (ns != null)
                    return new QName(ns, str);
            }
            return new QName("", str);
        }
    }

    /**
     * Return a string for a particular QName, mapping a new prefix
     * if necessary.
     */
    public static String getStringForQName(QName qname, Element e) {
        String uri = qname.getNamespaceURI();
        String prefix = getPrefix(uri, e);
        if (prefix == null) {
            int i = 1;
            prefix = "ns" + i;
            while (getNamespace(prefix, e) != null) {
                i++;
                prefix = "ns" + i;
            }
            e.setAttributeNS(NS_URI_XMLNS,
                    "xmlns:" + prefix, uri);
        }
        return prefix + ":" + qname.getLocalPart();
    }


    public static String getNamespace(String prefix, Node e, Node stopNode) {
        while (e != null && (e.getNodeType() == Node.ELEMENT_NODE)) {
            Attr attr = null;
            if (prefix == null) {
                attr = ((Element) e).getAttributeNode("xmlns");
            } else {
                attr = ((Element) e).getAttributeNodeNS(NS_URI_XMLNS,
                        prefix);
            }
            if (attr != null) return attr.getValue();
            if (e == stopNode)
                return null;
            e = e.getParentNode();
        }
        return null;
    }

    public static String getNamespace(String prefix, Node e) {
        return getNamespace(prefix, e, null);
    }

    /**
     * Outputs the document into string.
     *
     * @param doc the document to output.
     * @return the document in text form.
     */
    public static String toString(Document doc) {
        return getDocumentAsString(doc);
    }

    /**
     * Outputs the element into string.
     *
     * @param element the element to output.
     * @return the document in text form.
     */
    public static String toString(Element element) {
        return getElementAsString(element);
    }

    /**
     * Utility function for getting the first child element from a element
     *
     * @param element the parent element
     * @return the first child element if one was found, null otherwise
     */
    public static Element getFirstChildElement(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("nullArgument: element");
//                    i18n.getMessage("nullArgument", "element"));
        }
        for (Node currentChild = element.getFirstChild();
             currentChild != null;
             currentChild = currentChild.getNextSibling()) {
            if (currentChild.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) currentChild;
            }
        }

        return null;
    }

    /**
     * Sets namespaces on the given XML element.
     *
     * @param element    the XML element on which the namespaces will be set.
     * @param namespaces the namespace mappings.
     */
    public static void setNamespaces(Element element, Map namespaces) {
        if (element == null) {
            throw new IllegalArgumentException("nullArgument: element");
//                    i18n.getMessage("nullArgument", "element"));
        }
        if (namespaces != null) {
            for (Object o : namespaces.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                element.setAttributeNS("http://www.w3.org/2000/xmlns/",
                        (String) entry.getValue(),
                        (String) entry.getKey());
            }
        }
    }

    /**
     * Creates a <tt>Map</tt> of namespace mappings from the given node.
     * All namespace mappings are collected including all the children of
     * the given node.
     *
     * @param node the root node from which to collect the namespaces.
     * @return the collected <tt>Map</tt> of namespace mappings.
     */
    public static Map collectNamespaces(Node node) {
        Map namespaces = new HashMap();
        collectNamespaces(node, namespaces, new HashMap());
        return namespaces;
    }

    public static void collectNamespaces(Node node,
                                         Map namespaces,
                                         Map prefixes) {
        if (node == null) {
            throw new IllegalArgumentException("nullArgument: node");
//                    i18n.getMessage("nullArgument", "node"));
        }
        if (namespaces == null) {
            throw new IllegalArgumentException("nullArgument: namespace");
//                    i18n.getMessage("nullArgument", "namespaces"));
        }
        if (prefixes == null) {
            prefixes = new HashMap();
        }
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                Attr attr = (Attr) attributes.item(i);
                String name = attr.getName();
                String value = attr.getValue();
                if (name.startsWith("xmlns:")) {
                    if (namespaces.get(value) == null) {
                        // ns not defined
                        if (prefixes.get(name) != null) {
                            // find unique prefix
                            int j = 1;
                            do {
                                name = "xmlns:ns" + j++;
                            } while (prefixes.get(name) != null);
                        }
                        prefixes.put(name, value);
                        namespaces.put(value, name);
                    }
                }
            }
        }
        NodeList children = node.getChildNodes();
        if (children != null) {
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    collectNamespaces(child, namespaces, prefixes);
                }
            }
        }
    }

}
