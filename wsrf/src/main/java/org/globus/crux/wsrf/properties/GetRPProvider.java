package org.globus.crux.wsrf.properties;

import org.globus.crux.OperationProvider;
import org.springframework.aop.Advisor;
import org.springframework.aop.DynamicIntroductionAdvice;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.beans.factory.FactoryBean;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.GetResourcePropertyResponse;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.GetResourceProperty;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.ResourceUnknownFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.InvalidResourcePropertyQNameFault;
import org.aopalliance.intercept.Interceptor;

import javax.xml.namespace.QName;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.HashMap;

/**
 * @author turtlebender
 */
public class GetRPProvider implements OperationProvider {
    private Advisor advisor;
    private ResourcePropertySet rps;

    public GetRPProvider withRps(ResourcePropertySet rps){
        this.rps = rps;
        return this;
    }

    public void setRps(ResourcePropertySet rps) {
        this.rps = rps;
    }

    public Advisor getAdvisor() {
        if (advisor == null) {
            RPProviderImpl impl = new RPProviderImpl();
            impl.setRpSet(rps);
            DynamicIntroductionAdvice interceptor = new DelegatingIntroductionInterceptor(impl);
            advisor = new DefaultIntroductionAdvisor(interceptor, GetResourceProperty.class);
        }
        return advisor;
    }


}
