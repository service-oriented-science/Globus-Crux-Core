package com.counter;

import org.globus.crux.ResourceContext;

import javax.xml.bind.JAXBElement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author turtlebender
 */
public class InMemoryResourceContext<T, V> implements ResourceContext<T, V> {
    private Map<T, V> resourceMap = new ConcurrentHashMap<T, V>();
    private ResourceKeyThreadLocal currentResource = new ResourceKeyThreadLocal();

    class ResourceKeyThreadLocal extends ThreadLocal<Object> {
        public Object getCurrentResource() {
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
        Object key = currentResource.getCurrentResource();
        System.out.println("key = " + key);
        V resource = resourceMap.get(key);
        for(Map.Entry<T,V> entry: resourceMap.entrySet()){
            System.out.println("entry.getKey() = " + entry.getKey());
        }
        return resource;
    }

    public void setCurrentResourceKey(T key) {
        currentResource.setCurrentResourceKey(key);
    }

    public void storeResource(T key, V value) {
        resourceMap.put(key, value);
    }
}
