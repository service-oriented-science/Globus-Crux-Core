package org.globus.crux.service;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author turtlebender
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@StatefulMethod
public @interface DestroyState {
    
}
