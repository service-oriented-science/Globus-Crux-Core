package org.globus.crux.wsrf.properties;

import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.InvalidResourcePropertyQNameFault;

import javax.xml.namespace.QName;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * @author turtlebender
 */
public class AnnotationResourcePropertySet implements ResourcePropertySet {
    private Map<QName, Method> methodMap = new HashMap<QName, Method>();
    private Object target;

    public AnnotationResourcePropertySet() {
    }

    public void setTarget(Object target) {
        this.target = target;
        for (Method method : target.getClass().getMethods()) {
            if (method.isAnnotationPresent(ResourceProperty.class)) {
                ResourceProperty rp = method.getAnnotation(ResourceProperty.class);
                methodMap.put(new QName(rp.namespace(), rp.localpart()), method);
            }
        }
    }

    public AnnotationResourcePropertySet(Object target) {
        setTarget(target);
    }

    public Object getResourceProperty(QName qname) throws InvalidResourcePropertyQNameFault {
        Method method = methodMap.get(qname);
        if (method == null) {
            throw new InvalidResourcePropertyQNameFault();
        }
        try {
            return method.invoke(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }
}
