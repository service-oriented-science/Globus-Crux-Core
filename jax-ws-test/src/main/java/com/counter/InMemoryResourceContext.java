package com.counter;

import org.globus.crux.ResourceContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is an implementation of ResourceContext.  Pretty simple.  resource storage should be
 * moved out of this class.
 *
 * @author turtlebender
 */
public class InMemoryResourceContext<T, V> implements ResourceContext<T, V> {
    private Map<T, V> resourceMap = new ConcurrentHashMap<T, V>();
    private ResourceKeyThreadLocal currentResource = new ResourceKeyThreadLocal();

    class ResourceKeyThreadLocal extends ThreadLocal<T> {
        public T getCurrentResource() {
            return get();
        }

        public void setCurrentResourceKey(T key) {
            set(key);
        }
    }

    public V getResource(T key) {
        return resourceMap.get(key);
    }

    public V getCurrentResource() {
        return resourceMap.get(currentResource.getCurrentResource());
    }

    public void setCurrentResourceKey(T key) {
        currentResource.setCurrentResourceKey(key);
    }

    public void storeResource(T key, V value) {
        resourceMap.put(key, value);
    }
}
