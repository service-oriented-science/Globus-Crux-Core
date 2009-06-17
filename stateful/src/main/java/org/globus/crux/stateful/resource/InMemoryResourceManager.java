package org.globus.crux.stateful.resource;

import java.util.concurrent.ConcurrentHashMap;


/**
 * Simple In-Memory implementation of ResourceManager.  Although this is threadsafe, it is
 * not persistent, and is not recommended for production use.
 *
 * @param <T> Type of Key.
 * @param <V> Type of Value.
 */
public class InMemoryResourceManager<T, V> implements ResourceManager<T, V> {
    private ConcurrentHashMap<T, V> map = new ConcurrentHashMap<T, V>();

    /**
     * Find a resource based on the key.
     *
     * @param key The key of the resource
     * @return The key associated with the resource.
     * @throws ResourceException If the lookup fails.
     */
    public V findResource(T key) throws ResourceException {
        return map.get(key);
    }

    /**
     * Remove the resource associated with the key.  This will permanently remove the resource.
     *
     * @param key The key of the resource.
     * @return The removed resource.
     * @throws ResourceException If the removal fails.
     */
    public V removeResource(T key) throws ResourceException {
        V result = map.get(key);
        map.remove(key);
        return result;
    }

    /**
     * Store the resource and the associated key.  This should only be used on resources that do
     * not already exist.  Throws an exception if they key already exists.
     *
     * @param key The key to store.
     * @param resource The resource to store.
     * @throws ResourceException If the store operation fails.
     */
    public void storeResource(T key, V resource) throws ResourceException {
        if (!map.contains(key)) {
            map.put(key, resource);
        } else {
            throw new ResourceException("Key already exists in Map");
        }
    }

    /**
     * Update the resource in the resource manager.  This should only be used on resources that
     * do already exist.  Throws an exception if the key does not already exist in the manager.
     *
     * @param key The key to store.
     * @param resource The resource to store.
     * @return The updated resource.
     * @throws ResourceException If the update operation fails.
     */
    public V updateResource(T key, V resource) throws ResourceException {
        if (!map.contains(key)) {
            throw new ResourceException("Key does not exist in map");
        } else {
            map.put(key, resource);
            return resource;
        }
    }
}
