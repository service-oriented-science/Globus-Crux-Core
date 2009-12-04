package org.globus.crux.wsrf.lifetime;

import org.globus.crux.OperationProvider;
import org.globus.crux.ProviderException;
import org.oasis_open.docs.wsrf.rlw_2.ImmediateResourceTermination;

/**
 * {@link OperationProvider} providing a lifetime method for immediate destruction of
 * a resource.
 *
 * @author Doreen Seider
 */
public class ImmediateResourceLifetimeProvider implements OperationProvider<ImmediateResourceTermination> {

    private ImmediateResourceTermination impl;
    private Object target;

	public ImmediateResourceTermination getImplementation() throws ProviderException {
        if (impl == null && target == null){
            throw new ProviderException("target is required");
        } else if (impl == null){
            impl = new ResourceTerminationImpl(target);
        }
        return impl;
	}

	public Class<ImmediateResourceTermination> getInterface() {
		return ImmediateResourceTermination.class;
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
