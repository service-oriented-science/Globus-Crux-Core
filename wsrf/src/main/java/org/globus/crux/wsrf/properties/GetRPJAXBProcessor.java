package org.globus.crux.wsrf.properties;

import org.globus.crux.cxf.jaxb.MethodProcessor;
import org.globus.crux.cxf.StatefulServiceWebProvider;
import org.globus.crux.stateful.StateKey;
import org.globus.crux.stateful.StatefulService;

import javax.xml.namespace.QName;
import javax.xml.bind.JAXBContext;
import java.lang.reflect.Method;

/**
 * @author turtlebender
 */
public class GetRPJAXBProcessor implements MethodProcessor {
    private GetRPJAXBHandler handler;
    private JAXBContext jaxb;

    public GetRPJAXBProcessor(JAXBContext jaxb) {
        this.jaxb = jaxb;
    }

    public boolean canProcess(Method method) {
        return method.isAnnotationPresent(GetResourceProperty.class);
    }

    public void process(Object toProcess, Method method, StatefulServiceWebProvider provider) {
        if (handler == null) {
            StateKey key = toProcess.getClass().getAnnotation(StatefulService.class).value();
            QName keyName = new QName(key.namespace(), key.localpart());
            handler = new GetRPJAXBHandler(keyName, toProcess, jaxb);
            provider.registerHandler(new QName("http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceProperties-1.2-draft-01.xsd",
                    "GetResourceProperty"), this.handler);
            provider.registerHandler(new QName("http://docs.oasis-open.org/wsrf/rp-2",
                    "GetResourceProperty"), this.handler);
        }
        GetResourceProperty grp = method.getAnnotation(GetResourceProperty.class);
        this.handler.registerMethod(new QName(grp.namespace(), grp.localpart()), method);
    }
}
