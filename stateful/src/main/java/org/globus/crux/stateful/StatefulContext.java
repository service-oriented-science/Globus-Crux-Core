package org.globus.crux.stateful;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This marks a field or Method as containing an injected stateful context field
 *
 * @author Tom Howe
 * @since 1.0
 * @version 1.0
 */
@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StatefulContext {
}
