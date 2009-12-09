package org.globus.crux.messaging.topic;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;

import javax.xml.namespace.QName;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;
import org.globus.crux.messaging.topic.impl.InMemoryTopicManager;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
@Test(groups = "messaging")
public class InMemoryTopicManagerTest {
    InMemoryTopicManager topicManager;
    TopicListenerManager listenerManager;
    Topic basicTopic;
    Topic resourcefulTopic;

    @BeforeTest
    public void setup(){
        topicManager = new InMemoryTopicManager();
        listenerManager = mock(TopicListenerManager.class);
        topicManager.setListenerManager(listenerManager);
        resourcefulTopic= new Topic();
        resourcefulTopic.setProducerId("ResourceId");
        resourcefulTopic.setQname(new QName("http://www.test.com","resourcefulTopic"));
        topicManager.addTopic(resourcefulTopic);
        basicTopic = new Topic();
        basicTopic.setQname(new QName("http://www.test.com","basicTopic"));
        topicManager.addTopic(basicTopic);
    }

    @Test(dependsOnMethods = "testAddTopic")
    public void testGetTopic1() {
        Topic topic = topicManager.getTopic(new QName("http://www.test.com","basicTopic"));
        assertEquals(basicTopic, topic);
    }

    @Test(dependsOnMethods = "testGetTopic1")
    public void testGetTopic2() {
        Topic topic = topicManager.getTopic("ResourceId", new QName("http://www.test.com", "resourcefulTopic"));
        assertEquals(resourcefulTopic, topic);
    }

    @Test
    public void testAddTopic() {
        verify(listenerManager, times(2)).startTopicListener(any(TopicListener.class));
    }

    @Test(dependsOnMethods = "testGetTopic2")
    public void testRemoveTopic() {
        topicManager.removeTopic(resourcefulTopic);
        verify(listenerManager, times(2)).startTopicListener(any(TopicListener.class));
        verify(listenerManager).stopTopicListener(any(TopicListener.class));
    }
}
