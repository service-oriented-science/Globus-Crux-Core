package org.globus.crux.messaging.topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerRegistry;
import org.apache.activemq.broker.BrokerService;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.globus.crux.messaging.topic.impl.DefaultTopicListenerManager;

import javax.jms.ConnectionFactory;
import javax.xml.namespace.QName;
/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
@Test(groups = "messaging")
public class DefaultTopicListenerManagerTest {
    DefaultTopicListenerManager manager = new DefaultTopicListenerManager();
    static final QName topicName = new QName("http://www.test.com", "testTopic");

    @BeforeTest
    public void setup() {
        ConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        manager.setConnectionFactory(connectionFactory);
    }

    @Test
    public void testStartTopicListener() throws Exception {
        TopicListener listener = mock(TopicListener.class);
        Topic topic = new Topic();
        topic.setQname(topicName);
        when(listener.getTopic()).thenReturn(topic.getQnameString());
        manager.startTopicListener(listener);
        BrokerService broker = BrokerRegistry.getInstance().lookup("localhost");
        assertTrue(broker != null);
        broker.stop();
    }

    @Test(dependsOnMethods = "testStartTopicListener")
    public void testStopTopicListener() {
        // Add your code here
    }
}
