package org.globus.crux.stateful;

import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.globus.crux.stateful.resource.LookupServiceMetadata;
import org.globus.crux.stateful.resource.ResourceManager;

/**
 * This is a factory object for taking a service (using the term loosely here) object
 * and making the methods stateful.  This will only happen on objects that have been annotated
 * as @StatefulService and requires that one field be marked as @StatefulContext.  The object
 * produced by this factory will have the threadsafe StateInfo injected before each method call.
 *
 * @see org.globus.crux.stateful.StatefulService
 * @see org.globus.crux.stateful.StateInfo
 * 
 * @author turtlebender
 * @since 1.0
 * @version 1.0
 * @param <T> The type of the target bean (both input and output)
 * @param <V> The type of the value returned as "state"
 */
public class StatefulServiceFactory<T, V> {
    private T target;
    private StateAdapter<V> stateAdapter;
    private ResourceManager<V, Object> resourceManager;

    private boolean isProxy(Class<?> targetClass) {
        //Just want to make sure that the target has not already been proxied.
        return java.lang.reflect.Proxy.class.isAssignableFrom(targetClass)
                || net.sf.cglib.proxy.Proxy.class.isAssignableFrom(targetClass);
    }

    /**
     * Create a stateful service from the supplied target bean.  This will advise the target
     * bean with the aspect for injecting the state.
     *
     * @return The advised, stateful bean.
     * @throws org.globus.crux.stateful.StatefulServiceException If advising fails.
     */
    @SuppressWarnings("unchecked")
    public T getStatefulService() throws StatefulServiceException {
        if (isProxy(target.getClass())) {
            return target;
        }
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        AbstractServiceMetadata<V> metadata;
        if (this.resourceManager == null) {
            metadata = new ServiceMetadata<V>(target.getClass());
        } else {
            metadata = new LookupServiceMetadata<V, Object>(target.getClass(), this.resourceManager);
        }
        metadata.init();
        StatefulServiceAspect<V> aspect = new StatefulServiceAspect<V>(metadata, this.stateAdapter);
        factory.addAspect(aspect);
        target = (T) factory.getProxy();
        return target;
    }

    public T getTarget() {
        return target;
    }

    public void setTarget(T target) {
        this.target = target;
    }

    public StateAdapter<V> getStateAdapter() {
        return stateAdapter;
    }

    public void setStateAdapter(StateAdapter<V> stateAdapter) {
        this.stateAdapter = stateAdapter;
    }

    public ResourceManager<V, Object> getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(ResourceManager<V, Object> resourceManager) {
        this.resourceManager = resourceManager;
    }
}
