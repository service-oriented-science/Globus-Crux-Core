package org.globus.crux.messaging;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.log4j.BasicConfigurator;
import org.globus.crux.messaging.sender.Notifier;
import org.globus.crux.messaging.sender.impl.JMSNotifier;
import org.globus.crux.messaging.topic.Topic;
import org.globus.crux.messaging.topic.TopicManager;
import org.globus.crux.messaging.wsn.WSNNotificationProducerImpl;
import org.oasis_open.docs.wsn.b_2.ObjectFactory;
import org.oasis_open.docs.wsn.b_2.Subscribe;
import org.oasis_open.docs.wsn.b_2.WSNFilterType;
import org.oasis_open.docs.wsn.b_2.WSNTopicExpressionType;
import org.oasis_open.docs.wsn.bw_2.InvalidFilterFault;
import org.oasis_open.docs.wsn.bw_2.InvalidMessageContentExpressionFault;
import org.oasis_open.docs.wsn.bw_2.InvalidProducerPropertiesExpressionFault;
import org.oasis_open.docs.wsn.bw_2.InvalidTopicExpressionFault;
import org.oasis_open.docs.wsn.bw_2.NotifyMessageNotSupportedFault;
import org.oasis_open.docs.wsn.bw_2.SubscribeCreationFailedFault;
import org.oasis_open.docs.wsn.bw_2.TopicExpressionDialectUnknownFault;
import org.oasis_open.docs.wsn.bw_2.TopicNotSupportedFault;
import org.oasis_open.docs.wsn.bw_2.UnacceptableInitialTerminationTimeFault;
import org.oasis_open.docs.wsn.bw_2.UnrecognizedPolicyRequestFault;
import org.oasis_open.docs.wsn.bw_2.UnsupportedPolicyRequestFault;
import org.oasis_open.docs.wsn.bw_2.WSNNotificationProducer;
import org.oasis_open.docs.wsrf.rw_2.ResourceUnknownFault;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.jms.ConnectionFactory;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;


/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
//@Configuration
public class FunctionalTest {

    private static final String NOTIFICATION_PRODUCER_URL = "http://localhost:51515/producer";
    private static final String TEST_NAMESPACE_URI = "http://www.globus.org";
    private static final String TEST_TOPIC_NAME = "foo";
    private static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSzzz";
    private static final String BASE_NOTIFICATION_NAMESPACE_URI = "http://docs.oasis-open.org/wsn/b-2";
    private static final String INITIAL_TERMINATION_TIME = "InitialTerminationTime";
    private static final String SIMPLE_TOPIC_DIALECT = "http://docs.oasis-open.org/wsn/t-1/TopicExpression/Simple";
    private static final String TEST_CONSUMER_NS_URI = "http://test.com";
    private static final String TEST_CONSUMER_SERVICE_NAME = "TestConsumerService";
    private static final String NOTIFICATION_PORT_NS_URI = "http://docs.oasis-open.org/wsn/bw-2";
    private static final String NOTIFICATION_CONSUMER_NAME = "NotificationConsumer";
    private static final String APPLICATION_CONTEXT_XML = "/applicationContext.xml";
    private static final String NOTIFICATION_CONSUMER_BEAN = "notificationConsumer";
    private static final String RESOURCE_KEY_NAME = "resourceKey";
    private static final String TOPIC_MANAGER_BEAN = "topicManager";

    public static void main(String[] args) throws Exception {
        //Set up logging
        BasicConfigurator.configure();
        //Instantiate Beans
        FunctionalTest test = new FunctionalTest();
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        W3CEndpointReference consumerEpr =
                (W3CEndpointReference) ((Endpoint)context.getBean(NOTIFICATION_CONSUMER_BEAN)).getEndpointReference();
        TopicManager manager = (TopicManager) context.getBean(TOPIC_MANAGER_BEAN);
        Topic topic = new Topic();
        topic.setQname(new QName(TEST_NAMESPACE_URI, TEST_TOPIC_NAME));
        manager.addTopic(topic);
        Subscribe subscribe = createSubscriptionRequest(consumerEpr);
        WSNNotificationProducer producer = getNotificationProducerClient();
        Map requestContext = ((BindingProvider)producer).getRequestContext();
        requestContext.put(WSNNotificationProducerImpl.RESOURCE_ID_KEY,
                new QName(FunctionalTest.TEST_CONSUMER_NS_URI, RESOURCE_KEY_NAME));
        producer.subscribe(subscribe);
        Notifier notifier = new JMSNotifier(new QName(TEST_NAMESPACE_URI, TEST_TOPIC_NAME),
                (ConnectionFactory) context.getBean("connFact"));
//        notifier.sendNotification(new JAXBSource((JAXBContext) context.getBean("jaxbContext"), new GetMessages()));
        Thread.sleep(15000);
        context.stop();
    }

    private static WSNNotificationProducer getNotificationProducerClient() {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.getInInterceptors().add(new LoggingInInterceptor());
        factory.getOutInterceptors().add(new LoggingOutInterceptor());
        factory.setServiceClass(WSNNotificationProducer.class);
        factory.setAddress(NOTIFICATION_PRODUCER_URL);
        WSNNotificationProducer producer = (WSNNotificationProducer) factory.create();
        return producer;
    }
//
    private static Subscribe createSubscriptionRequest(W3CEndpointReference consumerEpr)
            throws DatatypeConfigurationException, InvalidTopicExpressionFault,
        InvalidMessageContentExpressionFault, UnacceptableInitialTerminationTimeFault,
        InvalidProducerPropertiesExpressionFault, SubscribeCreationFailedFault,
        InvalidFilterFault, UnrecognizedPolicyRequestFault, TopicNotSupportedFault,
        UnsupportedPolicyRequestFault, TopicExpressionDialectUnknownFault, ResourceUnknownFault,
        NotifyMessageNotSupportedFault {

        ObjectFactory objectFac = new ObjectFactory();
        Subscribe subscribeRequest = new Subscribe();
        subscribeRequest.setConsumerReference(consumerEpr);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 1);
        SimpleDateFormat iso8601Format = new SimpleDateFormat(ISO8601_FORMAT);
        JAXBElement<String> initTermTime =
                new JAXBElement<String>(new QName(BASE_NOTIFICATION_NAMESPACE_URI, INITIAL_TERMINATION_TIME),
                        String.class, iso8601Format.format(cal.getTime()));
        subscribeRequest.setInitialTerminationTime(initTermTime);
        subscribeRequest.setConsumerReference(consumerEpr);
        WSNTopicExpressionType topic = objectFac.createWSNTopicExpressionType();
        topic.setDialect(SIMPLE_TOPIC_DIALECT);
        topic.getContent().add(new QName(TEST_NAMESPACE_URI, TEST_TOPIC_NAME).toString());
        WSNFilterType filter = objectFac.createWSNFilterType();
        filter.getAny().add(objectFac.createTopicExpression(topic));
        subscribeRequest.setFilter(filter);
        return subscribeRequest;
    }

    public static W3CEndpointReference getEPR(EndpointReferenceType epr) {
        W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
        String address = epr.getAddress().getValue();
        builder.address(address);
        builder.serviceName(new QName(TEST_CONSUMER_NS_URI, TEST_CONSUMER_SERVICE_NAME));
        builder.endpointName(new QName(NOTIFICATION_PORT_NS_URI, NOTIFICATION_CONSUMER_NAME));
        return builder.build();

    }


}
