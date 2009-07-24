package org.globus.crux.service;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * @author turtlebender
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface PayloadParam {
}
