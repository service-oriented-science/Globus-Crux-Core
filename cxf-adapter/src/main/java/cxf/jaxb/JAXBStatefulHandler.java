package cxf.jaxb;

import cxf.Handler;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.WebServiceContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.namespace.QName;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;

import org.globus.crux.stateful.Payload;
import org.globus.crux.stateful.PayloadParam;
import org.globus.crux.stateful.StateKeyParam;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.JAXWSAConstants;
import org.w3c.dom.Document;

/**
 * @author turtlebender
 */
public class JAXBStatefulHandler implements Handler {
    private Method method;
    private QName keyName;
    private JAXBContext jaxb;
    private Object targetService;
    private DocumentBuilderFactory dbf;
    private int payloadIndex;
    private int keyIndex;

    public JAXBStatefulHandler(QName keyName, Object targetService, Method method, JAXBContext jaxb) {
        this.method = method;
        this.jaxb = jaxb;
        this.targetService = targetService;
        this.keyName = keyName;
        dbf = DocumentBuilderFactory.newInstance();
        int count = 0;
        for (Annotation[] annos : this.method.getParameterAnnotations()) {
            for (Annotation anno : annos) {
                if (anno instanceof PayloadParam) {
                    this.payloadIndex = count;
                } else if (anno instanceof StateKeyParam) {
                    this.keyIndex = count;
                }
            }
            count++;
        }
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
        Object[] params = new Object[2];
        params[keyIndex] = key;
        Object payload = jaxb.createUnmarshaller().unmarshal(request);
        params[payloadIndex] = payload;
        Object result = method.invoke(this.targetService, params);
        Document doc = dbf.newDocumentBuilder().newDocument();
        jaxb.createMarshaller().marshal(result, doc);
        return new DOMSource(doc);
    }
}
