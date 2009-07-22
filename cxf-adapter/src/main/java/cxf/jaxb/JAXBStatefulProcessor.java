package cxf.jaxb;

import cxf.StatefulServiceWebProvider;

import java.lang.reflect.Method;

import org.globus.crux.stateful.Payload;
import org.globus.crux.stateful.StateKey;
import org.globus.crux.stateful.StatefulService;
import org.globus.crux.stateful.StatefulMethod;

import javax.xml.namespace.QName;
import javax.xml.bind.JAXBContext;

/**
 * @author turtlebender
 */
public class JAXBStatefulProcessor implements MethodProcessor {
    JAXBContext jaxb;

    public JAXBStatefulProcessor(JAXBContext jaxb) {
        this.jaxb = jaxb;
    }    

    public boolean canProcess(Method method) {
        return method.isAnnotationPresent(Payload.class) && method.isAnnotationPresent(StatefulMethod.class);
    }

    public void process(Object toProcess, Method method, StatefulServiceWebProvider provider) {
        Payload payload = method.getAnnotation(Payload.class);
        QName payloadQName = new QName(payload.namespace(), payload.localpart());
        StateKey key = toProcess.getClass().getAnnotation(StatefulService.class).value();
        QName keyQName = new QName(key.namespace(), key.localpart());
        JAXBStatefulHandler handler = new JAXBStatefulHandler(keyQName, toProcess, method, jaxb);
        provider.registerHandler(payloadQName, handler);
    }
}
