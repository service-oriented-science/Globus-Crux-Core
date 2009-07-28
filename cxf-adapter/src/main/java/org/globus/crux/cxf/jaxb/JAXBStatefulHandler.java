package org.globus.crux.cxf.jaxb;


import org.globus.crux.service.StateKeyParam;
import org.globus.crux.service.PayloadParam;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * A WSDispatch Handler for calling stateful methods.  This acquires the state from the WS-Addressing headers
 * and passes the key and the request message to the specified method.
 *
 * @since 1.0
 * @version 1.0
 * @author Tom Howe
 */
public class JAXBStatefulHandler extends AbstractJAXBStatefulReflectiveHandler<Object, Object> {
    private Method method;
    private int payloadIndex;
    private int keyIndex;

    public JAXBStatefulHandler(Object targetService, Method method, JAXBContext jaxb) {
        super(targetService, jaxb);
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
