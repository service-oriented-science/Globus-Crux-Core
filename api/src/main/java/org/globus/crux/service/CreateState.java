package org.globus.crux.service;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * This annotation marks a method as a "factory" method.  The assumption is that the object
 * returned represents the unique key of a resource that has been created.
 *
 * @author Tom Howe
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CreateState {
    String namespace() default "";
    String localpart() default "";   
}
