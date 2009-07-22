package org.globus.crux.cxf;

import org.apache.cxf.ws.addressing.ReferenceParametersType;

/**
 * @author turtlebender
 */
public interface ResourceIdExtractor<T> {

    public T extractId(ReferenceParametersType referenceParameters) throws CXFAddressingException;
}
