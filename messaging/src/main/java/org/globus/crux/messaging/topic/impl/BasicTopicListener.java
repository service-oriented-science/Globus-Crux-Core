package org.globus.crux.messaging.topic.impl;

import org.globus.crux.messaging.MessagingException;
import org.globus.crux.messaging.topic.TopicListener;
import org.globus.crux.messaging.sender.Notifier;
import org.globus.crux.messaging.sender.NotifierFactory;
import org.globus.crux.messaging.subscription.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.TextMessage;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public class BasicTopicListener implements TopicListener {
    private NotifierFactory notifierFactory;
    private Map<String, Notifier> notifiers = new LinkedHashMap<String, Notifier>();
    private Logger logger = LoggerFactory.getLogger(getClass());
    private String topic;

    public BasicTopicListener(String topic) {
        this.topic = topic;
    }

    @SuppressWarnings("unchecked")
    public void handleMessage(TextMessage message) throws MessagingException {
        try {
            String o = message.getText();
            for (Notifier notifier : notifiers.values()) {
                notifier.sendNotification(new StreamSource(new StringReader(o)));
            }
        } catch (Exception e) {
            throw new MessagingException(e);
        }
    }

    public void addSubscriber(Subscription subscription) {
        logger.debug("Adding notifier {}", subscription.getId()); /* NON-NLS */
        this.notifiers.put(subscription.getId(), notifierFactory.createNotificationSender(subscription));
    }

    public void removeSubscriber(String subId) {
        logger.debug("Removing notifier {}", subId); /* NON-NLS */
        this.notifiers.remove(subId);
    }

    public NotifierFactory getNotifierFactory() {
        return notifierFactory;
    }

    public void setNotifierFactory(NotifierFactory notifierFactory) {
        this.notifierFactory = notifierFactory;
    }

    public String getTopic() {
        return topic;
    }
}
