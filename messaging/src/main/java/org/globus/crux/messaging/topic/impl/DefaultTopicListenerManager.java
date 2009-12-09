package org.globus.crux.messaging.topic.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.globus.crux.messaging.topic.TopicListenerManager;
import org.globus.crux.messaging.topic.TopicListener;

import javax.jms.ConnectionFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public class DefaultTopicListenerManager implements TopicListenerManager {
    private ConnectionFactory connectionFactory;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Map<TopicListener, DefaultMessageListenerContainer> containerMap =
            new HashMap<TopicListener, DefaultMessageListenerContainer>();

    public void startTopicListener(TopicListener listener) {
        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setMessageListener(new MessageListenerAdapter(listener));
        container.setPubSubDomain(true);
        container.setDestinationName(listener.getTopic());
        container.start();
        containerMap.put(listener, container);
        logger.info("Topic listener started on topic named: {}", listener.getTopic());   /* NON-NLS */
    }

    public void stopTopicListener(TopicListener listener) {
        containerMap.get(listener).stop();
        containerMap.remove(listener);
        logger.info("Topic listener stoped on topic named: {}", listener.getTopic());    /* NON-NLS */
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
}
