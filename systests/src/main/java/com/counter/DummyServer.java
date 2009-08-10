package com.counter;

import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;

import javax.xml.namespace.QName;
import javax.xml.bind.JAXBContext;
import java.io.File;

import org.globus.crux.cxf.StatefulServiceWebProvider;
import org.globus.crux.cxf.CXFCruxContextFactory;
import org.globus.crux.stateful.ServiceMethodProcessor;
import org.globus.crux.stateful.ServiceProcessor;
import org.globus.crux.cxf.jaxb.JAXBCreateProcesor;
import org.globus.crux.cxf.jaxb.JAXBStatefulProcessor;

/**
 * @author turtlebender
 */
public class DummyServer {

    public static void main(String[] args) throws Exception {
        File outputDir = new File("target/wsdl");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        Runtime.getRuntime().exec("cp src/main/wsdl/* target/wsdl");
        prepareWsdl("src/main/wsdl/counter_port_type.wsdl", "CounterPortType");

        JAXBContext jaxb = JAXBContext.newInstance("com.counter:org.oasis.wsrf.properties:org.oasis.wsrf.v200406.properties:org.oasis.wsrf.faults:org.oasis.wsrf.v200406.faults");
        CounterService service = new CounterService();
        StatefulServiceWebProvider provider = new StatefulServiceWebProvider();
        //TODO: getQName Dynamically
        CXFCruxContextFactory fac = new CXFCruxContextFactory(jaxb, new QName("http://counter.com", "CounterKey"));
        provider.setFactory(fac);
        ServiceProcessor processor = new ServiceMethodProcessor().
                withProcessor(new JAXBStatefulProcessor(jaxb, provider)).
                withProcessor(new JAXBCreateProcesor(jaxb, provider));
        processor.processObject(service);
        createService(provider);
    }

    private static void prepareWsdl(String compactWsdl, String portType) throws Exception {
//        WSDLPreprocessor pp = new WSDLPreprocessor();
//        pp.setInputFile(compactWsdl);
//        pp.setOutputFile("target/wsdl/counter_flattened.wsdl");
//        pp.setPortTypeName(portType);
//        pp.execute();
//        GenerateBinding gb = new GenerateBinding();
//        gb.setFileRoot("target/wsdl/counter");
//        gb.setProtocol("http");
//        gb.setPortTypeFile("target/wsdl/counter_flattened.wsdl");
//        gb.execute();
    }

    private static void createService(StatefulServiceWebProvider provider) {
        JaxWsServerFactoryBean svrFactory = new JaxWsServerFactoryBean();
        svrFactory.setServiceBean(provider);
        svrFactory.setWsdlURL("target/wsdl/counter_service.wsdl");
        svrFactory.setAddress("http://localhost:9000/counter");
        svrFactory.setServiceName(new QName("http://counter.com/service", "CounterService"));
        svrFactory.setEndpointName(new QName("http://counter.com/service", "CounterPortTypePort"));
        svrFactory.getFeatures().add(new WSAddressingFeature());
        svrFactory.getInInterceptors().add(new LoggingInInterceptor());
        svrFactory.getOutInterceptors().add(new LoggingOutInterceptor());
        svrFactory.create();
    }
}
