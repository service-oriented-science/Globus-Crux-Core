package org.camel.systest;

import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.globus.wsrf.properties.GetResourcePropertyResponse;
import org.globus.wsrf.properties.InvalidResourcePropertyQNameFault;
import org.globus.wsrf.properties.ResourceUnknownFault;
import org.globus.wsrf.properties.GetResourceProperty;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.xml.namespace.QName;

/**
 * @author turtlebender
 */
@WebService(targetNamespace = "http://www.counter.com",
        serviceName = "CounterService",
        portName = "CounterPort", endpointInterface = "org.camel.systest.DummyInterface")
public class DummyService implements GetResourceProperty{

    @WebResult(name = "GetResourcePropertyResponse", targetNamespace = "http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceProperties-1.2-draft-01.xsd", partName = "GetResourcePropertyResponse")
    @WebMethod(operationName = "GetResourceProperty",
            action = "http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceProperties/GetResourceProperty")
    public GetResourcePropertyResponse getResourceProperty(@WebParam(partName = "GetResourcePropertyRequest", name = "GetResourceProperty", targetNamespace = "http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceProperties-1.2-draft-01.xsd") QName getResourcePropertyRequest) throws InvalidResourcePropertyQNameFault, ResourceUnknownFault {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
