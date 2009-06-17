package org.globus.crux.stateful.resource;

import org.globus.crux.stateful.StatefulContext;
import org.globus.crux.stateful.StatefulServiceException;
import org.globus.crux.stateful.StatefulService;

@StatefulService
public class DefaultResourceSampleBean<T> implements ResourceSampleBean<T> {
    @StatefulContext
    private ResourcefulStateInfo<Integer, T> context;

    public int getKey() {
        try {
            return context.getResourceId();
        } catch (StatefulServiceException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public T getState() throws StatefulServiceException {
        return context.getResource();
    }

    public ResourcefulStateInfo<Integer, T> getContext() {
        return context;
    }

    public void setContext(ResourcefulStateInfo<Integer, T> context) {
        this.context = context;
    }
}
