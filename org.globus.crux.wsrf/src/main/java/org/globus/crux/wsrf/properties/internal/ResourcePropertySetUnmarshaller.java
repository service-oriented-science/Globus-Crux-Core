package org.globus.crux.wsrf.properties.internal;

import java.io.IOException;

import javax.xml.transform.Source;

import org.globus.crux.core.state.ResourcePropertySet;

/**
 * This interface is used to convert an XML Source into ResourcePropertySet.  Often, this will be combined
 * with the ResourcePropertySetMarshaller in the implementation.
 *
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public interface ResourcePropertySetUnmarshaller {
    /**
     * Convert a ResourcePropertySet to an XML Result.
     *
     * @param source The XML Source to unmarshall
     * @throws java.io.IOException If the unmarshalling fails
     */
    ResourcePropertySet unmarshallResourcePropertySet(Source source) throws IOException;
}
