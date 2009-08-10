package org.globus.crux.wsrf.query;

import org.globus.crux.wsrf.ResourcePropertySetMarshaller;
import org.globus.crux.wsrf.properties.ResourcePropertySet;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryEvaluationErrorFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryResourcePropertiesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.Unmarshaller;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author turtlebender
 */
public class DefaultXPathQueryEngine implements QueryEngine<Object, XPathQuery> {
    private ResourcePropertySetMarshaller marshaller;
    private Unmarshaller unmarshaller;
    private DocumentBuilder docBuilder;
    private XPathFactory xpathFac;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public static final String XPATH_1_DIALECT =
            "http://www.w3.org/TR/xpath";

    public DefaultXPathQueryEngine() throws ParserConfigurationException {
        docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        xpathFac = XPathFactory.newInstance();
    }

    public boolean canProcessDialect(String dialect) {
        return dialect.equals(XPATH_1_DIALECT);
    }

    public List<Object> executeQuery(XPathQuery queryExpr, ResourcePropertySet rps) throws QueryEvaluationErrorFault {
        Document doc = docBuilder.newDocument();
        Element root = doc.createElementNS(rps.getResourceName().getNamespaceURI(), rps.getResourceName().getLocalPart());
        doc.appendChild(root);
        try {
            marshaller.marshalResourcePropertySet(rps, new DOMResult(root));
        } catch (IOException e) {
            logger.warn("Error marshalling ResourcePropertySet to DOM", e);
            throw new QueryEvaluationErrorFault(e.getMessage());
        }
        QueryResourcePropertiesResponse response = new QueryResourcePropertiesResponse();
        List<Object> results = response.getContent();
        //TODO: cache this?
        try {
            XPath xpath = xpathFac.newXPath();
            xpath.setNamespaceContext(new XMLNamespaceContext(queryExpr));
            XPathExpression expression = xpath.compile(queryExpr.getQuery());
            Object result = expression.evaluate(doc, XPathConstants.NODESET);
            NodeList nodes = (NodeList) result;
            for (int i = 0; i < nodes.getLength(); i++) {
                results.add(unmarshaller.unmarshal(new DOMSource(nodes.item(i))));
            }
        } catch (XPathExpressionException e) {
            logger.error("Error processing XPath expression", e);
            throw new QueryEvaluationErrorFault(e.getMessage());
        } catch (IOException e) {
            logger.error("Error unmarshalling results to objects", e);
            throw new QueryEvaluationErrorFault(e.getMessage());
        }
        return results;
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    public void setMarshaller(ResourcePropertySetMarshaller marshaller) {
        this.marshaller = marshaller;
    }

    class XMLNamespaceContext implements NamespaceContext {
        private Map<String, String> nsToPrefix = new HashMap<String, String>();
        private Map<String, String> prefixToNS = new HashMap<String, String>();

        public XMLNamespaceContext(XPathQuery query) {
            for (NamespaceMapping nm : query.getNsMapping()) {
                nsToPrefix.put(nm.getNamespaceUri(), nm.getPrefix());
                prefixToNS.put(nm.getPrefix(), nm.getNamespaceUri());
            }
        }

        public String getNamespaceURI(String s) {
            return prefixToNS.get(s);
        }

        public String getPrefix(String s) {
            return nsToPrefix.get(s);
        }

        public Iterator getPrefixes(String s) {
            return prefixToNS.keySet().iterator();
        }
    }

    public Class<XPathQuery> getQueryType() {
        return XPathQuery.class;
    }
}
