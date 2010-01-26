package org.globus.crux.core.internal;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

import org.globus.crux.core.CruxService;
import org.globus.crux.core.OperationProvider;

/**
 * @author Doreen Seider
 */
public class SOAPServiceFactory {
	
    private List<OperationProvider> operationProviders;

    public void addService(CruxService service, Map properties) throws Exception {
    	System.out.println(">>> service added: " +  service.getInterface().getCanonicalName());
    	CruxMixin cruxMixin = new CruxMixin(service, service.getInterface());
    	for (OperationProvider provider : operationProviders) {
    		cruxMixin.addProvider(provider);
    	}
    	Object proxiedService = Proxy.newProxyInstance(SOAPServiceFactory.class.getClassLoader(), new Class[] {}, cruxMixin);
        
        // FIXME create the endpoint here; pass the proxiedService; store the endpoint
    }

    public void removeService(CruxService service, Map properties) {
    	System.out.println(">>> crux service removed: " +  service.getInterface().getCanonicalName());
    	// FIXME remove the appropriate endpoint
    }
	
    public void setOperationProviders(List<OperationProvider> operationProviders) {
    	this.operationProviders = operationProviders;
    	System.out.println(">>> " + operationProviders.size() + " operation providers added");
    }

}
