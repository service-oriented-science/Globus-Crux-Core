package com.counter;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.aop.framework.ProxyFactory;
import org.globus.crux.OperationProvider;
import org.globus.crux.wsrf.properties.AnnotationResourcePropertySet;
import org.globus.crux.wsrf.properties.GetRPProvider;

import java.util.List;

/**
 * This uses the Spring AOP support to generate a proxy for the service interface using the
 * providers and the service implementation.
 *
 * @author turtlebender
 */
public class ProviderServiceFactory implements FactoryBean {
    private Object target;
    private List<OperationProvider> providers;
    private Object proxied;
    private Class interf;

    public Object getObject() throws Exception {
        if (proxied == null) {
            ProxyFactory factory = new ProxyFactory(target);
            for (OperationProvider provider : providers) {
                factory.addAdvisor(provider.getAdvisor());
            }
            factory.addInterface(interf);
            proxied = factory.getProxy();
        }
        return proxied;
    }

    public Class getObjectType() {
        return interf;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setProviders(List<OperationProvider> providers) {
        this.providers = providers;
    }

    public void setProxied(Object proxied) {
        this.proxied = proxied;
    }

    public void setInterf(Class interf) {
        this.interf = interf;
    }
}
