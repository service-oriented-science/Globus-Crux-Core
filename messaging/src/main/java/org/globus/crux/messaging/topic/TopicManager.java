package org.globus.crux.messaging.topic;

import javax.xml.namespace.QName;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public interface TopicManager {
    Topic getTopic(QName qname);

    Topic getTopic(String producerId, QName qname);

    void addTopic(Topic topic);

    void removeTopic(Topic topic);
}
