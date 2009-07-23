package org.globus.crux.wsrf.properties;

import org.globus.crux.cxf.jaxb.AbstractJAXBStatefulReflectiveHandler;
import org.globus.crux.stateful.StateKeyParam;

import javax.xml.namespace.QName;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBContext;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.HashMap;

/**
 * @author turtlebender
 */
public abstract class AbstractGetRPJaxbHandler<T> extends AbstractJAXBStatefulReflectiveHandler<JAXBElement<QName>,
        T> {
    private Map<QName, Method> propertyMap = new HashMap<QName, Method>();

    public AbstractGetRPJaxbHandler(QName keyName, Object target, JAXBContext jaxb) {
        super(keyName, target, jaxb);
    }

    public void registerMethod(QName qname, Method method) {
        propertyMap.put(qname, method);
    }

    protected Object getValue(Object key, Object target, JAXBElement<QName> payload) throws Exception {
        Method getter = propertyMap.get(payload.getValue());
        if (getter == null) {
            throw new Exception("Invalid ResourceProperty qname");
            //todo: throw invalid rp exception
        }
        if (getter.getParameterAnnotations() != null && getter.getParameterAnnotations().length == 1) {
            Annotation[] annos = getter.getParameterAnnotations()[0];
            if (annos.length == 1 & annos[0] instanceof StateKeyParam) {
                return getter.invoke(target, key);
            }
        }
        return null;
    }
}
