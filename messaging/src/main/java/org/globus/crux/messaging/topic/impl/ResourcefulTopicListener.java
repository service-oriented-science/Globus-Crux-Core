package org.globus.crux.messaging.topic.impl;

import org.globus.crux.messaging.MessagingException;
import org.globus.crux.messaging.topic.TopicListener;
import org.globus.crux.messaging.sender.Notifier;
import org.globus.crux.messaging.sender.NotifierFactory;
import org.globus.crux.messaging.subscription.Subscription;
import org.globus.crux.messaging.subscription.SubscriptionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

import javax.jms.ConnectionFactory;
import javax.jms.TextMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class acts as a listener
 *
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public class ResourcefulTopicListener implements TopicListener {
    private DefaultMessageListenerContainer container;
    private NotifierFactory notifierFactory;
    private boolean running;
    private Map<String, Map<String, Notifier>> workers = new LinkedHashMap<String, Map<String, Notifier>>();
    private Logger logger = LoggerFactory.getLogger(getClass());
    private SubscriptionManager subManager;
    private String topic;


    public ResourcefulTopicListener(String topic, ConnectionFactory connFac) {
        this.topic = topic;
        MessageListenerAdapter adapter = new MessageListenerAdapter(this);
        adapter.setMessageConverter(null);
        container = new DefaultMessageListenerContainer();
        container.setConnectionFactory(connFac);
        container.setDestinationName(topic);
        container.setMessageListener(adapter);
        container.setPubSubDomain(true);
        container.initialize();
    }

    @SuppressWarnings("unchecked")
    public void handleMessage(TextMessage message) throws MessagingException {
        try {
            String id = message.getJMSCorrelationID();
            Source source = new StreamSource(new StringReader(message.getText()));
            if (id != null) {
                for (Notifier notifier : workers.get(id).values()) {
                    notifier.sendNotification(source);
                }
            }
        } catch (Exception e) {
            throw new MessagingException(e);
        }
    }

    public void addSubscriber(Subscription worker) {
        logger.debug("Adding notifier {}", worker.getId()); /* NON-NLS */
        Map<String, Notifier> notifiers = workers.get(worker.getProducerId());
        if(notifiers == null){
            notifiers = new LinkedHashMap<String, Notifier>();
            workers.put(worker.getProducerId(), notifiers);
        }
        notifiers.put(worker.getId(), notifierFactory.createNotificationSender(worker));
    }

    public void removeSubscriber(String subId) {
        logger.debug("Removing notifier {}", subId); /* NON-NLS */
        Subscription subscription = subManager.getSubscription(subId);
        Map<String, Notifier> notifiers = workers.get(subscription.getProducerId());
        notifiers.remove(subscription.getId());
    }

    public NotifierFactory getNotifierSenderFactory() {
        return notifierFactory;
    }

    public void setNotifierSenderFactory(NotifierFactory notifierFactory) {
        this.notifierFactory = notifierFactory;
    }

    public boolean isRunning() {
        return running;
    }

    public void startup() {
        container.start();
        running = true;
    }

    public void shutdown() {
        container.shutdown();
        running = false;
    }

    public SubscriptionManager getSubManager() {
        return subManager;
    }

    public void setSubManager(SubscriptionManager subManager) {
        this.subManager = subManager;
    }

    public String getTopic() {
        return topic;
    }
}
