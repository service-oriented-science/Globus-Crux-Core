package org.globus.crux.wsrf.properties;

import org.globus.crux.stateful.MethodProcessor;
import org.globus.crux.cxf.jaxb.AbstractJAXBStatefulReflectiveHandler;
import org.globus.crux.cxf.StatefulServiceWebProvider;
import org.globus.crux.stateful.StateKey;
import org.globus.crux.stateful.StatefulService;
import org.globus.crux.stateful.StateKeyParam;
import org.oasis.wsrf.properties.GetMultipleResourcePropertiesResponse;
import org.oasis.wsrf.properties.GetMultipleResourceProperties;
import org.oasis.wsrf.properties.ObjectFactory;

import javax.xml.namespace.QName;
import javax.xml.bind.JAXBContext;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * @author turtlebender
 */
public class GetMultipleRPJAXBProcessor implements MethodProcessor {
    private JAXBContext jaxb;
    private GetMRPHandler handler;
    private StatefulServiceWebProvider provider;

    public GetMultipleRPJAXBProcessor(JAXBContext jaxb, StatefulServiceWebProvider provider) {
        this.jaxb = jaxb;
        this.provider = provider;
    }

    public boolean canProcess(Method method) {
        return method.isAnnotationPresent(GetResourceProperty.class);
    }

    public void process(Object toProcess, Method method) {
        if (handler == null) {
            StateKey key = toProcess.getClass().getAnnotation(StatefulService.class).value();
            QName keyName = new QName(key.namespace(), key.localpart());
            handler = new GetMRPHandler(keyName, toProcess, jaxb);
            provider.registerHandler(new QName("http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceProperties-1.2-draft-01.xsd",
                    "GetMultipleResourceProperties"), this.handler);
        }
        GetResourceProperty grp = method.getAnnotation(GetResourceProperty.class);
        this.handler.registerMethod(new QName(grp.namespace(), grp.localpart()), method);
    }

    class GetMRPHandler extends AbstractJAXBStatefulReflectiveHandler<GetMultipleResourceProperties,
            GetMultipleResourcePropertiesResponse> {
        private Map<QName, Method> propertyMap = new HashMap<QName, Method>();
        private ObjectFactory objectFactory = new ObjectFactory();

        public void registerMethod(QName qname, Method method) {
            propertyMap.put(qname, method);
        }

        GetMRPHandler(QName keyName, Object target, JAXBContext jaxb) {
            super(keyName, target, jaxb);
        }

        protected GetMultipleResourcePropertiesResponse doHandle(Object key, Object target, GetMultipleResourceProperties payload) throws Exception {
            GetMultipleResourcePropertiesResponse response = objectFactory.createGetMultipleResourcePropertiesResponse();
            List<QName> properties = payload.getResourceProperty();
            for (QName property : properties) {
                Method getter = propertyMap.get(property);
                if (getter.getParameterAnnotations() != null && getter.getParameterAnnotations().length == 1) {
                    Annotation[] annos = getter.getParameterAnnotations()[0];
                    if (annos.length == 1 & annos[0] instanceof StateKeyParam) {
                        Object result = getter.invoke(target, key);
                        response.getAny().add(result);
                    }
                }
            }
            return response;
        }
    }
}
