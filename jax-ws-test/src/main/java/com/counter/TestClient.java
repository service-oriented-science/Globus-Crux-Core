package com.counter;

import com.counter.service.CounterFactoryService;
import com.counter.service.CounterService;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBusFactory;

import javax.xml.ws.wsaddressing.W3CEndpointReference;
import java.net.URL;

/**
 * @author turtlebender
 */
public class TestClient {

    public static void main(String[] args) throws Exception {
        Bus bus = new SpringBusFactory().createBus("client.xml");

        BusFactory.setDefaultBus(bus);
        CounterFactoryService counters = new CounterFactoryService(new URL("http://localhost:8080/jax-ws-test/counterFactory?wsdl"));
        CounterFactoryPortType factory = counters.getCounterFactoryPortTypePort();

        W3CEndpointReference epr = factory.createCounter();
        CounterService service = new CounterService();
        CounterPortType counter = service.getPort(epr, CounterPortType.class);
        System.out.println(counter.add(10));
        System.out.println(counter.add(10));
        System.out.println(counter.add(10));
    }
}
