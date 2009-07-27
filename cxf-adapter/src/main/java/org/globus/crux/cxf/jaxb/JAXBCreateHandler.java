package org.globus.crux.cxf.jaxb;

import org.w3c.dom.Document;

import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.bind.JAXBContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.globus.crux.DispatchHandler;
import org.globus.crux.cxf.CXFCruxContext;
import org.globus.crux.cxf.CXFEPRFactory;
import org.globus.crux.service.CreateState;
import org.globus.crux.service.EPRFactory;

/**
 * @author turtlebender
 */
public class JAXBCreateHandler implements DispatchHandler<CXFCruxContext> {
    private Method method;
    private Field eprFactoryField;
    private JAXBContext jaxb;
    private Object targetService;
    private DocumentBuilderFactory dbf;
    private EPRFactoryProxy facProxy = new EPRFactoryProxy();

    public JAXBCreateHandler(Object targetService, Method method, JAXBContext jaxb) {
        this(targetService, method, jaxb, null);
    }

    public JAXBCreateHandler(Object targetService, Method method, JAXBContext jaxb, Field eprFactoryField) {
        this.method = method;
        this.jaxb = jaxb;
        this.targetService = targetService;
        dbf = DocumentBuilderFactory.newInstance();
        this.eprFactoryField = eprFactoryField;
    }

    public Source handle(CXFCruxContext context, Source request) throws Exception {
        Object requestObject = jaxb.createUnmarshaller().unmarshal(request);
        if (eprFactoryField != null) {
            facProxy.set(new CXFEPRFactory(context.getWebServiceContext(), jaxb));
            EPRFactory eprFac = (EPRFactory) Proxy.newProxyInstance(CXFEPRFactory.class.getClassLoader(),
                    new Class[]{EPRFactory.class}, facProxy);
            eprFactoryField.setAccessible(true);
            eprFactoryField.set(targetService, eprFac);
        }
        Object result = method.invoke(targetService, requestObject);
        if (eprFactoryField != null) {
            facProxy.remove();
        }
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        jaxb.createMarshaller().marshal(result, doc);
        return new DOMSource(doc);
    }

    class EPRFactoryProxy extends ThreadLocal<CXFEPRFactory> implements InvocationHandler {
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            CXFEPRFactory fac = get();
            return fac.createEPRWithId(objects[0]);
        }
    }
}
