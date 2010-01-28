package org.globus.crux.core.internal;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

import org.globus.crux.core.CruxService;
import org.globus.crux.core.OperationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Doreen Seider
 */
public class SOAPServiceFactory {
	
    private Logger logger = LoggerFactory.getLogger(getClass());
    private List<OperationProvider> operationProviders;

    public void addService(CruxService service, Map properties) throws Exception {
    	logger.error("crux service added: "
    			+ "\n service impl - " + service.getServiceImpl().toString()
    			+ "\n service iface - " + service.getIface().getCanonicalName()
    			+ "\n interceptors - " + service.getInterceptors().toString());    	
    	
    	CruxMixin cruxMixin = new CruxMixin(service.getServiceImpl(), service.getIface());
    	for (OperationProvider provider : operationProviders) {
    		cruxMixin.addProvider(provider);
    	}
    	Object proxiedService = Proxy.newProxyInstance(SOAPServiceFactory.class.getClassLoader(), new Class[] {}, cruxMixin);
        
        // FIXME create the endpoint here; pass the proxiedService; store the endpoint
    }

    public void removeService(CruxService service, Map properties) {
    	logger.debug("crux service removed: " +  service.getIface().getCanonicalName());
    	// FIXME destroy the appropriate endpoint
    }
	
    public void setOperationProviders(List<OperationProvider> operationProviders) {
    	this.operationProviders = operationProviders;
    	logger.debug(operationProviders.size() + " operation providers added");
    }

}
