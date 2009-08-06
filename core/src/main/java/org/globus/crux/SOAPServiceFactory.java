package org.globus.crux;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.globus.crux.service.EPRFactory;

import java.util.List;

/**
 * This uses the Spring AOP support to generate a proxy for the service interface using the
 * providers and the service implementation.
 *
 * @author turtlebender
 */
public class SOAPServiceFactory implements FactoryBean {
    private Object target;
    private List<OperationProvider> providers;
    private Object proxied;
    private Class interf;
    //TODO: this really probably shouldn't be here
    private EPRFactory eprFactory;

    public Object getObject() throws Exception {
        if (proxied == null) {
            ProxyFactory factory = new ProxyFactory(target);
            factory.addAdvice(new CruxMixin(target).withEprFactory(eprFactory));
            if (providers != null) {
                for (OperationProvider provider : providers) {
                    DelegatingIntroductionInterceptor interceptor = new DelegatingIntroductionInterceptor(provider.getImplementation());
                    DefaultIntroductionAdvisor advisor = new DefaultIntroductionAdvisor(interceptor, provider.getInterface());
                    factory.addAdvisor(advisor);
                }
            }
            factory.addInterface(interf);
            proxied = factory.getProxy();
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

    public void setProxied(Object proxied) {
        this.proxied = proxied;
    }

    public void setInterf(Class interf) {
        this.interf = interf;
    }
}
