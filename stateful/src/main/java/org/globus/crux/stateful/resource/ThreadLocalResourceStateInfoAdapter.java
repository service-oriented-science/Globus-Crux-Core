package org.globus.crux.stateful.resource;

import org.globus.crux.stateful.StatefulServiceException;
import org.globus.crux.stateful.utils.AbstractThreadLocalAdapter;

/**
 * This is ResourcefulStateInfo object which implements thread safety via ThreadLocal objects.
 * As a result, it provides methods for getting both the state id as well as the associated resource.
 * Resources are looked up via the configured ResourceManager object.
 *
 * @param <T> The type of the key.
 * @param <V> The type of the resource.
 *
 * @see org.globus.crux.stateful.resource.ResourceManager
 *
 * @author Tom Howe
 * @since 1.0
 * @version 1.0 
 */
public class ThreadLocalResourceStateInfoAdapter<T, V> extends
        AbstractThreadLocalAdapter<T> implements ResourcefulStateInfo<T, V> {
    private ResourceManager<T, V> resourceManager;

    public ThreadLocalResourceStateInfoAdapter(ResourceManager<T, V> resourceManager) {
        this.resourceManager = resourceManager;
    }

    /**
     * Get the key associated with the current request.
     *
     * @return The current key.
     * @throws ResourceException If error getting current key.
     */
    public T getResourceId() throws ResourceException {
        return get();
    }

    /**
     * Get the state assocated with the current key.
     *
     * @return Current resource.
     * @throws StatefulServiceException If error finding resource.
     */
    public V getState() throws StatefulServiceException {
        return resourceManager.findResource(get());
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(ResourceManager<T, V> resourceManager) {
        this.resourceManager = resourceManager;
    }
}
