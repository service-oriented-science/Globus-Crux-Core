package cxf;

import org.apache.cxf.ws.addressing.AddressingProperties;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

/**
 * @author turtlebender
 */
public interface Handler<V, T> {
    public T handle(V request) throws Exception;
}
