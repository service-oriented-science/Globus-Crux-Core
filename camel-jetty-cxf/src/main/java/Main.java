import org.apache.camel.CamelContext;
import org.apache.camel.Processor;
import org.apache.camel.Exchange;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.globus.wsrf.tools.wsdl.WSDLPreprocessor;
import org.globus.wsrf.tools.wsdl.GenerateBinding;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;

import javax.xml.bind.JAXBContext;
import java.io.File;

/**
 * @author turtlebender
 */
public class Main {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        RouteBuilder builder = new RouteBuilder() {
            public void configure() throws Exception {
                from("file:/tmp/inputFile.csv").marshal().csv().marshal();

            }
        };

    }

}
