package org.globus.crux.wsrf.properties;

import org.globus.crux.OperationProvider;
import org.globus.crux.ProviderException;
import org.oasis.wsrf.properties.GetResourceProperty;

/**
 * @author turtlebender
 */
public class GetRPProvider implements OperationProvider<GetResourceProperty> {
    GetResourceProperty impl;
    private ResourcePropertySet rps;

    public GetResourceProperty getImplementation() throws ProviderException {
        if(impl == null && rps == null){
            throw new ProviderException("ResourcePropertySet is required");
        }else if(impl == null){
            impl = new GetResourcePropertyImpl().withRPSet(rps);
        }
        return impl;
    }

    public Class<GetResourceProperty> getInterface() {
        return GetResourceProperty.class;
    }

    public GetRPProvider withRps(ResourcePropertySet rps) {
        this.rps = rps;
        impl = null;
        return this;
    }

    public void setRPSet(ResourcePropertySet rps) {
        this.rps = rps;
        impl = null;
    }
}
