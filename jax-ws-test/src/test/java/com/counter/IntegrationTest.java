/*
 * Copyright 1999-2006 University of Chicago
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.counter;

import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.transport.servlet.CXFServlet;

import com.counter.service.CounterFactoryService;
import com.counter.service.CounterService;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * FILL ME
 *
 * @author ranantha@mcs.anl.gov
 */
@Test
public class IntegrationTest {
    Server server;

    @BeforeClass
    public void setup() throws Exception{
        server = new Server(55555);

        Context context = new Context();

        ServletHolder servletHolder = new ServletHolder();

        servletHolder.setInitOrder(1);
        servletHolder.setServlet(new CXFServlet());
        servletHolder.setName("CXFServlet");
        servletHolder.setDisplayName("CXF Servlet");
        context.addServlet(servletHolder, "/*");
        context.addEventListener(new ContextLoaderListener());
        Properties initParams = new Properties();
        initParams.put("contextConfigLocation", "classpath:/beans.xml,classpath:/factorybeans.xml");
        context.setInitParams(initParams);
        server.addHandler(context);
        server.start();
    }

    public void testAdd() throws Exception{
        DatatypeFactory fac = DatatypeFactory.newInstance();
        Bus bus = new SpringBusFactory().createBus("client.xml");

        BusFactory.setDefaultBus(bus);
        CounterFactoryService counters = new CounterFactoryService(new URL("http://localhost:55555/counterFactory?wsdl"));
        CounterFactoryPortType factory = counters.getCounterFactoryPortTypePort();

        W3CEndpointReference epr = factory.createCounter();
        CounterService service = new CounterService();
        CounterPortType counter = service.getPort(epr, CounterPortType.class);
        assertEquals(counter.add(10), 10);
        assertEquals(counter.add(10), 20);
        assertEquals(counter.add(10), 30);
        assertEquals(((JAXBElement)counter.getResourceProperty(
                new QName("http://counter.com", "Value")).getAny().get(0)).getValue(),30);
        counter.setTerminationTime(GregorianCalendar.getInstance(),
                new Holder<Calendar>(GregorianCalendar.getInstance()),
                new Holder<Calendar>(GregorianCalendar.getInstance()));
    }

    @AfterClass
    public void cleanup() throws Exception{
       server.stop();
    }
}
