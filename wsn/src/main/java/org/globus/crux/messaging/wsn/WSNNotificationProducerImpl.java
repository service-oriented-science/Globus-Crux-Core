package org.globus.crux.messaging.wsn;

import org.oasis_open.docs.wsn.b_2.GetCurrentMessage;
import org.oasis_open.docs.wsn.b_2.GetCurrentMessageResponse;
import org.oasis_open.docs.wsn.b_2.Subscribe;
import org.oasis_open.docs.wsn.b_2.SubscribeResponse;
import org.oasis_open.docs.wsn.b_2.WSNTopicExpressionType;
import org.oasis_open.docs.wsn.bw_2.InvalidFilterFault;
import org.oasis_open.docs.wsn.bw_2.InvalidMessageContentExpressionFault;
import org.oasis_open.docs.wsn.bw_2.InvalidProducerPropertiesExpressionFault;
import org.oasis_open.docs.wsn.bw_2.InvalidTopicExpressionFault;
import org.oasis_open.docs.wsn.bw_2.MultipleTopicsSpecifiedFault;
import org.oasis_open.docs.wsn.bw_2.NoCurrentMessageOnTopicFault;
import org.oasis_open.docs.wsn.bw_2.NotifyMessageNotSupportedFault;
import org.oasis_open.docs.wsn.bw_2.SubscribeCreationFailedFault;
import org.oasis_open.docs.wsn.bw_2.TopicExpressionDialectUnknownFault;
import org.oasis_open.docs.wsn.bw_2.TopicNotSupportedFault;
import org.oasis_open.docs.wsn.bw_2.UnacceptableInitialTerminationTimeFault;
import org.oasis_open.docs.wsn.bw_2.UnrecognizedPolicyRequestFault;
import org.oasis_open.docs.wsn.bw_2.UnsupportedPolicyRequestFault;
import org.oasis_open.docs.wsn.bw_2.WSNNotificationProducer;
import org.oasis_open.docs.wsrf.rw_2.ResourceUnknownFault;
import org.w3c.dom.Document;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.signature.XMLSignatureException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.globus.crux.messaging.topic.TopicManager;
import org.globus.crux.messaging.topic.Topic;
import org.globus.crux.messaging.wsn.EPRUtilities;
import org.globus.crux.messaging.subscription.Subscription;
import org.globus.crux.messaging.subscription.SubscriptionManager;
import org.globus.crux.messaging.MessagingException;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.soap.Addressing;
import javax.annotation.Resource;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.text.MessageFormat;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
@WebService
@Addressing
public class WSNNotificationProducerImpl implements WSNNotificationProducer {
    private TopicManager topicManager;
    private SubscriptionFactory subFact;
    private SubscriptionManager subManager;
    private WSNSubscriptionEPRFactory subscriptionEPRFactory;
    public static final String RESOURCE_ID_KEY = "RESOURCE_ID_KEY";
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("org.globus.crux.messaging.Messaging");         /*NON-NLS*/
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    WebServiceContext context;


    public GetCurrentMessageResponse getCurrentMessage(
            @WebParam(partName = "GetCurrentMessageRequest", name = "GetCurrentMessage",
                    targetNamespace = "http://docs.oasis-open.org/wsn/b-2")
            GetCurrentMessage getCurrentMessageRequest) throws
            NoCurrentMessageOnTopicFault,
            TopicNotSupportedFault,
            ResourceUnknownFault,
            MultipleTopicsSpecifiedFault,
            TopicExpressionDialectUnknownFault,
            InvalidTopicExpressionFault {
        WSNTopicExpressionType topicExpression = getCurrentMessageRequest.getTopic();
        QName topicName = (QName) topicExpression.getContent().get(0);
        String producerId = getResourceKeyAsString();
        Topic topic = topicManager.getTopic(topicName);
        if (topic == null) {
            throw new TopicNotSupportedFault(MessageFormat.format(resourceBundle.getString("this.producer.does.not.support.the.specified.topic.0"), topicName));
        }
        if (topic.getCurrentMessage() == null) {
            throw new NoCurrentMessageOnTopicFault(resourceBundle.getString("this.topic.does.not.have.a.current.message"));
        }
        GetCurrentMessageResponse response = new GetCurrentMessageResponse();
        response.getAny().add(topic.getCurrentMessage());
        return response;
    }

    private String getResourceKeyAsString() throws ResourceUnknownFault {
        Document o = (Document) context.getMessageContext().get(RESOURCE_ID_KEY);
        if (o == null) {
            return null;
        }
        String topicId;
        try {
            topicId = EPRUtilities.getReferenceKeyAsString(o, o.getDocumentElement());
        } catch (CanonicalizationException e) {
            throw new ResourceUnknownFault(resourceBundle.getString("unable.to.convert.resourcekey.to.string"), e);
        } catch (XMLSignatureException e) {
            throw new ResourceUnknownFault(resourceBundle.getString("unable.to.convert.resourcekey.to.string"), e);
        }
        return topicId;
    }

    public SubscribeResponse subscribe(@WebParam(partName = "SubscribeRequest", name = "Subscribe", targetNamespace = "http://docs.oasis-open.org/wsn/b-2") Subscribe subscribeRequest) throws UnrecognizedPolicyRequestFault, SubscribeCreationFailedFault, InvalidProducerPropertiesExpressionFault, UnsupportedPolicyRequestFault, TopicNotSupportedFault, NotifyMessageNotSupportedFault, ResourceUnknownFault, UnacceptableInitialTerminationTimeFault, InvalidMessageContentExpressionFault, InvalidFilterFault, TopicExpressionDialectUnknownFault, InvalidTopicExpressionFault {
        Subscription subscript = subFact.createSubscription(getResourceKeyAsString(), subscribeRequest);
        try {
            subManager.registerConsumer(subscript);
        } catch (SchedulerException e) {
            logger.warn("Unable to register subscription", e);
            throw new SubscribeCreationFailedFault("Unable to register subscription", e);
        } catch (MessagingException e) {
            logger.warn("Unable to register subscription", e);
            throw new SubscribeCreationFailedFault("Unable to register subscription", e);
        }
        SubscribeResponse response = new SubscribeResponse();
        response.setCurrentTime(Calendar.getInstance());
        response.setTerminationTime(subscript.getTerminationTime());
        response.setSubscriptionReference(subscriptionEPRFactory.createSubscriptionEPR(subscript));
        return response;
    }

    public TopicManager getTopicManager() {
        return topicManager;
    }

    public void setTopicManager(TopicManager topicManager) {
        this.topicManager = topicManager;
    }

    public WSNSubscriptionEPRFactory getSubscriptionEPRFactory() {
        return subscriptionEPRFactory;
    }

    public void setSubscriptionEPRFactory(WSNSubscriptionEPRFactory subscriptionEPRFactory) {
        this.subscriptionEPRFactory = subscriptionEPRFactory;
    }

    public SubscriptionFactory getSubFact() {
        return subFact;
    }

    public void setSubFact(SubscriptionFactory subFact) {
        this.subFact = subFact;
    }

    public SubscriptionManager getSubManager() {
        return subManager;
    }

    public void setSubManager(SubscriptionManager subManager) {
        this.subManager = subManager;
    }
}
