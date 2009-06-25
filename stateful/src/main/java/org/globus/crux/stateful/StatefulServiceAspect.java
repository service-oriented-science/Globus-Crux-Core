package org.globus.crux.stateful;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.globus.crux.stateful.resource.ResourceException;
import org.globus.crux.stateful.resource.ResourceManager;
import org.globus.crux.stateful.utils.InjectionUtils;
import org.globus.crux.stateful.utils.ThreadLocalAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * This is an aspect which injects State into an annotated target in a thread-safe way.
 * The annotation is defined using the @AspectJ notation.  At the moment this only works with
 * field injection, but soon it will support setter injection in the future as well.
 *
 * @author Tom Howe
 * @version 1.0
 * @param <T> The type associated with the field to be injected.
 * @since 1.0
 */
@Aspect
@Component
public class StatefulServiceAspect<T, V> {
    private Logger logger = LoggerFactory.getLogger(StatefulServiceAspect.class);

    private StateAdapter<T> stateAdapter;

    private ResourceManager<T, V> resourceManager;

    private ServiceMetadata<V>  serviceMetadata;

    /**
     * SimpleConstructor for the aspect which requires the metadata as well as the adapter
     * which retrieves the state value for each request.
     *
     * @param metadata The ServiceMetadata containing the fields to be injected.
     * @param adapter  This adapter retreives the state value.
     */
    public StatefulServiceAspect(ServiceMetadata<V> metadata, StateAdapter<T> adapter) {
        this.serviceMetadata = metadata;
        this.stateAdapter = adapter;
    }

    public StateAdapter<T> getStateAdapter() {
        return stateAdapter;
    }

    public void setStateAdapter(StateAdapter<T> stateAdapter) {
        this.stateAdapter = stateAdapter;
    }

    public ResourceManager<T, V> getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(ResourceManager<T, V> resourceManager) {
        this.resourceManager = resourceManager;
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
    @Pointcut("@within(statefulService)")
    public void inStatefulWS(StatefulService statefulService) {
    }


    @Pointcut(value = "anyPublicMethod() && inStatefulWS(statefulService) && " +
            "!@annotation(org.globus.crux.stateful.StateTransient) && " +
            "!@annotation(org.globus.crux.stateful.Create) && " +
            "!@annotation(org.globus.crux.stateful.Destroy)",
            argNames = "statefulService")
    public void anyUpdatingStatefulMethod(StatefulService statefulService) {

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
    @Around(value = "anyUpdatingStatefulMethod(statefulService)",
            argNames = "pjp,statefulService")
    public Object instantiateState(ProceedingJoinPoint pjp, StatefulService statefulService)
            throws StatefulServiceException {
        logger.debug("Updating State");
        Object proxy = pjp.getTarget();
        Map<Field, T> keyMap = fillFields(proxy);
        Object result;
        try {
            result = pjp.proceed();
            if (statefulService.autoCommit()) {
                postProcessFields(proxy, keyMap);
            }
        } catch (Throwable t) {
            throw new StatefulServiceException(t);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private void postProcessFields(Object proxy, Map<Field, T> keyMap) throws ResourceException {
        for (Field f : serviceMetadata.getStateInfoFields()) {
            T key = keyMap.get(f);
            this.resourceManager.updateResource(key, (V) InjectionUtils.extractFieldValue(f, proxy));
        }
    }

    private Map<Field, T> fillFields(Object proxy) throws ResourceException {
        Map<Field, T> keyMap = new HashMap<Field, T>();
        for (Field f : serviceMetadata.getStateInfoFields()) {
            ThreadLocalAdapter<V> adapter = serviceMetadata.getStateInfoProxy(f);
            T key = this.stateAdapter.getState();
            keyMap.put(f, key);
            V resource = resourceManager.findResource(key);
            adapter.set(resource);
            InjectionUtils.injectFieldValue(f, proxy, adapter);
        }
        return keyMap;
    }
}
