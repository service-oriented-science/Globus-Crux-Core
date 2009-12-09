package org.globus.crux.messaging;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public @interface MessageProducer {
    String namespace() default "";
    String localpart();
}
