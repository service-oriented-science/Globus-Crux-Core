package org.globus.crux.wsrf.properties;

import org.oasis.wsrf.v200406.properties.DraftGetResourcePropertyResponse;
import org.oasis.wsrf.properties.GetResourcePropertyResponse;
import org.oasis.wsrf.properties.ObjectFactory;

import javax.xml.namespace.QName;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBContext;

/**
 * @author turtlebender
 */
public class FinalGetRPJaxbHandler extends AbstractGetRPJaxbHandler<GetResourcePropertyResponse> {
    private ObjectFactory objectFactory = new ObjectFactory();

    public FinalGetRPJaxbHandler(Object target, JAXBContext jaxb) {
        super(target, jaxb);
    }

    protected GetResourcePropertyResponse doHandle(Object key, Object target, JAXBElement<QName> payload) throws Exception {
        GetResourcePropertyResponse response = objectFactory.createGetResourcePropertyResponse();
        response.getAny().add(this.getValue(key, target, payload));
        return response;
    }
}
