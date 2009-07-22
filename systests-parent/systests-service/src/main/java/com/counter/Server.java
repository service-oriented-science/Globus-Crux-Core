package com.counter;

import org.globus.wsrf.tools.wsdl.WSDLPreprocessor;
import org.globus.wsrf.tools.wsdl.GenerateBinding;
import org.globus.crux.stateful.AnnotationProcessor;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;

import javax.xml.namespace.QName;
import javax.xml.bind.JAXBContext;
import java.io.File;

import cxf.StatefulServiceWebProvider;
import cxf.jaxb.JAXBStatefulProcessor;
import cxf.jaxb.JAXBCreateProcesor;

/**
 * @author turtlebender
 */
public class Server {

    public static void main(String[] args) throws Exception {
        File outputDir = new File("target/wsdl");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        WSDLPreprocessor pp = new WSDLPreprocessor();
        pp.setInputFile("src/main/wsdl/counter_port_type.wsdl");
        pp.setOutputFile("target/wsdl/counter_flattened.wsdl");
        pp.setPortTypeName("CounterPortType");
        pp.execute();
        Runtime.getRuntime().exec("cp src/main/wsdl/* target/wsdl");
        GenerateBinding gb = new GenerateBinding();
        gb.setFileRoot("target/wsdl/counter");
        gb.setProtocol("http");
        gb.setPortTypeFile("target/wsdl/counter_flattened.wsdl");
        gb.execute();

        JAXBContext jaxb = JAXBContext.newInstance("com.counter");
        CounterService service = new CounterService();
        StatefulServiceWebProvider provider = new StatefulServiceWebProvider();
        AnnotationProcessor processor = new AnnotationProcessor().
                withProvider(provider).
                withProcessor(new JAXBStatefulProcessor(jaxb)).
                withProcessor(new JAXBCreateProcesor(jaxb));
        processor.processObject(service);
        createService(provider);
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
