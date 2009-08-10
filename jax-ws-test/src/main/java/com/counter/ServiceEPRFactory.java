package com.counter;

import org.globus.crux.service.EPRFactory;
import org.w3c.dom.Document;
import org.apache.cxf.jaxws.spring.EndpointDefinitionParser;
import org.apache.cxf.transport.servlet.ServletDestination;

import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;
import javax.xml.ws.Endpoint;
import javax.xml.ws.EndpointReference;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * EPRFactory based on a JAX-WS Service.  This defaults to the first portName unless otherwise
 * specfied.
 *
 * @author turtlebender
 */
public class ServiceEPRFactory implements EPRFactory {
    private Endpoint endpoint;
    private JAXBContext jaxb;
    private String endpointAddress;

    public void setJAXBPackage(String pkg) {
        try {
            this.jaxb = JAXBContext.newInstance(pkg);
        } catch (JAXBException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void setEndpointAddress(String endpointAddress) {
        this.endpointAddress = endpointAddress;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
        EndpointReference ref = endpoint.getEndpointReference();
        System.out.println("ref = " + ref);
    }

    public W3CEndpointReference createEPRWithId(Object id) {
        try {
            EndpointDefinitionParser.SpringEndpointImpl springEnd = (EndpointDefinitionParser.SpringEndpointImpl) endpoint;
            W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
            builder.serviceName(springEnd.getServiceName());
            builder.endpointName(springEnd.getEndpointName());
            builder.address(endpointAddress);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            DOMResult result = new DOMResult(doc);
            jaxb.createMarshaller().marshal(id, result);
            return builder.referenceParameter(doc.getDocumentElement()).build();
//            return endpoint.getEndpointReference(W3CEndpointReference.class, doc.getDocumentElement());
        } catch (JAXBException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
