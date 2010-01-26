package org.globus.crux.wsrf.properties.internal;

import java.io.IOException;

import javax.xml.transform.Result;

import org.globus.crux.core.state.ResourcePropertySet;

/**
 * This interface is used to convert a ResourcePropertySet to an XML Result.  Often, this will be combined
 * with the ResourcePropertySetUnmarshaller in the implementation.
 *
 * @author Tom Howe
 * @since 1.0
 * @version 1.0
 */
public interface ResourcePropertySetMarshaller {
    /**
     * Convert a ResourcePropertySet to an XML Result.
     *
     * @param rps The ResourcePropertySet to marshall
     * @param result The output
     * @throws IOException If the marshalling fails
     */
    void marshalResourcePropertySet(ResourcePropertySet rps, Result result) throws IOException;
}
