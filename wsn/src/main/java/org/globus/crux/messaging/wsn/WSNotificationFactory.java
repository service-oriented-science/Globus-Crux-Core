package org.globus.crux.messaging.wsn;

import org.globus.crux.messaging.sender.NotifierFactory;
import org.globus.crux.messaging.sender.Notifier;
import org.globus.crux.messaging.subscription.Subscription;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public class WSNotificationFactory implements NotifierFactory {
    public Notifier createNotificationSender(Subscription subscript) {
        return new WSNotificationSender(subscript);
    }
}
