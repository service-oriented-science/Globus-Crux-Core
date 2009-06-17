package org.globus.crux.stateful.resource;

import org.globus.crux.stateful.StatefulServiceException;
import org.globus.crux.stateful.utils.AbstractThreadLocalAdapter;


public class ThreadLocalResourceStateInfoAdapter<KEY, VALUE> extends 
        AbstractThreadLocalAdapter<KEY> implements ResourcefulStateInfo<KEY, VALUE> {
    private ResourceManager<KEY, VALUE> resourceManager;

    public ThreadLocalResourceStateInfoAdapter(ResourceManager<KEY, VALUE> resourceManager) {
        this.resourceManager = resourceManager;
    }

    public ThreadLocalResourceStateInfoAdapter() {
    }

    public KEY getResourceId() throws StatefulServiceException {
        return get();
    }

    public VALUE getResource() throws StatefulServiceException {
        return resourceManager.findResource(get());
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(ResourceManager<KEY, VALUE> resourceManager) {
        this.resourceManager = resourceManager;
    }
}
