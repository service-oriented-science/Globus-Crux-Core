package org.globus.crux.cxf;

import org.globus.crux.service.EPRFactory;
import org.w3c.dom.Document;

import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.WebServiceContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author turtlebender
 */
public class CXFEPRFactory implements EPRFactory {
    private WebServiceContext context;
    private JAXBContext jaxb;
    private DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    public CXFEPRFactory(WebServiceContext context, JAXBContext jaxb){
        this.context = context;
        this.jaxb = jaxb;
    }

    public W3CEndpointReference createEPRWithId(Object o) {
        try {
            Document doc = dbf.newDocumentBuilder().newDocument();
            jaxb.createMarshaller().marshal(o, doc);
            return (W3CEndpointReference) context.getEndpointReference(doc.getDocumentElement());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (JAXBException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return (W3CEndpointReference) context.getEndpointReference();
    }
}
