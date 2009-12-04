package org.globus.crux.wsrf.lifetime;

import org.globus.crux.OperationProvider;
import org.globus.crux.ProviderException;
import org.oasis_open.docs.wsrf.rlw_2.ScheduledResourceTermination;

public class ScheduledResourceLifetimeProvider implements OperationProvider<ScheduledResourceTermination> {

    private ScheduledResourceTermination impl;
    private Object target;

	public ScheduledResourceTermination getImplementation() throws ProviderException {
        if (impl == null && target == null){
            throw new ProviderException("target is required");
        } else if (impl == null){
            impl = new ResourceTerminationImpl(target);
        }
        return impl;
	}

	public Class<ScheduledResourceTermination> getInterface() {
		return ScheduledResourceTermination.class;
	}

	/**
	 * Setter for the service implementation.
	 *
	 * @param target The service implementation.
	 */
    public void setTarget(Object target) {
        this.target = target;
        impl = null;
    }
}
