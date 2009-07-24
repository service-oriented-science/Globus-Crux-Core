package org.globus.crux.service;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Defines a key QName
 *
 * @author Tom Howe
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StateKey {
    /**
     * The namespace of the key.
     * @return namespace.
     */
    String namespace();

    /**
     * The localpart of the key.
     *
     * @return localpart.
     */
    String localpart();
}
