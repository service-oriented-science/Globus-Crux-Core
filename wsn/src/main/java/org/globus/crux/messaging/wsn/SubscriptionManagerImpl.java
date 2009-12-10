package org.globus.crux.messaging.wsn;

import org.globus.crux.ResourceContext;
import org.globus.crux.service.ResourceStoreException;
import org.globus.crux.service.StatefulService;
import org.oasis_open.docs.wsn.b_2.Renew;
import org.oasis_open.docs.wsn.b_2.RenewResponse;
import org.oasis_open.docs.wsn.b_2.Unsubscribe;
import org.oasis_open.docs.wsn.b_2.UnsubscribeResponse;
import org.oasis_open.docs.wsn.bw_2.SubscribeCreationFailedFault;
import org.oasis_open.docs.wsn.bw_2.UnableToDestroySubscriptionFault;
import org.oasis_open.docs.wsn.bw_2.UnacceptableTerminationTimeFault;
import org.oasis_open.docs.wsn.bw_2.WSNSubscriptionManager;
import org.oasis_open.docs.wsrf.rw_2.ResourceUnknownFault;
import org.w3c.dom.Document;
import org.quartz.SchedulerException;

import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import static org.globus.crux.messaging.utils.XMLDateTimeHelper.*;
import org.globus.crux.messaging.wsn.WSNSubscriptionEPRFactory;
import org.globus.crux.messaging.subscription.Subscription;
import org.globus.crux.messaging.subscription.SubscriptionManager;
import org.globus.crux.messaging.MessagingException;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */

@StatefulService(namespace = "\"http://www.globus.org/namespaces/2004/06/core", keyName = "SubscriptionKey", resourceName = "Subscription")
public class SubscriptionManagerImpl implements WSNSubscriptionManager, WSNSubscriptionEPRFactory {

    public static final QName SUB_KEY = new QName("http://www.globus.org/namespaces/2004/06/core", "SubscriptionKey"); /*NON-NLS*/
    private final static ResourceBundle resourceBundle = ResourceBundle.getBundle("org.globus.crux.messaging.Messaging"); /*NON-NLS*/

    @Resource
    private ResourceContext<String, Subscription> resourceContext;
    @Resource
    WebServiceContext context;
    private JAXBContext jaxb;
    private DocumentBuilderFactory dbf;
    private SubscriptionManager subFact;


    public SubscriptionManagerImpl(){
        dbf = DocumentBuilderFactory.newInstance();
    }

    public W3CEndpointReference createSubscriptionEPR(Subscription subscription) throws SubscribeCreationFailedFault {
        String id = subscription.getId();
        JAXBElement<String> keyElement = new JAXBElement<String>(SUB_KEY, String.class, id);
        Document doc;
        try {
            doc = dbf.newDocumentBuilder().newDocument();
            jaxb.createMarshaller().marshal(keyElement, new DOMResult(doc));
            return (W3CEndpointReference) context.getEndpointReference(doc.getDocumentElement());
        } catch (ParserConfigurationException e) {
            throw new SubscribeCreationFailedFault(resourceBundle.getString("bad.subscriptionKey"), e);
        } catch (JAXBException e) {
            throw new SubscribeCreationFailedFault(resourceBundle.getString("bad.subscriptionKey"), e);
        }
    }

    public RenewResponse renew(@WebParam(partName = "RenewRequest", name = "Renew", targetNamespace = "http://docs.oasis-open.org/wsn/b-2") Renew renewRequest) throws UnacceptableTerminationTimeFault, ResourceUnknownFault {
        try {
            Subscription subscription = this.resourceContext.getCurrentResource();
            String terminationTime = renewRequest.getTerminationTime();
            GregorianCalendar cal = parseTerminationTime(terminationTime).toGregorianCalendar();
            subscription.setTerminationTime(cal);
            subFact.renew(subscription);
            RenewResponse response = new RenewResponse();
            response.setTerminationTime(cal);
            response.setCurrentTime(GregorianCalendar.getInstance());
            return response;
        } catch (ResourceStoreException e) {
            throw new ResourceUnknownFault(resourceBundle.getString("unknown.resource"), e);
        } catch (SchedulerException e) {
            throw new UnacceptableTerminationTimeFault(resourceBundle.getString("invalid.termination.time"), e);
        }
    }

    public UnsubscribeResponse unsubscribe(@WebParam(partName = "UnsubscribeRequest", name = "Unsubscribe", targetNamespace = "http://docs.oasis-open.org/wsn/b-2") Unsubscribe unsubscribeRequest) throws UnableToDestroySubscriptionFault, ResourceUnknownFault {
        try {
            subFact.unsubscribe(resourceContext.getCurrentResource());
            return new UnsubscribeResponse();
        } catch (ResourceStoreException e) {
            throw new ResourceUnknownFault(resourceBundle.getString("unknown.resource"), e);
        } catch (MessagingException e) {
            throw new ResourceUnknownFault(resourceBundle.getString("unknown.resource"), e);
        }
    }

    public JAXBContext getJaxb() {
        return jaxb;
    }

    public void setJaxb(JAXBContext jaxb) {
        this.jaxb = jaxb;
    }

    public SubscriptionManager getSubFact() {
        return subFact;
    }

    public void setSubFact(SubscriptionManager subFact) {
        this.subFact = subFact;
    }
}
