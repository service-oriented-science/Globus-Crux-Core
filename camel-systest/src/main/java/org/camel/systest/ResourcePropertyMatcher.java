package org.camel.systest;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import javax.xml.namespace.QName;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author turtlebender
 */
public class ResourcePropertyMatcher extends BaseMatcher {
    QName propQName;

    public ResourcePropertyMatcher(QName propQName) {
        this.propQName = propQName;
    }

    public boolean matches(Object o) {
        ResourceProperty prop = null;
        if (o.getClass().equals(Field.class)) {
            prop = ((Field) o).getAnnotation(ResourceProperty.class);
        } else if (o.getClass().equals(Method.class)) {
            prop = ((Method) o).getAnnotation(ResourceProperty.class);
        }
        if (prop != null) {
            QName fieldQName = new QName(prop.namespace(), prop.localpart());
            if (fieldQName.equals(propQName)) {
                return true;
            }
        }
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void describeTo(Description description) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
