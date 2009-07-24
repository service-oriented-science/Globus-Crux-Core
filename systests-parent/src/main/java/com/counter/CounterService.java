package com.counter;


import org.globus.crux.wsrf.properties.GetResourceProperty;
import org.globus.crux.service.StatefulService;
import org.globus.crux.service.StateKey;
import org.globus.crux.service.CreateState;
import org.globus.crux.service.Payload;
import org.globus.crux.service.PayloadParam;
import org.globus.crux.service.StatefulMethod;
import org.globus.crux.service.StateKeyParam;

import javax.xml.bind.JAXBElement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@StatefulService(@StateKey(namespace = "http://counter.com", localpart = "CounterKey"))
public class CounterService {
    private ObjectFactory factory = new ObjectFactory();

    private Map<String, CounterRP> counterMap = new ConcurrentHashMap<String, CounterRP>();
    private AtomicInteger counter = new AtomicInteger(0);

    @CreateState(responseType = CreateCounterResponse.class)
    @Payload(namespace = "http://counter.com", localpart = "createCounter")
    public JAXBElement<String> createCounter(
            @PayloadParam CreateCounter request) {
        int idNum = counter.incrementAndGet();
        String id = "counter" + Integer.toString(idNum);
        counterMap.put(id, new CounterRP());
        return factory.createCounterKey(id);
    }

    @StatefulMethod
    @Payload(namespace = "http://counter.com", localpart = "add")
    public JAXBElement<Integer> add(@StateKeyParam JAXBElement<String> id,
                                    @PayloadParam JAXBElement<Integer> request) {
        CounterRP counter = counterMap.get(id.getValue());
        counter.setValue(counter.getValue() + request.getValue());
        return factory.createAddResponse(counter.getValue());
    }


    @GetResourceProperty(namespace = "http://counter.com", localpart = "CounterRP")
    public CounterRP getCounter(@StateKeyParam JAXBElement<String> id) {
        return counterMap.get(id.getValue());
    }
}
