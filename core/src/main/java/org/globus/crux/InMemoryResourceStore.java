package org.globus.crux;

import org.globus.crux.service.ResourceStoreException;
import org.globus.crux.ResourceStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author turtlebender
 */
public class InMemoryResourceStore<T,V> implements ResourceStore<T,V> {
    private Map<T,V> resourceMap = new ConcurrentHashMap<T,V>();

    public void storeResource(T key, V resource) throws ResourceStoreException{
        resourceMap.put(key, resource);
    }

    public V findResourceByKey(T key) throws ResourceStoreException{
        return resourceMap.get(key);
    }
}
