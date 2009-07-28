package org.globus.crux.cxf.jaxb;

import org.globus.crux.cxf.StatefulServiceWebProvider;
import org.globus.crux.stateful.MethodProcessor;
import org.globus.crux.service.Payload;
import org.globus.crux.service.StatefulMethod;
import org.globus.crux.service.StatefulService;
import org.globus.crux.service.StateKey;

import java.lang.reflect.Method;

import javax.xml.namespace.QName;
import javax.xml.bind.JAXBContext;

/**
 * @author turtlebender
 */
public class JAXBStatefulProcessor implements MethodProcessor {
    JAXBContext jaxb;
    private StatefulServiceWebProvider provider;

    public JAXBStatefulProcessor(JAXBContext jaxb, StatefulServiceWebProvider provider) {
        this.jaxb = jaxb;
        this.provider = provider;
    }    

    public boolean canProcess(Method method) {
        return method.isAnnotationPresent(Payload.class) && method.isAnnotationPresent(StatefulMethod.class);
    }

    public void process(Object toProcess, Method method) {
        Payload payload = method.getAnnotation(Payload.class);
        QName payloadQName = new QName(payload.namespace(), payload.localpart());
        StateKey key = toProcess.getClass().getAnnotation(StatefulService.class).value();
        JAXBStatefulHandler handler = new JAXBStatefulHandler(toProcess, method, jaxb);
        provider.registerHandler(payloadQName, handler);
    }
}
