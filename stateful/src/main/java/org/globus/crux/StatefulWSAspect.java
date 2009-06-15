package org.globus.crux;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.globus.crux.utils.InjectionUtils;
import org.globus.crux.utils.ThreadLocalAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Aspect
@Component
public class StatefulWSAspect<T, V> {
    private Logger logger = LoggerFactory.getLogger(StatefulWSAspect.class);

    private StateAdapter<V> stateAdapter;

    private ServiceMetadata<T, V> serviceMetadata;    

    public StatefulWSAspect(ServiceMetadata<T, V> metadata, StateAdapter<V> adapter){
        this.serviceMetadata = metadata;
        this.stateAdapter = adapter;
    }

    public StateAdapter<V> getStateAdapter() {
        return stateAdapter;
    }

    public void setStateAdapter(StateAdapter<V> stateAdapter) {
        this.stateAdapter = stateAdapter;
    }

    @Pointcut("execution(public * *(..))")
    public void anyPublicMethod() {
    }


    @Pointcut("@within(org.globus.crux.StatefulService)")
    public void inStatefulWS() {
    }

    @Around("anyPublicMethod() && inStatefulWS()")
    public Object instantiateState(ProceedingJoinPoint pjp) throws Exception {
        logger.info("Updating State");
        for (Field f : serviceMetadata.getStateInfoFields()) {
            ThreadLocalAdapter<V> adapter = serviceMetadata.getStateInfoProxy(f);
            adapter.set(this.stateAdapter.getState());
            InjectionUtils.injectFieldValue(f, pjp.getTarget(), adapter);
        }
        try{
        return pjp.proceed();
        }catch(Throwable t){
            throw new StatefulServiceException(t);
        }
    }
}
