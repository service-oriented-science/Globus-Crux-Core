package org.globus.crux;

import java.lang.reflect.Proxy;
import java.util.List;

import org.globus.crux.service.EPRFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * This uses the Spring AOP support to generate a proxy for the service interface using the
 * providers and the service implementation.
 *
 * @author turtlebender
 */
public class SOAPServiceFactory implements FactoryBean {
    private Object target;
    private List<OperationProvider> providers;
    private List<MethodCallWrapper> wrappers;
    private Object proxied;
    private Class interf;
    //TODO: this really probably shouldn't be here  SOS-271
    private EPRFactory eprFactory;

    public Object getObject() throws Exception {
        if (proxied == null) {
            CruxMixin mixin = new CruxMixin(target, interf);
            if (providers != null) {
                for (OperationProvider provider : providers) {
                    mixin.addProvider(provider);
                }
            }
            if (wrappers != null) {
                for (MethodCallWrapper wrapper : wrappers) {
                	mixin.addWrapper(wrapper);
                }
            }

            proxied = Proxy.newProxyInstance(SOAPServiceFactory.class.getClassLoader(), new Class[]{interf},
                    mixin.withEprFactory(eprFactory));            
        }
        return proxied;
    }

    public Class getObjectType() {
        return interf;
    }

    public void setEprFactory(EPRFactory eprFactory) {
        this.eprFactory = eprFactory;
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

    public void setWrappers(List<MethodCallWrapper> wrappers) {
        this.wrappers = wrappers;
    }

    public void setProxied(Object proxied) {
        this.proxied = proxied;
    }

    public void setInterf(Class interf) {
        this.interf = interf;
    }
}
