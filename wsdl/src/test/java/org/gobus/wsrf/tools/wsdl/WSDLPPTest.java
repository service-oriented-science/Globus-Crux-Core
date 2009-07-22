package org.gobus.wsrf.tools.wsdl;

import org.testng.annotations.Test;
import org.globus.wsrf.tools.wsdl.WSDLPreprocessor;
import org.globus.wsrf.tools.wsdl.GenerateBinding;
import org.apache.cxf.wsdl11.ResourceManagerWSDLLocator;

/**
 * @author turtlebender
 */
@Test
public class WSDLPPTest {

    public void testExecute() throws Exception{        
        WSDLPreprocessor pp = new WSDLPreprocessor();
        pp.setInputFile("./src/test/resources/wsdl/counter_port_type.wsdl");
        pp.setOutputFile("./wsdl-out/counter_port_type_flattened.wsdl");
        pp.setQuiet(false);
        pp.setPortTypeName("CounterPortType");
        pp.execute();
        GenerateBinding binding = new GenerateBinding();
        binding.setPortTypeFile("./wsdl-out/counter_port_type_flattened.wsdl");
        binding.setFileRoot("./wsdl-out/counter");
        binding.setProtocol("http");
        binding.execute();
    }
    
}
