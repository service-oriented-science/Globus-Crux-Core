package com.counter;

import java.net.URL;
import java.util.Calendar;

import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryExpressionType;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryResourceProperties_Type;

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
        
        // test scheduled lifetime operation provider
        Holder<Calendar> currentTime = new Holder<Calendar>();
        Holder<Calendar> newTermTime = new Holder<Calendar>();
        Calendar termTime = Calendar.getInstance();
        termTime.add(Calendar.SECOND, 1000);
        counter.setTerminationTime(termTime, newTermTime, currentTime);
        System.out.println(currentTime + " - " + newTermTime);

        // test get resource  property operation provider
        System.out.println(counter.getResourceProperty(new QName("http://counter.com", "Value")));

        // test query resource properties operation provider
        QueryResourceProperties_Type queryResourcePropertiesRequest = new QueryResourceProperties_Type();
        QueryExpressionType queryExpression = new QueryExpressionType();
        queryExpression.setDialect("http://www.w3.org/TR/xpath");
        queryResourcePropertiesRequest.setQueryExpression(queryExpression);
        // FIXME why is this method generated with an argument of type QueryResourceProperties
        // instead of one of type QueryResourceProperties_type like the method on the server side
        // would suggest
//        counter.queryResourceProperties(queryResourcePropertiesRequest);
        
        System.out.println(counter.add(10));
        System.out.println(counter.add(10));
        System.out.println(counter.add(10));
        
        // test immediate lifetime operation provider
        counter.destroy();
    }
}
