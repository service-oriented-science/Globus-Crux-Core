package org.globus.crux.wsrf.properties;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Not used yet.
 *
 * @author Tom Howe
 * @since 1.0
 * @version 1.0
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.METHOD)
public @interface ResourcePropertyTopics {
    ResourcePropertyTopic[] value();
}
