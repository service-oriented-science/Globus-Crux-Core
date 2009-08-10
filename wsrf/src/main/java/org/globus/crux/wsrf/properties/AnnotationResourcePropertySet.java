package org.globus.crux.wsrf.properties;

import org.globus.crux.service.StatefulService;
import org.oasis.wsrf.properties.InvalidResourcePropertyQNameFault;
import org.oasis.wsrf.properties.ResourceUnknownFault;

import javax.xml.namespace.QName;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * ResourcePropertySet based on annotations applied to a resource class.  This will process the class
 * annotations and call the appropriate accessors via reflection.
 *
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public class AnnotationResourcePropertySet implements ResourcePropertySet {
    private Map<QName, Method> methodMap = new HashMap<QName, Method>();
    private Object target;
    private QName resourceName;

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("org.globus.crux.wsrf.wsrf");/*NON-NLS*/

    public AnnotationResourcePropertySet() {
    }

    public AnnotationResourcePropertySet(Object target) {
        setTarget(target);
    }


    /**
     * Implements the Iterable interface so that ResourcePropertySet can work with enhanced for-loops.
     *
     * @return Iterator over property names.
     */
    public Iterator<QName> iterator() {
        return methodMap.keySet().iterator();
    }

    /**
     * Determines if the specified resource property is present in this Resource Property Set.
     *
     * @param qname
     * @return True if the ResourceProperty exists, else false
     */
    public boolean containsResourceProperty(QName qname) {
        return methodMap.get(qname) != null;
    }

    /**
     * Get the name of the resource described by this ResourcePropertySet.
     *
     * @return Resource QName.
     */
    public QName getResourceName() {
        return resourceName;
    }

    /**
     * Specified the resource that this ResourcePropertySet will be describing.
     *
     * @param target The resource to describe.
     */
    public void setTarget(Object target) {
        this.target = target;
        if (!target.getClass().isAnnotationPresent(StatefulService.class)) {
            String message = resourceBundle.getString("class.does.not.represent.a.resource");
            throw new IllegalArgumentException(message);
        } else {
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

    /**
     * Get the value of the specified ResourceProperty.
     *
     * @param qname name of the ResourceProperty to retrieve.
     * @return The value of the ResourceProperty.
     * @throws InvalidResourcePropertyQNameFault
     *                              If named ResourceProperty does not exist.
     * @throws ResourceUnknownFault If the resource can not be found.
     */
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
