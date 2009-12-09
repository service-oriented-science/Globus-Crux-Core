package com.counter;

import java.net.URL;
import java.util.Calendar;

import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBusFactory;

import com.counter.service.CounterFactoryService;
import com.counter.service.CounterService;

/**
 * @author turtlebender
 */
public class TestClient {

    public static void main(String[] args) throws Exception {
        Bus bus = new SpringBusFactory().createBus("client.xml");

        BusFactory.setDefaultBus(bus);
        CounterFactoryService counters = new CounterFactoryService(new URL("http://localhost:55555/counterFactory?wsdl"));
        CounterFactoryPortType factory = counters.getCounterFactoryPortTypePort();

        W3CEndpointReference epr = factory.createCounter();
        CounterService service = new CounterService();
        CounterPortType counter = service.getPort(epr, CounterPortType.class);
        Holder<Calendar> currentTime = new Holder<Calendar>();
        Holder<Calendar> newTermTime = new Holder<Calendar>();
        Calendar termTime = Calendar.getInstance();
        termTime.setTimeInMillis(System.currentTimeMillis() + 10000L);
        System.out.println(termTime.getTimeInMillis());
        counter.setTerminationTime(termTime, newTermTime, currentTime);
        System.out.println(currentTime + " - " + newTermTime);
        System.out.println(counter.getResourceProperty(new QName("http://counter.com", "Value")));
        System.out.println(counter.add(10));
        System.out.println(counter.add(10));
        System.out.println(counter.add(10));        
    }
}
