package org.globus.crux.stateful.resource;

/**
 * Very simple CRUD interface for handling Resources.  This will generally be used in conjucntion
 * with org.globus.crux.stateful.ResourcefulStateInfo to lookup resources.
 *
 * @param <T> The type of the Key.
 * @param <V> The type of the Value.
 */
public interface ResourceManager<T, V> {
    V findResource(T key) throws ResourceException;

    V removeResource(T key) throws ResourceException;

    void storeResource(T key, V resource) throws ResourceException;

    V updateResource(T key, V resource) throws ResourceException;
}
