package org.globus.crux.cxf.jaxb;

import org.globus.crux.cxf.WSDispatchHandler;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.JAXWSAConstants;
import org.w3c.dom.Document;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.WebServiceContext;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author turtlebender
 */
public abstract class AbstractJAXBStatefulReflectiveHandler<T, V> implements WSDispatchHandler {
    private JAXBContext jaxb;
    private QName keyName;
    private DocumentBuilderFactory dbf;
    private Object target;

    protected AbstractJAXBStatefulReflectiveHandler(QName keyName, Object target, JAXBContext jaxb) {
        this.jaxb = jaxb;
        this.keyName = keyName;
        dbf = DocumentBuilderFactory.newInstance();
        this.target = target;
    }

    public Source handle(WebServiceContext wsc, Source request) throws Exception {
        AddressingProperties map =
                (AddressingProperties) wsc.getMessageContext().
                        get(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_INBOUND);
        JAXBIntrospector jaxbspec = jaxb.createJAXBIntrospector();
        Object key = null;
        for (Object candidate : map.getToEndpointReference().getReferenceParameters().getAny()) {
            if (jaxbspec.getElementName(candidate).equals(this.keyName)) {
                key = candidate;
                break;
            }
        }
        Object unmarshalled = jaxb.createUnmarshaller().unmarshal(request);
        V result = doHandle(key, target, (T) unmarshalled);
        Document doc = dbf.newDocumentBuilder().newDocument();
        jaxb.createMarshaller().marshal(result, doc);
        return new DOMSource(doc);
    }

    protected abstract V doHandle(Object key, Object target, T payload) throws Exception;
}
