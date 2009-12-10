package org.globus.crux.messaging.wsn;

import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.Base64;
import org.globus.crux.messaging.MessagingException;
import org.globus.crux.messaging.utils.XMLUtils;
import org.globus.crux.messaging.utils.EndpointReference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;


/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public class EPRUtilities {
    private static Canonicalizer c14n;
    private static XMLUtils xmlUtils = XMLUtils.newInstance();
    private static final String WSA_NAMESPACE = "http://www.w3.org/2005/08/addressing";
    private static final String WSAM_NAMESPACE = "http://www.w3.org/2006/05/addressing/wsdl";
    private static final String USER_NAMESPACE_PREFIX = "ns1";
    private static final String WSA_PREFIX = "wsa";
    private static final String WSAM_PREFIX = "wsam";
    private static final String WSAM_SERVICE_XPATH = "//wsam:ServiceName";
    private static final String WSAM_PORT_XPATH = "//wsam:InterfaceName";
    private static final String WSA_TO_ADDRESS_XPATH = "//wsa:ToAddress";
    private static final String USER_NAMESPACE_XPATH = "//ns1:";
    private static final String XMLNS = "xmlns";

    static {
        try {
            c14n = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static EndpointReference processEvent(W3CEndpointReference epr, QName resourceKeyName) throws MessagingException {
        Document doc = getEPRDoc(epr);
        EndpointReference reference = new EndpointReference();
        NamespaceContext context = new EPRNamespaceContext(resourceKeyName);
        reference.setServiceName(QName.valueOf(queryDoc(WSAM_SERVICE_XPATH, doc, context)));
        String portResult = queryDoc(WSAM_PORT_XPATH, doc, context);
        if (portResult != null && portResult.length() > 0) {
            reference.setPortName(QName.valueOf(portResult));
        }

        try {
            reference.setAddress(new URL(queryDoc(WSA_TO_ADDRESS_XPATH, doc, context)));
            reference.setReferenceKey(getReferenceKey(resourceKeyName, doc, context));
            return reference;
        } catch (MalformedURLException e) {
            throw new MessagingException(e);
        }
    }

    public static EndpointReference processEvent(W3CEndpointReference epr) throws MessagingException {
        Document doc = getEPRDoc(epr);
        EndpointReference reference = new EndpointReference();
        NamespaceContext context = new EPRNamespaceContext();
        reference.setServiceName(QName.valueOf(queryDoc(WSAM_SERVICE_XPATH, doc, context)));
        String portResult = queryDoc(WSAM_PORT_XPATH, doc, context);
        if (portResult != null && portResult.length() > 0) {
            reference.setPortName(QName.valueOf(portResult));
        }
        try {
            reference.setAddress(new URL(queryDoc(WSA_TO_ADDRESS_XPATH, doc, context)));
        } catch (MalformedURLException e) {
            throw new MessagingException(e);
        }

        return reference;
    }

    private static String queryDoc(String query, Document doc, NamespaceContext context) {
//        NodeList queryResults = xmlUtils.evaluateXPathExpression(query, doc, context);
//        if (queryResults != null && queryResults.getLength() > 0) {
//            return queryResults.item(0).getTextContent();
//        }
        return null;
    }

    public static String getReferenceKey(QName resourceKeyName, Document doc, NamespaceContext context)
            throws MessagingException {
        String referenceKey;
//        NodeList queryResults = xmlUtils.evaluateXPathExpression(USER_NAMESPACE_XPATH + resourceKeyName.getLocalPart(), doc, context);
//        if (queryResults != null && queryResults.getLength() > 0) {
//            Document refDoc = xmlUtils.createDocument();
//            Node referenceNode = queryResults.item(0);
//            Node importedNode = refDoc.importNode(referenceNode, true);
//            String prefix = importedNode.getPrefix();
//            if (prefix != null) {
//                String namespace = importedNode.getNamespaceURI();
//                ((Element) importedNode).setAttribute(XMLNS, namespace);
//                importedNode.setPrefix(null);
//            }
//            try {
//                referenceKey = getReferenceKeyAsString(refDoc, importedNode);
//            } catch (CanonicalizationException e) {
//                throw new MessagingException(e);
//
//            } catch (XMLSignatureException e) {
//                throw new MessagingException(e);
//            }
//            return referenceKey;
//        }
        return null;
    }

    public static String getReferenceKeyAsString(Document refDoc, Node importedNode) throws CanonicalizationException, XMLSignatureException {
        String referenceKey;
        byte[] c14nReference = c14n.canonicalizeSubtree(importedNode);
        MessageDigestAlgorithm mda;
        mda = MessageDigestAlgorithm.getInstance(refDoc, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1);
        mda.reset();
        mda.update(c14nReference);
        byte[] digest = mda.digest();
        referenceKey = Base64.encode(digest);
        return referenceKey;
    }

    private static Document getEPRDoc(W3CEndpointReference epr) {
//        Document doc = xmlUtils.createDocument();
//        DOMResult result = new DOMResult(doc);
//        epr.writeTo(result);
//        return doc;
        return null;
    }

    static class EPRNamespaceContext implements NamespaceContext {
        private QName resourceKeyName;

        public EPRNamespaceContext(QName resourceKeyName) {
            this.resourceKeyName = resourceKeyName;
        }

        public EPRNamespaceContext() {

        }

        public String getNamespaceURI(String s) {
            if (s.equals(USER_NAMESPACE_PREFIX)) {
                return resourceKeyName.getNamespaceURI();
            } else if (s.equals(WSA_PREFIX)) {
                return WSA_NAMESPACE;
            } else if (s.equals(WSAM_PREFIX)) {
                return WSAM_NAMESPACE;
            } else if ("xml".equals(s)) return XMLConstants.XML_NS_URI;
            return XMLConstants.NULL_NS_URI;
        }

        public String getPrefix(String s) {
            throw new UnsupportedOperationException();
        }

        public Iterator getPrefixes(String s) {
            throw new UnsupportedOperationException();
        }
    }
}
