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

    public V findResource(T key) throws ResourceException {
        return map.get(key);
    }

    public V removeResource(T key) throws ResourceException {
        V result = map.get(key);
        map.remove(key);
        return result;
    }

    public void storeResource(T key, V resource) throws ResourceException {
        if (!map.contains(key)) {
            map.put(key, resource);
        } else {
            throw new ResourceException("Key already exists in Map");
        }
    }

    public V updateResource(T key, V resource) throws ResourceException {
        if (!map.contains(key)) {
            throw new ResourceException("Key does not exist in map");
        } else {
            map.put(key, resource);
            return resource;
        }
    }
}
