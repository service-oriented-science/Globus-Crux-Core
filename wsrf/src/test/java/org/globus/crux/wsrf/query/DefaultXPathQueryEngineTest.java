package org.globus.crux.wsrf.query;

import org.globus.crux.wsrf.ResourcePropertySetMarshaller;
import org.globus.crux.wsrf.properties.ResourcePropertySet;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.oxm.Unmarshaller;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.oasis.wsrf.properties.QueryExpressionType;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.List;

/**
 * @author turtlebender
 */
@Test(groups={"wsrf","query"})
public class DefaultXPathQueryEngineTest {
    DefaultXPathQueryEngine queryEngine;
    @Mock
    ResourcePropertySetMarshaller marshaller;
    @Mock
    Unmarshaller unmarshaller;
    @Mock
    ResourcePropertySet rps;

    Node counterNode;

    private Document createDom() throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = doc.createElementNS("http://counter.com", "CounterRP");
        doc.appendChild(root);
        Element child = doc.createElementNS("http://counter.com", "Value");
        root.appendChild(child);
        counterNode = root;
        child.setTextContent("20");
        child = doc.createElementNS("http://counter.com", "Status");
        root.appendChild(child);
        child.setTextContent("1");
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        trans.transform(new DOMSource(doc), new StreamResult(System.out));
        return doc;
    }

    @BeforeTest
    public void setUp() throws Exception {
        initMocks(this);
        final Document dom = createDom();
        queryEngine = new DefaultXPathQueryEngine();
        queryEngine.setMarshaller(marshaller);
        queryEngine.setUnmarshaller(unmarshaller);
        queryEngine.setRps(rps);
        QName resourceName = new QName("http://counter.com", "CounterRP");
        when(rps.getResourceName()).thenReturn(resourceName);
        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Result result = (Result) invocationOnMock.getArguments()[1];
                TransformerFactory.newInstance().newTransformer().transform(new DOMSource(dom), result);
                return null;
            }
        }).when(marshaller).marshalResourcePropertySet(any(ResourcePropertySet.class), any(Result.class));
        when(unmarshaller.unmarshal(any(Source.class))).thenReturn(dom.getFirstChild());

    }

    @Test
    public void testCanProcessDialect() {
        assertTrue(queryEngine.canProcessDialect(DefaultXPathQueryEngine.XPATH_1_DIALECT));
        assertFalse(queryEngine.canProcessDialect("Fake Dialect"));
    }

    @Test
    public void testExecuteQuery() throws Exception{
        QueryExpressionType exp = new QueryExpressionType();
        exp.setDialect(DefaultXPathQueryEngine.XPATH_1_DIALECT);
        XPathQuery query = new XPathQuery();
        query.getNsMapping().add(new NamespaceMapping("count", "http://counter.com"));
        query.setQuery("/count:CounterRP");
        exp.getContent().add(query);
        List<Object> results = queryEngine.executeQuery(query);
        assertEquals(results.size(), 1);
        assertEquals(results.get(0), counterNode);
    }
}
