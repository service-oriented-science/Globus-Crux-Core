package org.globus.crux.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a service as being stateful and the methods will reflect the current
 * state.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface StatefulService {
    /**
     * Specifies the key QName for the resource associated with the service.
     * @return
     */
    StateKey value();

    Class publicInterface() default Object.class;
}
