package cxf;

import org.w3c.dom.Element;
import org.apache.cxf.ws.addressing.ReferenceParametersType;

/**
 * @author turtlebender
 */
public interface ResourceIdExtractor<T> {

    public T extractId(ReferenceParametersType referenceParameters) throws CXFAddressingException;
}
