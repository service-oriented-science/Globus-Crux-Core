package org.globus.crux.messaging.subscription;

import org.quartz.SchedulerException;
import org.globus.crux.messaging.MessagingException;

import javax.xml.namespace.QName;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public interface SubscriptionManager<T> {
    void initTopic(QName topicName) throws SchedulerException;

    void registerConsumer(Subscription subscription) throws SchedulerException, MessagingException;

    void unsubscribe(Subscription subscription) throws MessagingException;

    void unsubscribe(QName topicName, String subId) throws MessagingException;

    void renew(Subscription subscription) throws SchedulerException;

    Subscription getSubscription(String subId);
}
