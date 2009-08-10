package org.globus.crux.wsrf.properties;


import org.oasis.wsrf.properties.GetResourcePropertyResponse;
import org.oasis.wsrf.properties.GetResourceProperty;
import org.oasis.wsrf.properties.InvalidResourcePropertyQNameFault;
import org.oasis.wsrf.properties.ResourceUnknownFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.util.ResourceBundle;

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
        response.getAny().add(this.rpSet.getResourceProperty(getResourcePropertyRequest));
        return response;
    }
}