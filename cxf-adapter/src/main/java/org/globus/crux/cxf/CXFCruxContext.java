package org.globus.crux.cxf;

import org.globus.crux.stateful.CruxContext;
import org.globus.crux.stateful.CruxServiceException;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.JAXWSAConstants;

import javax.xml.ws.WebServiceContext;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

/**
 * @author turtlebender
 */
public class CXFCruxContext implements CruxContext {
    private WebServiceContext wsc;
    private QName keyName;
    private JAXBContext jaxb;

    public CXFCruxContext(WebServiceContext context, QName keyName, JAXBContext jaxb) {
        this.keyName = keyName;
        this.wsc = context;
        this.jaxb = jaxb;
    }

    public WebServiceContext getWebServiceContext() {
        return wsc;
    }

    public Object getKey() throws CruxServiceException {
        AddressingProperties map =
                (AddressingProperties) wsc.getMessageContext().
                        get(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_INBOUND);
        JAXBIntrospector jaxbspec = jaxb.createJAXBIntrospector();
        for (Object candidate : map.getToEndpointReference().getReferenceParameters().getAny()) {
            if (jaxbspec.getElementName(candidate).equals(this.keyName)) {
                return candidate;
            }
        }
        return null;
    }

    public Object get(String key) throws CruxServiceException {
        return wsc.getMessageContext().get(key);
    }
}
