package org.globus.crux.messaging.wsn;

import javax.jms.ConnectionFactory;
import javax.xml.namespace.QName;

import org.globus.crux.messaging.sender.NotifierFactory;
import org.globus.crux.messaging.sender.Notifier;
import org.globus.crux.messaging.subscription.Subscription;
import org.globus.crux.messaging.wsn.WSNNotificationSender;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public class WSNNotificationFactory implements NotifierFactory {
	private ConnectionFactory connectionFactory;
	
    public Notifier createNotificationSender(Subscription subscript) {
        return new WSNNotificationSender(subscript, connectionFactory);
    }

    public Notifier createNotificationSender(QName topicName, String resourceKeyId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
    
    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
}
