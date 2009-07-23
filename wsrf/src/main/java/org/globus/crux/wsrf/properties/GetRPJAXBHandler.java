package org.globus.crux.wsrf.properties;

import org.globus.crux.cxf.jaxb.AbstractJAXBStatefulReflectiveHandler;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.lang.reflect.Method;

/**
 * @author turtlebender
 */
public class GetRPJAXBHandler extends AbstractJAXBStatefulReflectiveHandler<JAXBElement<QName>, Object> {
    private static final String DRAFT_VERSION_NS = "http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceProperties-1.2-draft-01.xsd";
    private DraftGetRPJaxbHandler draftHandler;
    private FinalGetRPJaxbHandler finalHandler;

    public GetRPJAXBHandler(QName keyName, Object target, JAXBContext jaxb) {
        super(keyName, target, jaxb);
        draftHandler = new DraftGetRPJaxbHandler(keyName, target, jaxb);
        finalHandler = new FinalGetRPJaxbHandler(keyName, target, jaxb);
    }

    public void registerMethod(QName qname, Method method) {
        draftHandler.registerMethod(qname, method);
        finalHandler.registerMethod(qname, method);
    }

    protected Object doHandle(Object key, Object target, JAXBElement<QName> payload) throws Exception {
        if (payload.getName().getNamespaceURI().equals(DRAFT_VERSION_NS)) {
            return draftHandler.doHandle(key, target, payload);
        } else {
            return finalHandler.doHandle(key, target, payload);
        }
    }
}
