package org.globus.crux.messaging.sender.impl;

import org.globus.crux.messaging.subscription.Subscription;
import org.globus.crux.messaging.sender.NotifierFactory;
import org.globus.crux.messaging.sender.Notifier;

import javax.jms.ConnectionFactory;
import javax.xml.bind.JAXBContext;

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

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
}
