package org.globus.crux.messaging.wsn;

import org.oasis_open.docs.wsn.b_2.Subscribe;
import org.oasis_open.docs.wsn.b_2.UseRaw;
import org.oasis_open.docs.wsn.b_2.WSNTopicExpressionType;
import org.oasis_open.docs.wsn.b_2.WSNInvalidTopicExpressionFaultType;
import org.oasis_open.docs.wsn.b_2.WSNUnrecognizedPolicyRequestFaultType;
import org.oasis_open.docs.wsn.b_2.WSNSubscribeCreationFailedFaultType;
import org.oasis_open.docs.wsn.b_2.WSNUnacceptableInitialTerminationTimeFaultType;
import org.oasis_open.docs.wsn.b_2.WSNUnacceptableTerminationTimeFaultType;
import org.oasis_open.docs.wsn.bw_2.InvalidTopicExpressionFault;
import org.oasis_open.docs.wsn.bw_2.InvalidProducerPropertiesExpressionFault;
import org.oasis_open.docs.wsn.bw_2.InvalidMessageContentExpressionFault;
import org.oasis_open.docs.wsn.bw_2.InvalidFilterFault;
import org.oasis_open.docs.wsn.bw_2.UnrecognizedPolicyRequestFault;
import org.oasis_open.docs.wsn.bw_2.SubscribeCreationFailedFault;
import org.oasis_open.docs.wsn.bw_2.UnacceptableInitialTerminationTimeFault;
import org.oasis_open.docs.wsn.bw_2.UnacceptableTerminationTimeFault;
import org.globus.crux.messaging.topic.TopicManager;
import org.globus.crux.messaging.subscription.Subscription;
import org.globus.crux.messaging.topic.Topic;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.Duration;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.namespace.QName;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.Calendar;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public class SubscriptionFactory {
    public static final QName NOTIFY_PORT = new QName("http://docs.oasis-open.org/wsn/bw-2", "NotificationConsumer"); /*NON-NLS*/

    public static final String WSN_URI = "http://docs.oasis-open.org/wsn/b-2";

    public static final String XPATH1_URI = "http://www.w3.org/TR/1999/REC-xpath-19991116";

    public static final QName QNAME_TOPIC_EXPRESSION = new QName(WSN_URI, "TopicExpression");     /*NON-NLS*/

    public static final QName QNAME_PRODUCER_PROPERTIES = new QName(WSN_URI, "ProducerProperties"); /*NON-NLS*/

    public static final QName QNAME_MESSAGE_CONTENT = new QName(WSN_URI, "MessageContent");  /*NON-NLS*/

    public static final QName QNAME_USE_RAW = new QName(WSN_URI, "UseRaw");  /*NON-NLS*/
    private static ResourceBundle bundle = ResourceBundle.getBundle("org.globus.crux.messaging.Messaging");  /*NON-NLS*/

    private DatatypeFactory datatypeFactory;
    private TopicManager topicManager;

    public SubscriptionFactory() throws DatatypeConfigurationException {
        datatypeFactory = DatatypeFactory.newInstance();
    }

    public Subscription createSubscription(Subscribe subscribe) throws
            InvalidTopicExpressionFault,
            InvalidMessageContentExpressionFault, UnacceptableInitialTerminationTimeFault,
            InvalidProducerPropertiesExpressionFault, SubscribeCreationFailedFault, InvalidFilterFault,
            UnrecognizedPolicyRequestFault {
        return createSubscription(null, subscribe);        
    }

    public Subscription createSubscription(String producerId, Subscribe subscribeRequest) throws
            InvalidTopicExpressionFault,
            InvalidMessageContentExpressionFault, UnacceptableInitialTerminationTimeFault,
            InvalidProducerPropertiesExpressionFault, SubscribeCreationFailedFault, InvalidFilterFault,
            UnrecognizedPolicyRequestFault {
        // Check consumer reference
        Subscription subscript = new Subscription();
        //TODO: Fixme
//        subscript.setConsumerW3CReference(subscribeRequest.getConsumerReference());
        // Check terminationTime
        checkTerminationTime(subscribeRequest, subscript);
        // Check filter
        checkFilter(producerId, subscribeRequest, subscript);
        // Check policy
        checkPolicy(subscribeRequest, subscript);
        // Check all parameters
        checkParameters(subscript);
        // TODO check we can resolve endpoint
        checkTopic(subscribeRequest, subscript);
//        checkContentFilter(subscript);

        subscript.setId(UUID.randomUUID().toString());
        return subscript;
    }

//    private void checkContentFilter(Subscription subscript) throws InvalidMessageContentExpressionFault {
//        if (subscript.getContentFilter() != null && !subscript.getContentFilter().getDialect().equals(XPATH1_URI)) {
//            WSNInvalidMessageContentExpressionFaultType fault = new WSNInvalidMessageContentExpressionFaultType();
//            throw new InvalidMessageContentExpressionFault(bundle.getString("unsupported.dialect")    /*NON-NLS*/
//                    + subscript.getContentFilter().getDialect() + "'", fault);
//        }
//    }

    private void checkTopic(Subscribe request, Subscription subscript) throws InvalidFilterFault {
        
        //TODO: check with TopicManager;
//        if (subscript.getTopic() == null) {
//            WSNInvalidFilterFaultType fault = new WSNInvalidFilterFaultType();
//            throw new InvalidFilterFault(bundle.getString("no.topic"), fault); /*NON-NLS*/
//        }
    }

    private void checkParameters(Subscription subscript) throws SubscribeCreationFailedFault {
        //TODO: fixme
        //TODO: fixme
//        if (subscript.getConsumerW3CReference() == null) {
//            WSNSubscribeCreationFailedFaultType fault = new WSNSubscribeCreationFailedFaultType();
//            throw new SubscribeCreationFailedFault(bundle.getString("invalid.consumerreference.null"), fault);   /*NON-NLS*/
//        }
    }

    private void checkPolicy(Subscribe subscribeRequest, Subscription subscript) throws UnrecognizedPolicyRequestFault {
        if (subscribeRequest.getSubscriptionPolicy() != null) {
            for (Object p : subscribeRequest.getSubscriptionPolicy().getAny()) {
                JAXBElement e;
                if (p instanceof JAXBElement) {
                    e = (JAXBElement) p;
                    p = e.getValue();
                }
                if (p instanceof UseRaw) {
                    subscript.setUseRaw(true);
                } else {
                    WSNUnrecognizedPolicyRequestFaultType fault = new WSNUnrecognizedPolicyRequestFaultType();
                    throw new UnrecognizedPolicyRequestFault(
                            MessageFormat.format(bundle.getString("unrecognized.policy.0"), /*NON-NLS*/
                                    p), fault);
                }
            }
        }
    }

    private void checkFilter(String producerId, Subscribe subscribeRequest, Subscription subscript) throws InvalidTopicExpressionFault, InvalidProducerPropertiesExpressionFault, InvalidMessageContentExpressionFault, InvalidFilterFault {
        if (subscribeRequest.getFilter() != null) {
            for (Object f : subscribeRequest.getFilter().getAny()) {
                JAXBElement e = null;
                if (f instanceof JAXBElement) {
                    e = (JAXBElement) f;
                    f = e.getValue();
                }
                if (f instanceof WSNTopicExpressionType) {
                    if (e != null && !e.getName().equals(QNAME_TOPIC_EXPRESSION)) {
                        WSNInvalidTopicExpressionFaultType fault = new WSNInvalidTopicExpressionFaultType();
                        throw new InvalidTopicExpressionFault(
                                MessageFormat.format(bundle.getString("unrecognized.topicexpression.0"), e), fault); /*NON-NLS*/
                    }
                    QName topicName = QName.valueOf(((WSNTopicExpressionType) f).getContent().get(0).toString());
                    Topic topic = topicManager.getTopic(topicName);
                    
                    subscript.setTopic(topic);
                }
//                } else if (f instanceof QueryExpressionType) {
//                    if (e != null && e.getName().equals(QNAME_PRODUCER_PROPERTIES)) {
//                        WSNInvalidProducerPropertiesExpressionFaultType fault =
//                                new WSNInvalidProducerPropertiesExpressionFaultType();
//                        throw new InvalidProducerPropertiesExpressionFault(
//                                bundle.getString("producerproperties.are.not.supported"), fault); /*NON-NLS*/
//                    } else if (e != null && e.getName().equals(QNAME_MESSAGE_CONTENT)) {
//                        if (subscript.getContentFilter() != null) {
//                            WSNInvalidMessageContentExpressionFaultType fault =
//                                    new WSNInvalidMessageContentExpressionFaultType();
//                            throw new InvalidMessageContentExpressionFault(
//                                    bundle.getString("multiple.messagecontent.filters"), fault);  /*NON-NLS*/
//                        }
//                        subscript.setContentFilter((QueryExpressionType) f);
//                        // Defaults to XPath 1.0
//                        if (subscript.getContentFilter().getDialect() == null) {
//                            subscript.getContentFilter().setDialect(XPATH1_URI);
//                        }
//                    } else {
//                        WSNInvalidFilterFaultType fault = new WSNInvalidFilterFaultType();
//                        throw new InvalidFilterFault(
//                                MessageFormat.format(bundle.getString("unrecognized.filter.0"),/*NON-NLS*/
//                                        e != null ? e.getName() : f), fault);
//                    }
//                } else {
//                    WSNInvalidFilterFaultType fault = new WSNInvalidFilterFaultType();
//                    throw new InvalidFilterFault(MessageFormat.format(bundle.getString("unrecognized.filter.0"),   /*NON-NLS*/
//                            e != null ? e.getName() : f), fault);
//                }
            }
        }
    }

    private void checkTerminationTime(Subscribe subscribeRequest, Subscription subscript) throws UnacceptableInitialTerminationTimeFault {
        if (subscribeRequest.getInitialTerminationTime() != null
                && !subscribeRequest.getInitialTerminationTime().isNil()
                && subscribeRequest.getInitialTerminationTime().getValue() != null) {
            String strTerminationTime = subscribeRequest.getInitialTerminationTime().getValue();
            subscript.setTerminationTime(validateInitialTerminationTime(strTerminationTime.trim()));
        } else {
            //TODO: shouldn't we support this
            WSNUnacceptableInitialTerminationTimeFaultType fault = new WSNUnacceptableInitialTerminationTimeFaultType();
            throw new UnacceptableInitialTerminationTimeFault(
                    bundle.getString("initialterminationtime.is.not.supported"), fault);          /*NON-NLS*/
        }
    }

    protected Calendar getCurrentTime() {
        return Calendar.getInstance();
    }

    SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSzzz");

    protected Calendar parseTerminationTime(String value) {
        try {
            Duration d = datatypeFactory.newDuration(value);
            Calendar c = getCurrentTime();
            d.addTo(c);
            return c;
        } catch (Exception e) {
            // Ignore
        }
        try {
            Duration d = datatypeFactory.newDurationDayTime(value);
            Calendar c = getCurrentTime();
            d.addTo(c);
            return c;
        } catch (Exception e) {
            // Ignore
        }
        try {
            Duration d = datatypeFactory.newDurationYearMonth(value);
            Calendar c = getCurrentTime();
            d.addTo(c);
            return c;
        } catch (Exception e) {
            // Ignore
        }
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(iso8601Format.parse(value));
            return cal;
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }


    protected Calendar validateTerminationTime(String value) throws UnacceptableTerminationTimeFault {
        Calendar tt = parseTerminationTime(value);
        if (tt == null) {
            WSNUnacceptableTerminationTimeFaultType fault = new WSNUnacceptableTerminationTimeFaultType();
            throw new UnacceptableTerminationTimeFault(
                    MessageFormat.format(bundle.getString("unparsable.termination.time.0"), value), fault); /*NON-NLS*/
        }
        Calendar ct = getCurrentTime();
        if (tt.before(ct) || tt.equals(ct)) {
            WSNUnacceptableTerminationTimeFaultType fault = new WSNUnacceptableTerminationTimeFaultType();
            fault.setMinimumTime(ct);
            throw new UnacceptableTerminationTimeFault(bundle.getString("invalid.termination.time"), fault); /*NON-NLS*/
        }
        return tt;
    }

    protected Calendar validateInitialTerminationTime(String value)
            throws UnacceptableInitialTerminationTimeFault {
        Calendar tt = parseTerminationTime(value);
        if (tt == null) {
            WSNUnacceptableInitialTerminationTimeFaultType fault = new WSNUnacceptableInitialTerminationTimeFaultType();
            throw new UnacceptableInitialTerminationTimeFault(
                    MessageFormat.format(bundle.getString("unparsable.termination.time.0"), value), fault);  /*NON-NLS*/
        }
        Calendar ct = getCurrentTime();
        if (tt.before(ct) || tt.equals(ct)) {
            WSNUnacceptableInitialTerminationTimeFaultType fault = new WSNUnacceptableInitialTerminationTimeFaultType();
            fault.setMinimumTime(ct);
            throw new UnacceptableInitialTerminationTimeFault(
                    bundle.getString("invalid.initial.termination.time"), fault); /*NON-NLS*/
        }
        return tt;
    }

    public TopicManager getTopicManager() {
        return topicManager;
    }

    public void setTopicManager(TopicManager topicManager) {
        this.topicManager = topicManager;
    }
}
