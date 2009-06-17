package org.globus.crux.stateful;

import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.globus.crux.stateful.resource.ResourceManager;
import org.globus.crux.stateful.resource.LookupServiceMetadata;


public class StatefulServiceFactoryBean<TARGET, KEY> implements FactoryBean {
    private TARGET target;
    private StateAdapter<KEY> stateAdapter;
    private ResourceManager<KEY, Object> resourceManager;

    public Object getObject() throws StatefulServiceException {
        return getStatefulService();
    }

    public Class getObjectType() {
        return target.getClass();
    }

    private boolean isProxy(Class<?> targetClass) {
        return java.lang.reflect.Proxy.class.isAssignableFrom(targetClass)
                || net.sf.cglib.proxy.Proxy.class.isAssignableFrom(targetClass);
    }

    //    @SuppressWarnings("unchecked")
    public TARGET getStatefulService() throws StatefulServiceException {
        if (isProxy(target.getClass())) {
            return target;
        }
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        AbstractServiceMetadata<KEY> metadata;
        if (this.resourceManager == null) {
            metadata = new ServiceMetadata<KEY>(target.getClass());
        } else {
            metadata = new LookupServiceMetadata<KEY, Object>(target.getClass(), this.resourceManager);
        }
        metadata.init();
        StatefulWSAspect<KEY> aspect = new StatefulWSAspect<KEY>(metadata, this.stateAdapter);
        factory.addAspect(aspect);
        target = (TARGET) factory.getProxy();
        return target;
    }

    public boolean isSingleton() {
        return true;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(TARGET target) {
        this.target = target;
    }

    public StateAdapter<KEY> getStateAdapter() {
        return stateAdapter;
    }

    public void setStateAdapter(StateAdapter<KEY> stateAdapter) {
        this.stateAdapter = stateAdapter;
    }

    public ResourceManager<KEY, Object> getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(ResourceManager<KEY, Object> resourceManager) {
        this.resourceManager = resourceManager;
    }
}
