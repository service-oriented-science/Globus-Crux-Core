
/*
 * 
 */

package org.globus.wsrf.properties.service;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;
import org.globus.wsrf.properties.GetResourceProperty;
import org.camel.systest.DummyInterface;

/**
 * This class was generated by Apache CXF 2.2
 * Thu Jul 02 12:36:31 CDT 2009
 * Generated source version: 2.2
 * 
 */


@WebServiceClient(name = "CounterService",
                  wsdlLocation = "file:WS-ResourceProperties_service.wsdl",
                  targetNamespace = "http://www.counter.com")
public class WSResourcePropertiesService extends Service {

    public final static URL WSDL_LOCATION;
    public final static QName SERVICE = new QName("http://www.counter.com", "CounterService");
    public final static QName GetResourcePropertyPort = new QName("http://www.counter.com", "CounterPort");
    static {
        URL url = null;
        try {
            url = new URL("file:CounterService.wsdl");
        } catch (MalformedURLException e) {
            System.err.println("Can not initialize the default wsdl from file:WS-ResourceProperties_service.wsdl");
            // e.printStackTrace();
        }
        WSDL_LOCATION = url;
    }

    public WSResourcePropertiesService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public WSResourcePropertiesService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public WSResourcePropertiesService() {
        super(WSDL_LOCATION, SERVICE);
    }

    /**
     * 
     * @return
     *     returns GetResourceProperty
     */
    @WebEndpoint(name = "CounterPort")
    public DummyInterface getGetResourcePropertyPort() {
        return super.getPort(GetResourcePropertyPort, DummyInterface.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns GetResourceProperty
     */
    @WebEndpoint(name = "GetResourcePropertyPort")
    public GetResourceProperty getGetResourcePropertyPort(WebServiceFeature... features) {
        return super.getPort(GetResourcePropertyPort, GetResourceProperty.class, features);
    }
}
