package org.globus.crux.stateful.jpa;

import org.globus.crux.stateful.StatefulService;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author turtlebender
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
//@StatefulService
public @interface PersistentState {
}
