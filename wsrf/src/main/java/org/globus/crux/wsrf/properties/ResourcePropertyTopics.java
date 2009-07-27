package org.globus.crux.wsrf.properties;

import org.globus.crux.service.Topic;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author turtlebender
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.METHOD)
@Topic
public @interface ResourcePropertyTopics {
    ResourcePropertyTopic[] value();
}
