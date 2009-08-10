package org.globus.crux;

import org.globus.crux.service.EPRFactory;
import org.globus.crux.service.EPRFactoryException;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.Endpoint;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;
import java.util.Map;

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

    public void setJAXBContext(JAXBContext jaxb) {
        this.jaxb = jaxb;
    }

    public void setEndpointAddress(String endpointAddress) {
        this.endpointAddress = endpointAddress;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
        EndpointReference ref = endpoint.getEndpointReference();
        System.out.println("ref = " + ref);
    }

    public W3CEndpointReference createEPRWithId(Object id) throws EPRFactoryException{
        try {

            W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
            Map<String, Object> propMap = endpoint.getProperties();
            builder.serviceName((QName) propMap.get(Endpoint.WSDL_SERVICE));
            builder.endpointName((QName) propMap.get(Endpoint.WSDL_PORT));
            builder.address(endpointAddress);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            DOMResult result = new DOMResult(doc);
            Marshaller marshaller = jaxb.createMarshaller();
            marshaller.marshal(id, result);
            return builder.referenceParameter(doc.getDocumentElement()).build();
        } catch (JAXBException e) {
            throw new EPRFactoryException(e);
        } catch (ParserConfigurationException e) {
            throw new EPRFactoryException(e);
        }
    }
}
