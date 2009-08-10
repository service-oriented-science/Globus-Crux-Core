package org.globus.crux;

import org.globus.crux.service.ResourceStoreException;

/**
 * This is an implementation of ResourceContext.  Pretty simple.
 *
 * @author Tom Howe
 * @since 1.0
 * @version 1.0
 */
public class DefaultResourceContext<T, V> implements ResourceContext<T, V> {
    //TODO: support external Context, e.g. WebServiceContext
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
