package org.globus.crux;

import org.globus.crux.service.EPRFactory;
import org.globus.crux.service.EPRFactoryException;
import org.w3c.dom.Document;

import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;

/**
 * @author turtlebender
 */
public class SimpleEPRFactory implements EPRFactory {
    private QName serviceName;
    private QName portName;
    private String address;
    private JAXBContext jaxb;

    public W3CEndpointReference createEPRWithId(Object id) throws EPRFactoryException {
        try {
            W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
            builder.serviceName(serviceName).endpointName(portName).address(address);
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

    public void setJaxb(JAXBContext jaxb) {
        this.jaxb = jaxb;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = QName.valueOf(serviceName);
    }

    public void setPortName(String portName) {
        this.portName = QName.valueOf(portName);
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
