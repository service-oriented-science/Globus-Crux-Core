package cxf.jaxb;

import cxf.StatefulServiceWebProvider;
import cxf.Handler;

import java.lang.reflect.Method;

import org.globus.crux.stateful.GetResourceProperty;
import org.globus.crux.stateful.StatefulService;
import org.globus.crux.stateful.StateKey;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.JAXWSAConstants;

import javax.xml.transform.Source;
import javax.xml.ws.WebServiceContext;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

/**
 * @author turtlebender
 */
public class JAXBGetResourceProcessor implements MethodProcessor {
    private boolean pushed = false;
    private JAXBContext jaxb;
    private QName keyName;

    public JAXBGetResourceProcessor(JAXBContext jaxb) {
        this.jaxb = jaxb;
    }

    public boolean canProcess(Method method) {
        if (method.isAnnotationPresent(GetResourceProperty.class)) ;
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void process(Object toProcess, Method method, StatefulServiceWebProvider provider) {
        if(keyName == null){
            StateKey key = toProcess.getClass().getAnnotation(StatefulService.class).value();            
            keyName = new QName(key.namespace(), key.localpart());
        }
        //To change body of implemented methods use File | Settings | File Templates.
    }

    class GRPHandler implements Handler {
        public Source handle(WebServiceContext wsc, Source request) throws Exception {
            AddressingProperties map =
                    (AddressingProperties) wsc.getMessageContext().
                            get(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_INBOUND);
            JAXBIntrospector jaxbspec = jaxb.createJAXBIntrospector();
            Object key = null;
            for (Object candidate : map.getToEndpointReference().getReferenceParameters().getAny()) {
                if (jaxbspec.getElementName(candidate).equals(keyName)) {
                    key = candidate;
                    break;
                }
            }
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
