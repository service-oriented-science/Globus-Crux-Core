package org.globus.crux;

import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.addressing.soap.MAPCodec;
import org.apache.cxf.ws.addressing.JAXWSAConstants;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.ws.addressing.ReferenceParametersType;
import org.apache.cxf.ws.addressing.ContextUtils;
import org.w3c.dom.Document;
import org.globus.crux.service.StatefulService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.namespace.QName;
import java.util.List;

/**
 * @author turtlebender
 */
public class IdExtractorInterceptor extends AbstractSoapInterceptor {
    private DefaultResourceContext<Object, Object> context;
    private JAXBContext jaxb;
    private JAXBContext bareJaxb;
    private QName resourceKeyName;

    public IdExtractorInterceptor() {
        super(Phase.PRE_PROTOCOL);
        addAfter(MAPCodec.class.getName());
        try {
            bareJaxb = ContextUtils.getJAXBContext();
        } catch (JAXBException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void setTarget(Object target) {
        if (target.getClass().isAnnotationPresent(StatefulService.class)) {
            StatefulService service = target.getClass().getAnnotation(StatefulService.class);
            resourceKeyName = new QName(service.value().namespace(), service.value().localpart());
        }
    }

    public void setJAXBParam(String pkg) {
        try {
            jaxb = JAXBContext.newInstance(pkg);
        } catch (JAXBException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void setContext(DefaultResourceContext<Object, Object> context) {
        this.context = context;
    }

    /**
     * This extracts the appropriate resource key from the request.
     * Actually, this just chooses the first ReferenceParameter.  A real implementation would
     * choose the ReferenceParameter that matches the Resource Key qname.
     *
     * @param message
     * @throws Fault
     */
    public void handleMessage(SoapMessage message) throws Fault {
        AddressingProperties map =
                (AddressingProperties) message.getExchange().getInMessage().
                        get(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_INBOUND);
        EndpointReferenceType epr = map.getToEndpointReference();
        if (epr != null) {
            ReferenceParametersType referenceParams = epr.getReferenceParameters();
            if (referenceParams != null) {
                List<Object> params = referenceParams.getAny();
                if (params != null && params.size() > 0) {
                    Object param = params.get(0);
                    if (resourceKeyName != null) {
                        for(Object candidate: params){
                            if(candidate instanceof JAXBElement){
                                if(((JAXBElement) candidate).getName().equals(resourceKeyName)){
                                    param = candidate;
                                    break;
                                }
                            }
                        }
                    }
                    //This is horribly ugly, but since the JAXBContext used by the addressing
                    //tools can't properly deserialize our elements, this is required.
                    try {
                        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                        DOMResult result = new DOMResult(doc);
                        bareJaxb.createMarshaller().marshal(param, result);
                        param = jaxb.createUnmarshaller().unmarshal(new DOMSource(doc));
                        if (param instanceof JAXBElement) {
                            param = ((JAXBElement) param).getValue();
                        }
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (JAXBException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    context.setCurrentResourceKey(param);
                }
            }
        }
    }
}
