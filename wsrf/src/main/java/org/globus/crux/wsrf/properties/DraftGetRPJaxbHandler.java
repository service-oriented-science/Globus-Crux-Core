package org.globus.crux.wsrf.properties;

import org.oasis.wsrf.properties.GetResourcePropertyResponse;
import org.oasis.wsrf.v200406.properties.DraftGetResourcePropertyResponse;
import org.oasis.wsrf.v200406.properties.ObjectFactory;
import org.globus.crux.cxf.jaxb.AbstractJAXBStatefulReflectiveHandler;
import org.globus.crux.stateful.StateKeyParam;

import javax.xml.namespace.QName;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBContext;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.HashMap;

/**
 * @author turtlebender
 */
public class DraftGetRPJaxbHandler extends AbstractGetRPJaxbHandler<DraftGetResourcePropertyResponse> {
    private ObjectFactory objectFactory = new ObjectFactory();


    public DraftGetRPJaxbHandler(QName keyName, Object target, JAXBContext jaxb) {
        super(keyName, target, jaxb);
    }

    protected DraftGetResourcePropertyResponse doHandle(Object key, Object target, JAXBElement<QName> payload) throws Exception {
        DraftGetResourcePropertyResponse response = objectFactory.createDraftGetResourcePropertyResponse();
        response.getAny().add(getValue(key, target, payload));
        return response;
    }
}
