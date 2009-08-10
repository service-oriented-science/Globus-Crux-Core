package org.globus.crux;

import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;

/**
 * @author turtlebender
 */
public class DefaultOperationProvider implements OperationProvider{
    DefaultIntroductionAdvisor advisor;

    public DefaultOperationProvider(Class<?> iface, Object impl){
        advisor = new DefaultIntroductionAdvisor(
                        new DelegatingIntroductionInterceptor(impl), iface);
    }

    public Advisor getAdvisor() {
        return advisor;
    }
}
