package org.globus.crux.service;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Used to specify which parameter is associated with the Payload.
 *
 * @author Tom Howe
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface PayloadParam {
}
