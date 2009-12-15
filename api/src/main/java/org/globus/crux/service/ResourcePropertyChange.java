package org.globus.crux.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method or field as a ResourceProperty on which changes a notification will be send.
 *
 * @author Doreen Seider
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ResourcePropertyChange {
    
	/** Namespace of the ResourceProperty. */
    String namespace();
    
    /** Localpart of the ResourceProperty. */
    String[] localparts();
}
