package org.camel.systest;

import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.globus.wsrf.properties.GetResourceProperty;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author turtlebender
 */
@WebService(targetNamespace = "http://www.counter.com")
public interface DummyInterface  {
    @WebMethod(operationName = "createCounter", action = "http://www.counter.com/createCounter")
    public EndpointReferenceType createCounter();
}
