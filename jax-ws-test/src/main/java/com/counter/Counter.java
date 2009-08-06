package com.counter;

import org.globus.crux.ResourceContext;
import org.globus.crux.service.ResourceStoreException;
import org.globus.crux.service.StateKey;
import org.globus.crux.service.StatefulService;
import org.globus.crux.wsrf.properties.ResourceProperty;

//TODO: replace RuntimeExceptions with better exception . . . custom fault
@StatefulService(@StateKey(namespace = "http://counter.com", localpart = "CounterKey"))
public class Counter {
    //So, right now, we are accessing the resource via this context.  I'd like to move this
    //to passing the key directly into the method using StateKeyParam.  Because we are using
    //a proxy approach, we could do such and still take advantage of cxf's ability to unwrap 
    //parameters.
    private ResourceContext<Long, CounterResource> resourceContext;

    public int add(int value) {
        try {
            CounterResource c = resourceContext.getCurrentResource();
            c.getCounterRP().setValue(c.getCounterRP().getValue() + value);
            return c.getCounterRP().getValue();
        } catch (ResourceStoreException e) {
            throw new RuntimeException(e);
        }
    }

    @ResourceProperty(namespace = "http://counter.com", localpart = "CounterRP")
    public CounterRP getCounterRP() {
        try {
            return resourceContext.getCurrentResource().getCounterRP();
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
