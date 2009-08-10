package com.counter;

import org.globus.crux.cxf.StatefulServiceWebProvider;
import org.globus.crux.cxf.CXFCruxContextFactory;
import org.globus.crux.cxf.jaxb.JAXBStatefulProcessor;
import org.globus.crux.cxf.jaxb.JAXBCreateProcesor;
import org.globus.crux.stateful.ServiceProcessor;
import org.globus.crux.stateful.ServiceMethodProcessor;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.ws.addressing.JAXWSAConstants;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.AddressingBuilder;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.ws.addressing.AttributedURIType;
import org.apache.cxf.ws.addressing.ReferenceParametersType;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertEquals;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBElement;
import javax.xml.ws.Service;
import javax.xml.ws.Dispatch;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMResult;
import java.util.Map;
import java.io.IOException;

/**
 * @author turtlebender
 */
public class CounterIntegrationTest {
    Server server;
    JAXBContext jaxb;
    Dispatch<Object> dispatch;
    W3CEndpointReference epr;
    Service service;
    ObjectFactory fac = new ObjectFactory();

    public void startServer() throws Exception, IOException {
        jaxb = JAXBContext.newInstance("com.counter:org.oasis.wsrf.properties:org.oasis.wsrf.v200406.properties:org.oasis.wsrf.faults:org.oasis.wsrf.v200406.faults");
        CounterService service = new CounterService();
        StatefulServiceWebProvider provider = new StatefulServiceWebProvider();
        //TODO: getQName Dynamically
        CXFCruxContextFactory fac = new CXFCruxContextFactory(jaxb, new QName("http://counter.com", "CounterKey"));
        provider.setFactory(fac);
        ServiceProcessor processor = new ServiceMethodProcessor().
                withProcessor(new JAXBStatefulProcessor(jaxb, provider)).
                withProcessor(new JAXBCreateProcesor(jaxb, provider));
        processor.processObject(service);
        createService(provider);
    }    

    public void setupClient() throws JAXBException {
        SpringBusFactory busFactory = new SpringBusFactory();
        Bus bus = busFactory.createBus("/cxf-client-config.xml");
        BusFactory.setDefaultBus(bus);
        QName port = new QName("http://counter.com/service", "CounterPortTypePort");
        service = Service.create(new QName("http://counter.com/service", "CounterService"));
        service.addPort(port, SOAPBinding.SOAP12HTTP_BINDING,
                "http://localhost:12345/counter");
        dispatch = service.createDispatch(port, jaxb, Service.Mode.PAYLOAD);
    }

    private void createService(StatefulServiceWebProvider provider) {
        JaxWsServerFactoryBean svrFactory = new JaxWsServerFactoryBean();
        svrFactory.setServiceBean(provider);
        svrFactory.setWsdlURL("target/wsdl/counter_service.wsdl");
        svrFactory.setAddress("http://localhost:12345/counter");
        svrFactory.setServiceName(new QName("http://counter.com/service", "CounterService"));
        svrFactory.setEndpointName(new QName("http://counter.com/service", "CounterPortTypePort"));
        svrFactory.getFeatures().add(new WSAddressingFeature());
        svrFactory.getInInterceptors().add(new LoggingInInterceptor());
        svrFactory.getOutInterceptors().add(new LoggingOutInterceptor());
        server = svrFactory.create();
    }

    public void testAdd() throws JAXBException {
        Object o = dispatch.invoke(fac.createAdd(10));
        assertTrue(o instanceof JAXBElement);
        JAXBElement<Integer> result = (JAXBElement<Integer>) o;
        assertEquals(10, result.getValue().intValue());
    }

    public void testDraftGetRP() {
//        org.oasis.wsrf.v200406.properties.ObjectFactory fac = new org.oasis.wsrf.v200406.properties.ObjectFactory();
//        JAXBElement<QName> request = fac.createGetResourceProperty(new QName("http://counter.com", "CounterRP"));
//        Object o = dispatch.invoke(request);
//        assertTrue(o instanceof DraftGetResourcePropertyResponse);
//        DraftGetResourcePropertyResponse response = (DraftGetResourcePropertyResponse) o;
//        assertEquals(response.getAny().size(), 1);
//        Object rp = response.getAny().get(0);
//        assertTrue(rp instanceof CounterRP);
//        CounterRP c = (CounterRP) rp;
//        assertEquals(c.getValue(), 10);
    }

    public void testFinalGetRP() {
//        org.oasis.wsrf.properties.ObjectFactory fac = new org.oasis.wsrf.properties.ObjectFactory();
//        JAXBElement<QName> request = fac.createGetResourceProperty(new QName("http://counter.com", "CounterRP"));
//        Object o = dispatch.invoke(request);
//        assertTrue(o instanceof GetResourcePropertyResponse);
//        GetResourcePropertyResponse response = (GetResourcePropertyResponse) o;
//        assertEquals(response.getAny().size(), 1);
//        Object rp = response.getAny().get(0);
//        assertTrue(rp instanceof CounterRP);
//        CounterRP c = (CounterRP) rp;
//        assertEquals(c.getValue(), 10);
    }

    public void testCreateCounter() throws JAXBException {
        Object o = dispatch.invoke(new CreateCounter());
        assertTrue(o instanceof CreateCounterResponse);
        CreateCounterResponse response = (CreateCounterResponse) o;
        Marshaller marshaller = jaxb.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(response, new StreamResult(System.out));
        epr = response.getEndpointReference();
        assertNotNull(epr);
        //This is really ugly because the cxf dispatch is kinda broken
        configureEPR(epr);
    }

    private void configureEPR(W3CEndpointReference epr) {
        DOMResult result = new DOMResult();
        epr.writeTo(result);
        //I really didn't feel like using xpath for something this stupid
        Node node = result.getNode().getFirstChild().getFirstChild().
                getNextSibling().getFirstChild();
        assertEquals("CounterKey", node.getLocalName());
        String id = node.getFirstChild().getTextContent();
        Map<String, Object> requestContext = dispatch.getRequestContext();
        AddressingBuilder builder = AddressingBuilder.getAddressingBuilder();
        AddressingProperties maps = builder.newAddressingProperties();
        EndpointReferenceType ref = new EndpointReferenceType();
        AttributedURIType uri = new AttributedURIType();
        uri.setValue("http://localhost:9000/counter");
        ref.setAddress(uri);
        ReferenceParametersType params = new ReferenceParametersType();
        ref.setReferenceParameters(params);
        params.getAny().add(fac.createCounterKey(id));
        maps.setTo(ref);
        requestContext.put(JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES, maps);
    }

    public void shutdown() {
        this.server.stop();
    }
}
