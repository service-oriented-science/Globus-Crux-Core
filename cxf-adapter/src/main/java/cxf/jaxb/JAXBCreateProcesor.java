package cxf.jaxb;

import cxf.StatefulServiceWebProvider;

import java.lang.reflect.Method;

import org.globus.crux.stateful.Payload;
import org.globus.crux.stateful.CreateState;

import javax.xml.namespace.QName;
import javax.xml.bind.JAXBContext;

/**
 * @author turtlebender
 */
public class JAXBCreateProcesor implements MethodProcessor {
    private JAXBContext jaxb;

    public JAXBCreateProcesor(JAXBContext jaxb) {
        this.jaxb = jaxb;
    }

    public boolean canProcess(Method method) {
        return method.isAnnotationPresent(Payload.class) && method.isAnnotationPresent(CreateState.class);
    }

    public void process(Object toProcess, Method method, StatefulServiceWebProvider provider) {
        Payload payload = method.getAnnotation(Payload.class);
        JAXBCreateHandler handler = new JAXBCreateHandler(toProcess, method, jaxb);
        provider.registerHandler(new QName(payload.namespace(), payload.localpart()), handler);
    }
}
