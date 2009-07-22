package org.camel.systest;

import org.globus.wsrf.properties.GetResourceProperty;
import org.globus.wsrf.properties.*;
import org.globus.wsrf.properties.service.WSResourcePropertiesService;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.frontend.ClientProxy;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.jaxws.support.ServiceDelegateAccessor;
import org.apache.cxf.jaxws.ServiceImpl;

import javax.xml.namespace.QName;
import java.net.URL;
/**
 * @author turtlebender
 */
public class TestClient {

    public static void main(String[] args) throws Exception {
        WSResourcePropertiesService service = new WSResourcePropertiesService(
                new URL("http://localhost:9027/myapp/myservice?wsdl"));
        DummyInterface port = service.getGetResourcePropertyPort();
        Client client = ClientProxy.getClient(port);
        Endpoint endpoint = client.getEndpoint();
        endpoint.getActiveFeatures().add(new WSAddressingFeature());
        EndpointReferenceType epr = port.createCounter();
        System.out.println("epr = " + epr);
        ServiceImpl serviceImpl = ServiceDelegateAccessor.get(service);
        port = serviceImpl.getPort(epr, DummyInterface.class);
         port.createCounter();//getResourceProperty(new QName("http://www.counter.com", "value"));
        
    }
}
