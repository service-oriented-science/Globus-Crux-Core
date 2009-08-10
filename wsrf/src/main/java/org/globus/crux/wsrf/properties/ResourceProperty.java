package org.globus.crux.wsrf.properties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method or field as a ResourceProperty.
 *
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface ResourceProperty {
    /**
     * Namespace of the ResourceProperty.
     *
     */
    String namespace();

    /**
     * Localpart of the ResourceProperty.
     *
     */
    String localpart();
}
