package org.globus.crux.service;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * This specifies the QName of the Payload argument.  This can be used to dispatch requests
 * to specific methods.
 *
 * @author Tom Howe
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Payload {
    /**
     * The namespace of the payload.
     * @return the namespace.
     */
    String namespace();

    /**
     * The localpart of the payload.
     * @return the localpart.
     */
    String localpart();
}
