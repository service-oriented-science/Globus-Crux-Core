package com.counter;

import com.counter.service.CounterService;

import java.net.URL;

import org.apache.cxf.BusFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.interceptor.LoggingOutInterceptor;

import javax.xml.ws.wsaddressing.W3CEndpointReference;

/**
 * @author turtlebender
 */
public class TestClient {

    public static void main(String[] args) throws Exception {
        SpringBusFactory fac = new SpringBusFactory();
        Bus bus = fac.createBus("client.xml");
        BusFactory.setDefaultBus(bus);
        CounterService service = new CounterService(new URL("http://localhost:8080/jax-ws-test/counter?wsdl"));
        CounterPortType port = service.getCounterPortTypePort();
        W3CEndpointReference epr = port.createCounter();
        port = service.getPort(epr, CounterPortType.class);
        System.out.println(port.add(10));
        System.out.println(port.add(10));
        System.out.println(port.add(10));
    }
}
