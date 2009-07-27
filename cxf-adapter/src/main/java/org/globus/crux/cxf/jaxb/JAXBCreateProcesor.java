package org.globus.crux.cxf.jaxb;

import org.globus.crux.cxf.StatefulServiceWebProvider;
import org.globus.crux.stateful.MethodProcessor;
import org.globus.crux.service.Payload;
import org.globus.crux.service.CreateState;
import org.globus.crux.service.EPRFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

import javax.xml.namespace.QName;
import javax.xml.bind.JAXBContext;
import javax.annotation.Resource;

/**
 * @author turtlebender
 */
public class JAXBCreateProcesor implements MethodProcessor {
    private JAXBContext jaxb;
    private StatefulServiceWebProvider provider;

    public JAXBCreateProcesor(JAXBContext jaxb, StatefulServiceWebProvider provider) {
        this.jaxb = jaxb;
        this.provider = provider;
    }

    public boolean canProcess(Method method) {
        return method.isAnnotationPresent(Payload.class) && method.isAnnotationPresent(CreateState.class);
    }

    public void process(Object toProcess, Method method) {
        Payload payload = method.getAnnotation(Payload.class);
        Field[] fields = toProcess.getClass().getDeclaredFields();
        Field eprField = null;
        for (Field field : fields) {
            if (field.isAnnotationPresent(Resource.class) && EPRFactory.class.isAssignableFrom(field.getType())) {
                eprField = field;
                break;
            }
        }
        JAXBCreateHandler handler;
        if (eprField == null) {
            handler = new JAXBCreateHandler(toProcess, method, jaxb);
        } else {
            handler = new JAXBCreateHandler(toProcess, method, jaxb, eprField);
        }
        provider.registerHandler(new QName(payload.namespace(), payload.localpart()), handler);
    }
}
