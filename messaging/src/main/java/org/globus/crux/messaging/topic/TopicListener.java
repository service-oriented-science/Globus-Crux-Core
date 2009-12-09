package org.globus.crux.messaging.topic;

import org.globus.crux.messaging.MessagingException;
import org.globus.crux.messaging.subscription.Subscription;

import javax.jms.TextMessage;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public interface TopicListener {
    @SuppressWarnings("unchecked")
    void handleMessage(TextMessage message) throws MessagingException;

    void addSubscriber(Subscription worker);

    void removeSubscriber(String subId);

    String getTopic();
}
