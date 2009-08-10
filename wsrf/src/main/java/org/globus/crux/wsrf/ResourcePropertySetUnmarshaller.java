package org.globus.crux.wsrf;

import org.globus.crux.wsrf.properties.ResourcePropertySet;

import javax.xml.transform.Source;

/**
 * @author turtlebender
 */
public interface ResourcePropertySetUnmarshaller {
    public ResourcePropertySet unmarshallResourcePropertySet(Source source);
}
