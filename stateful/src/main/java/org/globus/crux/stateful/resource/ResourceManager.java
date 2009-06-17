package org.globus.crux.stateful.resource;

/**
 * Very simple CRUD interface for handling Resources.  This will generally be used in conjucntion
 * with org.globus.crux.stateful.ResourcefulStateInfo to lookup resources.
 *
 * @param <T> The type of the Key.
 * @param <V> The type of the Value.
 *
 * @author Tom Howe
 * @since 1.0
 * @version 1.0
 */
public interface ResourceManager<T, V> {
    /**
     * Find a resource based on the key.
     *
     * @param key The key of the resource
     * @return The key associated with the resource.
     * @throws ResourceException If the lookup fails.
     */
    V findResource(T key) throws ResourceException;

    /**
     * Remove the resource associated with the key.  This will permanently remove the resource.
     *
     * @param key The key of the resource.
     * @return The removed resource.
     * @throws ResourceException If the removal fails.
     */
    V removeResource(T key) throws ResourceException;

    /**
     * Store the resource and the associated key.  This should only be used on resources that do
     * not already exist.  Implementors may choose to throw an exception if they key
     * already exists.
     *
     * @param key The key to store.
     * @param resource The resource to store.
     * @throws ResourceException If the store operation fails.
     */
    void storeResource(T key, V resource) throws ResourceException;

    /**
     * Update the resource in the resource manager.  This should only be used on resources that
     * do already exist.  Implementors may choose to thrown an exception if the key does not already
     * exist in the manager.
     *
     * @param key The key to store.
     * @param resource The resource to store.
     * @return The updated resource.
     * @throws ResourceException If the update operation fails.
     */
    V updateResource(T key, V resource) throws ResourceException;
}
