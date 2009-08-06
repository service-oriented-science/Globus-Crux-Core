package org.globus.crux.wsrf.properties;

import org.globus.crux.OperationProvider;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.GetResourceProperty;

/**
 * @author turtlebender
 */
public class GetRPProvider implements OperationProvider<GetResourceProperty> {
    GetResourceProperty impl;

    public GetResourceProperty getImplementation() {
        return impl;
    }

    public Class<GetResourceProperty> getInterface() {
        return GetResourceProperty.class;
    }

    public GetRPProvider withRps(ResourcePropertySet rps) {
        impl = new RPProviderImpl().withRPSet(rps);
        return this;
    }

    public void setRPSet(ResourcePropertySet rps){
        impl = new RPProviderImpl().withRPSet(rps);
    }
}
