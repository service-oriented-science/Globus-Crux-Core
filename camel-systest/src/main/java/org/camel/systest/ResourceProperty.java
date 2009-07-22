package org.camel.systest;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author turtlebender
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface ResourceProperty {
    String namespace();
    String localpart();
}
