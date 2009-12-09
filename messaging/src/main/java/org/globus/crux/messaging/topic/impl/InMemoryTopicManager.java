package org.globus.crux.messaging.topic.impl;

import org.globus.crux.messaging.sender.NotifierFactory;
import org.globus.crux.messaging.topic.TopicManager;
import org.globus.crux.messaging.topic.TopicListenerManager;
import org.globus.crux.messaging.topic.Topic;
import org.globus.crux.messaging.topic.TopicListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public class InMemoryTopicManager implements TopicManager {
    private Map<TopicKey, TopicListenerKey> topicMap = new HashMap<TopicKey, TopicListenerKey>();
    private NotifierFactory factory;
    private TopicListenerManager listenerManager;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public InMemoryTopicManager(NotifierFactory factory) {
        this.factory = factory;
    }

    public InMemoryTopicManager() {
    }

    public Topic getTopic(QName qname) {
        TopicKey key = new TopicKey();
        key.setQname(qname);
        Topic value = topicMap.get(key).getTopic();
        return value;
    }

    public Topic getTopic(String producerId, QName qname) {
        TopicKey key = new TopicKey();
        key.setQname(qname);
        key.setProducerId(producerId);
        return topicMap.get(key).getTopic();
    }

    public void addTopic(Topic topic) {
        TopicKey key = new TopicKey();
        key.setProducerId(topic.getProducerId());
        key.setQname(QName.valueOf(topic.getQnameString()));
        BasicTopicListener listener = new BasicTopicListener(topic.getQnameString());
        listener.setNotifierFactory(factory);
        this.listenerManager.startTopicListener(listener);
        topicMap.put(key, new TopicListenerKey(topic, listener));
        logger.debug("Registering Topic Listener: {}", topic.toString()); /*NON-NLS*/
    }

    public void removeTopic(Topic topic) {
        TopicKey key = new TopicKey();
        key.setProducerId(topic.getProducerId());
        key.setQname(QName.valueOf(topic.getQnameString()));
        TopicListener listener = topicMap.remove(key).getTopicListener();
        this.listenerManager.stopTopicListener(listener);
        logger.debug("Unregistering TopicListener: {}", topic.toString()); /*NON-NLS*/
    }

    public TopicListenerManager getListenerManager() {
        return listenerManager;
    }

    public void setListenerManager(TopicListenerManager listenerManager) {
        this.listenerManager = listenerManager;
    }

    public NotifierFactory getSenderFactory() {
        return factory;
    }

    public void setSenderFactory(NotifierFactory factory) {
        this.factory = factory;
    }

    static class TopicKey {
        private QName qname;
        private String producerId;


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TopicKey)) return false;

            TopicKey topicKey = (TopicKey) o;

            if (producerId != null ? !producerId.equals(topicKey.producerId) : topicKey.producerId != null)
                return false;
            if (qname != null ? !qname.equals(topicKey.qname) : topicKey.qname != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = qname != null ? qname.hashCode() : 0;
            result = 31 * result + (producerId != null ? producerId.hashCode() : 0);
            return result;
        }

        public QName getQname() {
            return qname;
        }

        public void setQname(QName qname) {
            this.qname = qname;
        }

        public String getProducerId() {
            return producerId;
        }

        public void setProducerId(String producerId) {
            this.producerId = producerId;
        }
    }

    class TopicListenerKey {
        private Topic topic;
        private TopicListener topicListener;

        TopicListenerKey(Topic topic, TopicListener topicListener) {
            this.topic = topic;
            this.topicListener = topicListener;
        }

        public Topic getTopic() {
            return topic;
        }

        public void setTopic(Topic topic) {
            this.topic = topic;
        }

        public TopicListener getTopicListener() {
            return topicListener;
        }

        public void setTopicListener(TopicListener topicListener) {
            this.topicListener = topicListener;
        }
    }
}
