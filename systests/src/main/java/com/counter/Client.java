package com.counter;

import org.apache.cxf.BusFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;

import javax.xml.ws.Service;
import javax.xml.ws.Dispatch;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamResult;

/**
 * @author turtlebender
 */
public class Client {

    public static void main(String[] args) throws Exception {
        SpringBusFactory busFactory = new SpringBusFactory();
        Bus bus = busFactory.createBus("src/main/resources/cxf-client-config.xml");
        BusFactory.setDefaultBus(bus);
        QName port = new QName("http://counter.com/service", "CounterPortTypePort");
        JAXBContext jaxb = JAXBContext.newInstance("com.counter:org.oasis.wsrf.properties:org.oasis.wsrf.v200406.properties");
        Service service = Service.create(new QName("http://counter.com/service", "CounterService"));
        service.addPort(port, SOAPBinding.SOAP12HTTP_BINDING,
                "http://localhost:9000/counter");
        Dispatch<Object> dispatch = service.createDispatch(port, jaxb, Service.Mode.PAYLOAD);
        Object o = dispatch.invoke(new CreateCounter());
        Marshaller marshal = jaxb.createMarshaller();
        marshal.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StreamResult result = new StreamResult(System.out);
        marshal.marshal(o, result);
    }
}
