package com.counter;

import org.globus.crux.stateful.StatefulService;
import org.globus.crux.stateful.StateKey;
import org.globus.crux.stateful.CreateState;
import org.globus.crux.stateful.StatefulMethod;
import org.globus.crux.stateful.StateKeyParam;
import org.globus.crux.stateful.GetResourceProperty;
import org.globus.crux.stateful.PayloadParam;
import org.globus.crux.stateful.Payload;

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
    public CounterRP getCounter(@StateKeyParam String id) {
        return new CounterRP();
    }
}
