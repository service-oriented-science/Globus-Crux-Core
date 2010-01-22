package org.globus.crux.wsrf.lifetime;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import org.globus.crux.wsrf.StatefulMethod;

/**
 * This marks a method as a destruct method.  After this method is called, the resource
 * associated with the call will be destroyed.  This is used to implement the Resource Lifetime
 * specifications from WSRF.
 *
 * @author turtlebender
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@StatefulMethod
public @interface DestroyState {
    
}
