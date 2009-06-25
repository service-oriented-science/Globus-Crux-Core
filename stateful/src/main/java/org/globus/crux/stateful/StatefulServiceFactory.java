package org.globus.crux.stateful;

import org.globus.crux.stateful.resource.ResourceManager;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

/**
 * This is a factory object for taking a service (using the term loosely here) object
 * and making the methods stateful.  This will only happen on objects that have been annotated
 * as @StatefulService and requires that one field be marked as @StatefulContext.  The object
 * produced by this factory will have the threadsafe StateInfo injected before each method call.
 *
 * @author turtlebender
 * @version 1.0
 * @param <T> The type of the target bean (both input and output)
 * @param <K> The type of the key pointing to the state
 * @param <V> The type of the value returned as "state"
 * @see org.globus.crux.stateful.StatefulService
 * @see org.globus.crux.stateful.StateInfo
 * @since 1.0
 */
public class StatefulServiceFactory<T, K, V> {
    private T target;
    private Class<?> targetClass;
    private StateAdapter<K> stateAdapter;
    private ResourceManager<K, V> resourceManager;

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
     * @throws org.globus.crux.stateful.StatefulServiceException
     *          If advising fails.
     */
    @SuppressWarnings("unchecked")
    public T getStatefulService() throws StatefulServiceException {
        if (isProxy(target.getClass())) {
            return target;
        }
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        ServiceMetadata<V> metadata =
                new ServiceMetadata<V>(target.getClass());
        metadata.init();
        StatefulServiceAspect<K, V> aspect = new StatefulServiceAspect<K, V>(metadata, this.stateAdapter);
        aspect.setResourceManager(this.resourceManager);
        factory.addAspect(aspect);
        target = (T) factory.getProxy();
        return target;
    }

    public T getTarget() {
        return target;
    }

    public void setTarget(T target) {
        this.target = target;
        this.targetClass = target.getClass();
    }

    public StateAdapter<K> getStateAdapter() {
        return stateAdapter;
    }

    public void setStateAdapter(StateAdapter<K> stateAdapter) {
        this.stateAdapter = stateAdapter;
    }

    public ResourceManager<K,V> getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(ResourceManager<K,V> resourceManager) {
        this.resourceManager = resourceManager;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }
}
