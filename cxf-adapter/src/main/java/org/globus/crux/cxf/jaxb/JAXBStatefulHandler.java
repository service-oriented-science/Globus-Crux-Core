package org.globus.crux.cxf.jaxb;

import org.globus.crux.stateful.PayloadParam;
import org.globus.crux.stateful.StateKeyParam;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author turtlebender
 */
public class JAXBStatefulHandler extends AbstractJAXBStatefulReflectiveHandler<Object, Object> {
    private Method method;
    private int payloadIndex;
    private int keyIndex;

    public JAXBStatefulHandler(QName keyName, Object targetService, Method method, JAXBContext jaxb) {
        super(keyName, targetService, jaxb);
        this.method = method;
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

    protected Object doHandle(Object key, Object targetService, Object payload) throws Exception {
        Object[] params = new Object[2];
        params[keyIndex] = key;
        params[payloadIndex] = payload;
        return method.invoke(targetService, params);
    }
}
