package com.counter;


import org.globus.crux.wsrf.properties.GetResourceProperty;
import org.globus.crux.wsrf.properties.ResourcePropertyTopic;
import org.globus.crux.service.StatefulService;
import org.globus.crux.service.StateKey;
import org.globus.crux.service.CreateState;
import org.globus.crux.service.Payload;
import org.globus.crux.service.PayloadParam;
import org.globus.crux.service.StatefulMethod;
import org.globus.crux.service.StateKeyParam;
import org.globus.crux.service.EPRFactory;

import javax.xml.bind.JAXBElement;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * This sample service demonstrates various pieces of the Crux Toolkit.  The goal is to
 * create a service which is stateful, supports WSRF GetResourceProperty and Resource
 * Property based notifications.
 *
 * The crux toolkit aims to be XML centric, as opposed to WSDL centric.  In doing so,
 * the toolkit supports a wider range of service oriented technologies.  
 *
 */
// This annotation specifies that the service is stateful and uses the specified qname to identify
// its resource.
@StatefulService(@StateKey(namespace = "http://counter.com", localpart = "CounterKey"))
public class CounterService {
    //This is the ObjectFactory from JAXB which can be used to create our
    private ObjectFactory factory = new ObjectFactory();

    //The @Resource annotation is a standard java annotation which specifies that this field
    //can be injected from the environment.  The EPRFactory is a transport/protocol specific
    //factory that can be used to simply get an appropriate Endpoint Reference with an
    //associated ID.
    @Resource
    private EPRFactory eprFac;

    //This is the "Resource Management" part of this service.  In our, very simple, example
    //we are storing our resources in a simple hashmap and using an atomic Integer to
    //generate our ids.  In a real life situation, this would likely be replaced with a more
    //robust solution.
    private Map<String, CounterRP> counterMap = new ConcurrentHashMap<String, CounterRP>();
    private AtomicInteger counter = new AtomicInteger(0);

    /**
     * Our first service method is the classic factory pattern.  This method creates a resource
     * and returns an EndpointReference to the resource.  It has 2 annotations.  The first annotation
     * marks this as a factory method and the second annotation specifies the qname of the payload.
     * This qname will be used to determine which method to invoke when a message arrives.  The
     * payload itself will be passed to the method in the parameters with the @PayloadParam annotation.
     *
     * This method uses the EPRFactory injected above to create an appropriate EPR for the protocol
     * being used.
     *
     * @param request The payload of the message
     * @return A wrapped EPR.
     */
    @CreateState()
    @Payload(namespace = "http://counter.com", localpart = "createCounter")
    public CreateCounterResponse createCounter(
            @PayloadParam CreateCounter request) {
        int idNum = counter.incrementAndGet();
        String id = "counter" + Integer.toString(idNum);
        counterMap.put(id, new CounterRP());
        W3CEndpointReference epr = eprFac.createEPRWithId(factory.createCounterKey(id));
        return new CreateCounterResponse(epr);
    }


    /**
     * This is a simple stateful web method.  That means, that when this method is invoked, a
     * certain resource or piece of state should be passed into the method.  The StatefulMethod
     * annotation, not surprising, defines this method as being associated with state.  The key
     * qname specified above in the class level StatefulService annotation is used to extract the
     * key value from the message.  As in the method above, the payload annotation specifies what
     * kind of message will invoke this service.  The third annotation is for notifications.  This
     * annotation says that this method will alter the {http://counter.com}CounterRP resourceproperty
     * and will fire a notification to the topic associated with that resource property.  This functionality
     * is not yet implemented.
     *
     * The parameters also have annotations.  In this case, the first parameter is marked as the StateKeyParam.
     * This means that the key of the state associated with this message will be passed into the method via this
     * parameter.  Because this is XML centric, the key type should be defined in your XML schema and the type
     * should be reflected here.  In this particular example, the type was an element of type string, so the
     * key type is JAXBElement<String> (since we are working in JAXB).
     *
     * As before, the PayloadParam parameter is the body of the message.
     *
     * @param id The id or key extracted from the message by the transport
     * @param request The payload or body of the message.
     * @return The new value of the resource
     */
    @StatefulMethod
    @Payload(namespace = "http://counter.com", localpart = "add")
    @ResourcePropertyTopic(namespace = "http://counter.com", localpart = "CounterRP")
    public JAXBElement<Integer> add(@StateKeyParam JAXBElement<String> id,
                                    @PayloadParam JAXBElement<Integer> request) {
        CounterRP counter = counterMap.get(id.getValue());
        counter.setValue(counter.getValue() + request.getValue());
        return factory.createAddResponse(counter.getValue());
    }


    /**
     * This method looks a little different than the others.  You'll notice that it doesn't have a
     * Payload annotation.  This is because this particular method is not supposed to be exposed directly.
     * This is part of the implementation of the GetResourceProperty operation defined in WSRF.  The annotation
     * specifies that this method exposes a particular resource property.  If support for the GetResourceProperty
     * operation has been configured for this service, these annotations will be used to get the actual values.
     *
     * As before, though, the StateKeyParam parameter annotation specifies the parameter that should accept the
     * resource key.
     * @param id The resource key.
     * @return the value of the {http://counter.com}CounterRP resource property.
     */
    @GetResourceProperty(namespace = "http://counter.com", localpart = "CounterRP")
    public CounterRP getCounter(@StateKeyParam JAXBElement<String> id) {
        return counterMap.get(id.getValue());
    }
}
