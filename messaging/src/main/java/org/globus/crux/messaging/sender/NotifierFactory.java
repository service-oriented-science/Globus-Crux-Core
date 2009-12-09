package org.globus.crux.messaging.sender;

import org.globus.crux.messaging.subscription.Subscription;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public interface NotifierFactory {

    Notifier createNotificationSender(Subscription subscript);
}
