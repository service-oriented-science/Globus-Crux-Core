package org.globus.crux.wsrf.properties;

import org.globus.crux.service.Topic;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * @author turtlebender
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Topic
public @interface ResourcePropertyTopic {
    String namespace();

    String localpart();
}
