package com.counter;

import org.globus.crux.service.CreateState;
import org.globus.crux.service.ResourceStoreException;
import org.globus.crux.ResourceStore;
import org.globus.crux.IDGenerator;

/**
 * @author turtlebender
 */
public class Counters {
    ObjectFactory objFactory = new ObjectFactory();
    ResourceStore<Long, CounterResource> resourceStore;
    IDGenerator<Long> idGen;

    //TODO: replace RuntimeException
    @CreateState(namespace = "http://counter.com", localpart = "CounterKey")
    public Long createCounter(){
        long id = idGen.getNextId();
        CounterResource counter = new CounterResource(id);
        try {
            resourceStore.storeResource(id, counter);
        } catch (ResourceStoreException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public void setResourceStore(ResourceStore<Long, CounterResource> resourceStore) {
        this.resourceStore = resourceStore;
    }

    public void setIdGen(IDGenerator<Long> idGen) {
        this.idGen = idGen;
    }
}
