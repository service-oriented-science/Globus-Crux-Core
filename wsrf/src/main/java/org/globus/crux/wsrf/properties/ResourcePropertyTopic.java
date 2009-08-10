package org.globus.crux.wsrf.properties;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Not used yet
 * 
 * @author Tom Howe
 * @since 1.0
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ResourcePropertyTopic {
    String namespace();

    String localpart();
}
