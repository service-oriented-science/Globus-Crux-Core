package com.counter;

import org.globus.crux.ResourceContext;
import org.globus.crux.service.ResourceStoreException;
import org.globus.crux.service.StatefulService;
import org.globus.crux.wsrf.properties.ResourceProperty;

//TODO: replace RuntimeExceptions with better exception . . . custom fault
@StatefulService(namespace = "http://counter.com", keyName = "CounterKey", resourceName = "CounterRP")
public class Counter {
    private ResourceContext<Long, CounterResource> resourceContext;

    public int add(int value) {
        try {
            CounterResource c = resourceContext.getCurrentResource();
            c.setValue(c.getValue() + value);
            return c.getValue();
        } catch (ResourceStoreException e) {
            throw new RuntimeException(e);
        }
    }

    @ResourceProperty(namespace = "http://counter.com", localpart = "Value")
    public int getCounterRP() {
        try {
            return resourceContext.getCurrentResource().getValue();
        } catch (ResourceStoreException e) {
            throw new RuntimeException(e);
        }
    }

    @ResourceProperty(namespace = "http://counter.com", localpart = "Status")
    public int getStatus() {
        try {
            return resourceContext.getCurrentResource().getStatus();
        } catch (ResourceStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public void setResourceContext(ResourceContext<Long, CounterResource> resourceContext) {
        this.resourceContext = resourceContext;
    }
}
