package org.globus.crux.messaging.sender;

import javax.xml.transform.Source;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public interface Notifier {
    void sendNotification(Source notification);
}
