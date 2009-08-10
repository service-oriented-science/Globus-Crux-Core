package com.counter;

import org.globus.crux.ResourceContext;
import org.globus.crux.IDGenerator;
import org.globus.crux.service.EPRFactory;
import org.globus.crux.wsrf.properties.ResourceProperty;

import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.jws.WebService;

//Probably would want a StatefulService annotation here to define the ResourceKey qname.
public class CounterImpl implements CounterResource {
    //So, right now, we are accessing the resource via this context.  I'd like to move this
    //to passing the key directly into the method using StateKeyParam.  Because we are using
    //a proxy approach, we could do such and still take advantage of cxf's ability to unwrap
    //parameters.
    private ResourceContext<Long, CounterRP> resourceContext;
    private EPRFactory eprFac;
    private IDGenerator<Long> idGen;
    private ObjectFactory factory = new ObjectFactory();

    public W3CEndpointReference createCounter() {
        Long id = idGen.getNextId();
        Object key = factory.createCounterKey(id);
        resourceContext.storeResource(id, new CounterRP());
        W3CEndpointReference epr = eprFac.createEPRWithId(key);
        return epr;
    }

    public int add(int value) {
        CounterRP c = resourceContext.getCurrentResource();
        c.setValue(c.getValue() + value);
        return c.getValue();
    }

    @ResourceProperty(namespace = "http://counter.com", localpart = "CounterRP")
    public CounterRP getCounterRP() {
        return resourceContext.getCurrentResource();
    }

    public void setResourceContext(ResourceContext<Long, CounterRP> resourceContext) {
        this.resourceContext = resourceContext;
    }

    public void setEprFac(EPRFactory eprFac) {
        this.eprFac = eprFac;
    }

    public void setIdGen(IDGenerator<Long> idGen) {
        this.idGen = idGen;
    }
}
