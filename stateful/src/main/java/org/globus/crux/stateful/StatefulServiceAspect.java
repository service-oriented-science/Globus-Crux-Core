package org.globus.crux.stateful;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.globus.crux.stateful.utils.InjectionUtils;
import org.globus.crux.stateful.utils.ThreadLocalAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * This is an aspect which injects State into an annotated target in a thread-safe way.
 * The annotation is defined using the @AspectJ notation.  At the moment this only works with
 * field injection, but soon it will support setter injection in the future as well.
 *
 * @author Tom Howe
 * @since 1.0
 * @version 1.0
 * @param <V> The type associated with the field to be injected.
 */
@Aspect
@Component
public class StatefulServiceAspect<V> {
    private Logger logger = LoggerFactory.getLogger(StatefulServiceAspect.class);

    private StateAdapter<V> stateAdapter;

    private AbstractServiceMetadata<V> serviceMetadata;

    /**
     * SimpleConstructor for the aspect which requires the metadata as well as the adapter
     * which retrieves the state value for each request.
     *
     * @param metadata The ServiceMetadata containing the fields to be injected.
     * @param adapter This adapter retreives the state value.
     */
    public StatefulServiceAspect(AbstractServiceMetadata<V> metadata, StateAdapter<V> adapter) {
        this.serviceMetadata = metadata;
        this.stateAdapter = adapter;
    }

    public StateAdapter<V> getStateAdapter() {
        return stateAdapter;
    }

    public void setStateAdapter(StateAdapter<V> stateAdapter) {
        this.stateAdapter = stateAdapter;
    }

    /**
     * Point cut defining any public method.
     */
    @Pointcut("execution(public * *(..))")
    public void anyPublicMethod() {
    }


    /**
     * Pointcut defining any method in a class annotated as @StatefulService
     */
    @Pointcut("@within(org.globus.crux.stateful.StatefulService)")
    public void inStatefulWS() {
    }


    /**
     * This advice is applied to any JoinPoint which satisfies the union of the two other
     * pointcuts specified in this class.  It acquires the state from the state adapter,
     * sets it in a threadlocal adapter object and sets that adapter in the target.
     *
     * @param pjp The JoinPoint to advise
     * @return The result of the invoked JoinPoint
     * @throws StatefulServiceException Thrown if the joinpoint fails when it is invoked.
     */
    @Around("anyPublicMethod() && inStatefulWS()")
    public Object instantiateState(ProceedingJoinPoint pjp) throws StatefulServiceException{
        logger.info("Updating State");
        for (Field f : serviceMetadata.getStateInfoFields()) {
            ThreadLocalAdapter<V> adapter = serviceMetadata.getStateInfoProxy(f);
            adapter.set(this.stateAdapter.getState());
            InjectionUtils.injectFieldValue(f, pjp.getTarget(), adapter);
        }
        try {
            return pjp.proceed();
        } catch (Throwable t) {
            throw new StatefulServiceException(t);
        }
    }
}
