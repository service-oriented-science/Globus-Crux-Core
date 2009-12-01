package org.globus.crux.wsrf.properties;


import java.util.ResourceBundle;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.GetResourceProperty;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.GetResourcePropertyResponse;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.InvalidResourcePropertyQNameFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.ResourceUnknownFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the default implementation of GetResourceProperty.  It delegates the request to
 * a ResourcePropertySet.
 *
 * @author Tom Howe
 * @since 1.0
 * @version 1.0
 */
public class GetResourcePropertyImpl implements GetResourceProperty {
    private ResourcePropertySet rpSet;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("org.globus.crux.wsrf.wsrf");

    public void setRpSet(ResourcePropertySet rpSet) {
        this.rpSet = rpSet;
    }

    public GetResourcePropertyImpl withRPSet(ResourcePropertySet rpSet) {
        this.rpSet = rpSet;
        return this;
    }

    public GetResourcePropertyResponse getResourceProperty(QName getResourcePropertyRequest) throws InvalidResourcePropertyQNameFault, ResourceUnknownFault {
        GetResourcePropertyResponse response = new GetResourcePropertyResponse();
        if (!this.rpSet.containsResourceProperty(getResourcePropertyRequest)) {
            String message = resourceBundle.getString("no.such.resource.property.exists");
            message = String.format("%s %s", message, getResourcePropertyRequest);
            logger.info(message);
            throw new InvalidResourcePropertyQNameFault(message);
        }
        Object rpResult =this.rpSet.getResourceProperty(getResourcePropertyRequest);
        JAXBElement rp = new JAXBElement(getResourcePropertyRequest, rpResult.getClass(), rpResult);
        response.getAny().add(rp);
        return response;
    }
}