package org.globus.crux.stateful;

import org.springframework.stereotype.Service;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * This annotation marks a service as being stateful and the methods will reflect the current
 * state.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Service
public @interface StatefulService {
    StateKey value();
    /**
     * If this is set to true, state will be automatically perserved at the end of
     * each method.
     * 
     * @return
     */
    boolean autoCommit() default false;

    Class publicInterface() default Object.class;
}
