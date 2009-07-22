package org.globus.crux.wsrf.properties;

import org.globus.crux.cxf.jaxb.AbstractJAXBStatefulReflectiveHandler;
import org.globus.crux.stateful.StateKeyParam;
import org.oasis.wsrf.v200406.properties.GetResourcePropertyResponse;
import org.oasis.wsrf.v200406.properties.ObjectFactory;

import javax.xml.namespace.QName;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;

/**
 * @author turtlebender
 */
public class GetRPJAXBHandler extends AbstractJAXBStatefulReflectiveHandler<JAXBElement<QName>, GetResourcePropertyResponse> {
    private Map<QName, Method> propertyMap = new HashMap<QName, Method>();
    private ObjectFactory objectFactory = new ObjectFactory();

    public void registerMethod(QName qname, Method method) {
        propertyMap.put(qname, method);
    }

    public GetRPJAXBHandler(QName keyName, Object target, JAXBContext jaxb) {
        super(keyName, target, jaxb);
    }

    protected GetResourcePropertyResponse doHandle(Object key, Object target, JAXBElement<QName> payload) throws Exception {
        GetResourcePropertyResponse response = objectFactory.createGetResourcePropertyResponse();
        Method getter = propertyMap.get(payload.getValue());
        if (getter == null) {
            throw new Exception("Invalid ResourceProperty qname");
            //todo: throw invalid rp exception
        }
        if (getter.getParameterAnnotations() != null && getter.getParameterAnnotations().length == 1) {
            Annotation[] annos = getter.getParameterAnnotations()[0];
            if (annos.length == 1 & annos[0] instanceof StateKeyParam) {
                Object result = getter.invoke(target, key);
                response.getAny().add(result);
            }
        }
        return response;
    }
}
