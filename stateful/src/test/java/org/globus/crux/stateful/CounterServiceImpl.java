package org.globus.crux.stateful;

import org.globus.crux.stateful.StatefulService;
import org.globus.crux.stateful.StatefulMethod;
import org.globus.crux.stateful.StateKey;
import org.globus.crux.stateful.StateKeyParam;
import org.globus.crux.stateful.CreateState;
import org.globus.crux.stateful.DestroyState;
import org.globus.crux.stateful.StateProperty;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.HashMap;

/**
 * @author turtlebender
 */

@StatefulService(
        value = @StateKey(localpart = "CounterKey",
                namespace = "http://counter.com"))
public class CounterServiceImpl{
    private Map<String, Counter> counterMap = new HashMap<String, Counter>();

    @StatefulMethod
    public AddResponse add(@StateKeyParam String id, AddRequest request) {
        int currentValue = counterMap.get(id).getValue();
        counterMap.get(id).setValue(currentValue + request.getIncrement());
        AddResponse response = new AddResponse();
        response.setValue(counterMap.get(id).getValue());
        return response;
    }

    @CreateState
    public CreateCounterResponse createCounter(CreateCounterRequest request) {
        CreateCounterResponse response = new CreateCounterResponse();
        response.id = "counter1";
        counterMap.put(response.id, new Counter());
        return response;
    }

    @DestroyState
    public void destroyCounter(@StateKeyParam String id, DestroyCounterRequest request) {
     
    }
}
