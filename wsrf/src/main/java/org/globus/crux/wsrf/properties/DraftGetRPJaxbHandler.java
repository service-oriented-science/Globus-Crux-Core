package org.globus.crux.wsrf.properties;

import org.oasis.wsrf.v200406.properties.DraftGetResourcePropertyResponse;
import org.oasis.wsrf.v200406.properties.ObjectFactory;

import javax.xml.namespace.QName;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBContext;

/**
 * @author turtlebender
 */
public class DraftGetRPJaxbHandler extends AbstractGetRPJaxbHandler<DraftGetResourcePropertyResponse> {
    private ObjectFactory objectFactory = new ObjectFactory();


    public DraftGetRPJaxbHandler(Object target, JAXBContext jaxb) {
        super(target, jaxb);
    }

    protected DraftGetResourcePropertyResponse doHandle(Object key, Object target, JAXBElement<QName> payload) throws Exception {
        DraftGetResourcePropertyResponse response = objectFactory.createDraftGetResourcePropertyResponse();
        response.getAny().add(getValue(key, target, payload));
        return response;
    }
}
