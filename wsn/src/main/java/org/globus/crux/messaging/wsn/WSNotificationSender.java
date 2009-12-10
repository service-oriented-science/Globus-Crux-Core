package org.globus.crux.messaging.wsn;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.ws.addressing.VersionTransformer;
import org.globus.crux.messaging.sender.Notifier;
import org.globus.crux.messaging.subscription.Subscription;
import org.oasis_open.docs.wsn.b_2.Notify;
import org.oasis_open.docs.wsn.b_2.WSNNotificationMessageHolderType;
import org.oasis_open.docs.wsn.bw_2.NotificationConsumer;

import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.transform.Source;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public class WSNotificationSender implements Notifier {

    private Subscription subscription;
    private NotificationConsumer consumer;

    public WSNotificationSender(Subscription subscript) {
        this.subscription = subscript;
        try {
            JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
            factory.getInInterceptors().add(new LoggingInInterceptor());
            factory.getOutInterceptors().add(new LoggingOutInterceptor());
            factory.setServiceClass(NotificationConsumer.class);
            W3CEndpointReference consumerEPR = subscription.getConsumerW3CReference();
            EndpointReferenceType epr = VersionTransformer.convertToInternal(consumerEPR);
            factory.getClientFactoryBean().setEndpointReference(epr);
            consumer = (NotificationConsumer) factory.create();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNotification(Source notificationMessage) {
        Notify notify = new Notify();
        WSNNotificationMessageHolderType messageHolder = new WSNNotificationMessageHolderType();
        notify.getNotificationMessage().add(messageHolder);
//        messageHolder.setProducerReference(subscription.getProducerW3CReference());
//        messageHolder.setSubscriptionReference(subscription.getSubscriptionReference());
        WSNNotificationMessageHolderType.Message message = new WSNNotificationMessageHolderType.Message();
        message.setAny(notificationMessage);
        consumer.notify(notify);
    }

    public String getSubscriptionId() {
        return subscription.getId();
    }
}
