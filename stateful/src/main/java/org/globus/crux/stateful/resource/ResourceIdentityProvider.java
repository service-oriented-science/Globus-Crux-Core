package org.globus.crux.stateful.resource;

/**
 * This provider interface provides a key value given a resource.  Implementors of this
 * Interface can specify any semantics for determining the Id from the object, but must return an
 * Id for any object accepted by the implementation.
 *
 * @author turtlebender
 * @since 1.0
 * @version 1.0
 */
public interface ResourceIdentityProvider {

    Object getResourceId(Object resource);

    boolean accepts(Class resourceType);
}
