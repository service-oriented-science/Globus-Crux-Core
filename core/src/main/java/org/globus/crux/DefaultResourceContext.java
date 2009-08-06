package org.globus.crux;

import org.globus.crux.ResourceContext;
import org.globus.crux.ResourceStore;
import org.globus.crux.service.ResourceStoreException;

/**
 * This is an implementation of ResourceContext.  Pretty simple.  resource storage should be
 * moved out of this class.
 *
 * @author turtlebender
 */
public class DefaultResourceContext<T, V> implements ResourceContext<T, V> {
    private ResourceStore<T, V> store;
    private ResourceKeyThreadLocal currentResource = new ResourceKeyThreadLocal();

    class ResourceKeyThreadLocal extends ThreadLocal<T> {
        public T getCurrentResource() {
            return get();
        }

        public void setCurrentResourceKey(T key) {
            set(key);
        }
    }

    public void setStore(ResourceStore<T, V> store) {
        this.store = store;
    }

    public V getResource(T key) throws ResourceStoreException {
        return store.findResourceByKey(key);
    }

    public V getCurrentResource() throws ResourceStoreException{
        return store.findResourceByKey(currentResource.getCurrentResource());
    }

    public void setCurrentResourceKey(T key) {        
        currentResource.setCurrentResourceKey(key);
    }

    public void storeResource(T key, V value) throws ResourceStoreException{
        store.storeResource(key, value);
    }
}
