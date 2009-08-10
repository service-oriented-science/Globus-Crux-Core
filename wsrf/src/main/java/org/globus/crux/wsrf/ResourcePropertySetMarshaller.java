package org.globus.crux.wsrf;

import org.globus.crux.wsrf.properties.ResourcePropertySet;

import javax.xml.transform.Result;
import java.io.IOException;

/**
 * @author turtlebender
 */
public interface ResourcePropertySetMarshaller {
    public void marshalResourcePropertySet(ResourcePropertySet rps, Result result) throws IOException;
}
