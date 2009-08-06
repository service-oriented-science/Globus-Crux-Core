package org.globus.crux;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.xml.ws.Endpoint;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static org.mockito.Mockito.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.globus.crux.service.EPRFactoryException;

import java.util.Map;
import java.util.HashMap;
import java.io.StringWriter;

/**
 * @author turtlebender
 */
public class ServiceEPRFactoryTest {
    @Mock
    Endpoint mockEndpoint;
    @Mock
    JAXBContext jaxb;
    @Mock
    Marshaller marshaller;
    private String expectedEPR;
    private ServiceEPRFactory factory;
    private Document keyDom;
    private JAXBElement<String> keyJAXB;
    private static final String ADDRESS = "http://localhost:12345/service/counter";
    private static final QName SERVICE_QNAME = new QName("http://counter.com", "CounterService");
    private static final QName PORT_NAME = new QName("http://counter.com", "CounterPortType");

    public ServiceEPRFactoryTest() throws ParserConfigurationException, TransformerException {
        keyDom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = keyDom.createElementNS("http://counter.com", "CounterKey");
        keyDom.appendChild(root);
        String keyString = "counter";
        root.setTextContent(keyString);
        String keyNamespace = "http://counter.com";
        String keyLocalpart = "CounterKey";
        keyJAXB = new JAXBElement<String>(new QName(keyNamespace, keyLocalpart),
                String.class, keyString);
        W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
        W3CEndpointReference expected = builder.serviceName(SERVICE_QNAME).endpointName(PORT_NAME).
                address(ADDRESS).
                referenceParameter(keyDom.getDocumentElement()).build();
        DOMResult expectedDOMResult = new DOMResult();
        expected.writeTo(expectedDOMResult);
        StringWriter expectedWriter = new StringWriter();
        Transformer transform = TransformerFactory.newInstance().newTransformer();
        transform.transform(new DOMSource(expectedDOMResult.getNode()), new StreamResult(expectedWriter));
        this.expectedEPR = expectedWriter.toString();
    }

    @BeforeTest
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        factory = new ServiceEPRFactory();
        factory.setEndpoint(mockEndpoint);
        factory.setJAXBContext(jaxb);
        factory.setEndpointAddress(ADDRESS);
        Map<String, Object> propMap = new HashMap<String, Object>();
        propMap.put(Endpoint.WSDL_SERVICE, SERVICE_QNAME);
        propMap.put(Endpoint.WSDL_PORT, PORT_NAME);
        when(mockEndpoint.getProperties()).thenReturn(propMap);
        when(jaxb.createMarshaller()).thenReturn(marshaller);
        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                //Not the most beautiful mock ever, but kinda required when working
                //with JAXB
                Object[] args = invocationOnMock.getArguments();
                DOMResult result = (DOMResult) args[1];
                if (result.getNode() != null) {
                    if (result.getNode() instanceof Document) {
                        Document doc = (Document) result.getNode();
                        doc.appendChild(doc.importNode(keyDom.getDocumentElement(), true));
                    }
                }
                return result;
            }
        }).when(marshaller).marshal(any(JAXBElement.class), any(DOMResult.class));
    }

    @Test
    public void testCreateEPRWithId() throws ParserConfigurationException,
            TransformerException, EPRFactoryException {
        W3CEndpointReference epr = factory.createEPRWithId(keyJAXB);
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        DOMResult result = new DOMResult(doc);
        epr.writeTo(result);
        StringWriter writer = new StringWriter();
        Transformer transform = TransformerFactory.newInstance().newTransformer();
        transform.transform(new DOMSource(doc), new StreamResult(writer));
        assertEquals(writer.toString(), expectedEPR);
    }
}
