package org.globus.crux.messaging.sender.impl;

import javax.jms.ConnectionFactory;
import javax.xml.namespace.QName;

import org.globus.crux.messaging.sender.Notifier;
import org.globus.crux.messaging.sender.NotifierFactory;
import org.globus.crux.messaging.subscription.Subscription;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public class JMSNotifierFactory implements NotifierFactory {
    private ConnectionFactory connectionFactory;

    public Notifier createNotificationSender(Subscription subscript) {
        return new JMSNotifier(subscript.getTopic().getQname(), subscript.getProducerId(),
                connectionFactory);
    }
    
    public Notifier createNotificationSender(QName topicName, String resourceKeyId) {
        return new JMSNotifier(topicName, resourceKeyId, connectionFactory);
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
}
