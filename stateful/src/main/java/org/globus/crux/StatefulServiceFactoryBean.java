package org.globus.crux;

import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.beans.factory.FactoryBean;


public class StatefulServiceFactoryBean<T, V> implements FactoryBean {
    private T target;
    private StateAdapter<V> stateAdapter;

    public Object getObject() throws Exception {
        return getStatefulService();
    }

    public Class getObjectType() {
        return target.getClass();
    }

    private boolean isProxy(Class<?> targetClass) {
        return java.lang.reflect.Proxy.class.isAssignableFrom(targetClass)
                || net.sf.cglib.proxy.Proxy.class.isAssignableFrom(targetClass);
    }

    @SuppressWarnings("unchecked")
    public T getStatefulService() throws StatefulServiceException {
        if (isProxy(target.getClass())) {
            return target;
        }
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        StatefulWSAspect<T, V> aspect = new StatefulWSAspect<T, V>(new ServiceMetadata<T, V>(target), this.stateAdapter);
        factory.addAspect(aspect);
        target = (T) factory.getProxy();
        return target;
    }

    public boolean isSingleton() {
        return true;
    }

    public Object getTarget() {
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
}
