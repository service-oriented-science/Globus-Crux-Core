package org.globus.crux.messaging.wsn;

import javax.xml.namespace.QName;

import org.globus.crux.messaging.sender.NotifierFactory;
import org.globus.crux.messaging.sender.Notifier;
import org.globus.crux.messaging.subscription.Subscription;
import org.globus.crux.messaging.wsn.WSNotificationSender;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public class WSNNotificationFactory implements NotifierFactory {
    public Notifier createNotificationSender(QName topicName, String resourceKeyId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Notifier createNotificationSender(Subscription subscript) {
        return new WSNotificationSender(subscript);
    }
}
