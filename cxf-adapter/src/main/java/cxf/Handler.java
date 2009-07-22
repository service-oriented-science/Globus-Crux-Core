package cxf;

import org.apache.cxf.ws.addressing.AddressingProperties;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceContext;

/**
 * @author turtlebender
 */
public interface Handler {
    public Source handle(WebServiceContext wsc, Source request) throws Exception;        
}
