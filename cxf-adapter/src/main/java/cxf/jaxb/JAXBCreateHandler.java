package cxf.jaxb;

import org.globus.crux.stateful.CreateState;
import org.w3c.dom.Document;

import javax.xml.ws.WebServiceContext;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.bind.JAXBContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Method;
import java.beans.Introspector;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;

import cxf.Handler;

/**
 * @author turtlebender
 */
public class JAXBCreateHandler implements Handler {
    private Method method;
    private Class<?> returnType = null;
    private JAXBContext jaxb;
    private Object targetService;
    private DocumentBuilderFactory dbf;
    private Method writeKeyMethod;

    public JAXBCreateHandler(Object targetService, Method method, JAXBContext jaxb) {
        this.method = method;
        this.jaxb = jaxb;
        this.targetService = targetService;
        dbf = DocumentBuilderFactory.newInstance();
        CreateState createAnno = method.getAnnotation(CreateState.class);
        if (!createAnno.responseType().equals(Object.class)) {
            returnType = createAnno.responseType();
        }
    }

    public Source handle(WebServiceContext wsc, Source request) throws Exception {
        Object requestObject = jaxb.createUnmarshaller().unmarshal(request);
        Object result = method.invoke(targetService, requestObject);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        if (this.returnType == null) {
            jaxb.createMarshaller().marshal(result, doc);
        } else {
            jaxb.createMarshaller().marshal(result, doc);
            W3CEndpointReference epr = (W3CEndpointReference) wsc.getEndpointReference(doc.getDocumentElement());
            Object object = this.returnType.newInstance();
            BeanInfo spec = Introspector.getBeanInfo(this.returnType);
            if (this.writeKeyMethod == null) {
                for (PropertyDescriptor desc : spec.getPropertyDescriptors()) {
                    if (desc.getPropertyType().equals(W3CEndpointReference.class)) {
                        this.writeKeyMethod = desc.getWriteMethod();
                        break;
                    }
                }
            }
            if (this.writeKeyMethod != null) {
                this.writeKeyMethod.invoke(object, epr);
            }
            doc = db.newDocument();
            jaxb.createMarshaller().marshal(object, doc);
        }
        return new DOMSource(doc);
    }
}
