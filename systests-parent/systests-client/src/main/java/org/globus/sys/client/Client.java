package org.globus.sys.client;

import com.counter.CreateCounterResponse;
import com.counter.CreateCounter;
import com.counter.CounterPortType;
import com.counter.service.CounterService;
import org.apache.cxf.BusFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.support.ServiceDelegateAccessor;
import org.apache.cxf.jaxws.ServiceImpl;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.GetResourcePropertyResponse;

import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamResult;
import javax.xml.namespace.QName;
import java.io.StringWriter;


/**
 * @author turtlebender
 */
public class Client {
    public static void main(String[] args) throws Exception {
        SpringBusFactory busFactory = new SpringBusFactory();
        Bus bus = busFactory.createBus("src/main/resources/cxf-client-config.xml");
        BusFactory.setDefaultBus(bus);

        CounterService service = new CounterService();
        CreateCounterResponse response = service.getCounterPortTypePort().createCounter(new CreateCounter());
        JAXBContext context = JAXBContext.newInstance("com.counter");
        StringWriter writer = new StringWriter();
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(response, new StreamResult(writer));
        System.out.println(writer.toString());
        ServiceImpl serviceImpl = ServiceDelegateAccessor.get(service);
        CounterPortType port = serviceImpl.getPort(response.getEndpointReference(), CounterPortType.class);
        System.out.println("AddResult: " + port.add(10));
        GetResourcePropertyResponse grpr =
                port.getResourceProperty(new QName("http://counter.com", "CounterRP"));
        writer = new StringWriter();
        marshaller.marshal(grpr, new StreamResult(writer));
        System.out.println(writer.toString());

    }
}
