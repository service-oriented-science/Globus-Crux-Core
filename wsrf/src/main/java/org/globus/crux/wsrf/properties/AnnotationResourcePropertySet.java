package org.globus.crux.wsrf.properties;

import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.InvalidResourcePropertyQNameFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.ResourceUnknownFault;
import org.globus.crux.service.StatefulService;

import javax.xml.namespace.QName;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * @author turtlebender
 */
public class AnnotationResourcePropertySet implements ResourcePropertySet {
    private Map<QName, Method> methodMap = new HashMap<QName, Method>();
    private Object target;
    private QName resourceName;

    public AnnotationResourcePropertySet() {
    }

    public Iterator<QName> iterator() {
        return methodMap.keySet().iterator();
    }

    public boolean containsResourceProperty(QName qname) {
        return methodMap.get(qname) != null;
    }

    public QName getResourceName() {
        return resourceName;
    }

    public void setTarget(Object target) {
        this.target = target;
        if(!target.getClass().isAnnotationPresent(StatefulService.class)){
            //TODO: something bad.
        }else{
            StatefulService ss = target.getClass().getAnnotation(StatefulService.class);
            resourceName = new QName(ss.namespace(), ss.resourceName());
        }
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

    public Object getResourceProperty(QName qname) throws InvalidResourcePropertyQNameFault, ResourceUnknownFault {
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
