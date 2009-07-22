package org.globus.wsrf.utils;

import org.xml.sax.InputSource;

import javax.wsdl.xml.WSDLLocator;
import java.io.InputStream;

/**
 * @author turtlebender
 */
public class ClassPathWSDLLocator extends AbstractWrapperWSDLLocator {
    public ClassPathWSDLLocator(String wsdlUrl, WSDLLocator parent) {
        super(wsdlUrl, parent);
    }

    public InputSource getInputSource() {
        InputStream ins = ClassPathWSDLLocator.class.getResourceAsStream(this.wsdlUrl);
        InputSource is = new InputSource(ins);
        is.setSystemId(wsdlUrl);
        is.setPublicId(wsdlUrl);
        baseUri = is.getPublicId();
        return is;
    }
}
