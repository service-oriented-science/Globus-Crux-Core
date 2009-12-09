package org.globus.crux.messaging.topic;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public interface TopicListenerManager {
    void startTopicListener(TopicListener listener);

    void stopTopicListener(TopicListener listener);
}
