package org.globus.crux.messaging.subscription.impl;

import org.globus.crux.messaging.MessagingException;
import org.globus.crux.messaging.subscription.Subscription;
import org.globus.crux.messaging.sender.NotifierFactory;
import org.globus.crux.messaging.topic.Topic;
import org.globus.crux.messaging.topic.TopicManager;
import static org.mockito.Mockito.*;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.jms.ConnectionFactory;
import javax.xml.namespace.QName;
import java.util.Calendar;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
@Test(groups = "messaging")
public class InMemorySubscriptionManagerTest {
    private Topic basicTopic;
    private QName topicName;
    private InMemorySubscriptionManager manager;
    private TopicManager topicManager;
    private Scheduler scheduler;
    private ConnectionFactory connectionFactory;
    private NotifierFactory notifierFactory;
    private Subscription subscript;
    private static final String SAMPLE_PRODUCER_ID = "SampleProducerId";
    private static final String SUBSCRIPTION_ID = "RandomSubscriptionId";

    @BeforeMethod
    @SuppressWarnings("unchecked")
    public void setUp() throws MessagingException, SchedulerException {
        manager = new InMemorySubscriptionManager();
        topicManager = mock(TopicManager.class);
        scheduler = mock(Scheduler.class);
        connectionFactory = mock(ConnectionFactory.class);
        notifierFactory = mock(NotifierFactory.class);
        manager.setScheduler(scheduler);
        manager.setTopicManager(topicManager);
        manager.setConnFact(connectionFactory);
        manager.setSenderFact(notifierFactory);
        topicName = new QName("http://www.test.com", "testTopic");
        basicTopic = new Topic();
        basicTopic.setQname(topicName);
        basicTopic.setTopicId("RandomTopicId");
        when(topicManager.getTopic(topicName)).thenReturn(basicTopic);
        createSubscription();
    }

    private void createSubscription() throws MessagingException, SchedulerException {
        subscript = new Subscription();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10);
        subscript.setTerminationTime(calendar);
        subscript.setProducerId(SAMPLE_PRODUCER_ID);
        subscript.setId(SUBSCRIPTION_ID);
        subscript.setTopic(basicTopic);
        manager.registerConsumer(subscript);
    }


    private Subscription createBadSubscription() throws SchedulerException {
        Subscription subscription = new Subscription();
        Topic badTopic = new Topic();
        badTopic.setTopicId("BadTopicId");  /*NON-NLS*/
        badTopic.setQname(new QName("http://bad.com", "fake"));  /*NON-NLS*/
        subscription.setTopic(badTopic);
        subscription.setId("BadSubscriptionId");  /*NON-NLS*/
        subscription.setTerminationTime(Calendar.getInstance());
        return subscription;
    }

    @Test(expectedExceptions = MessagingException.class)
    public void testBadSubscription() throws Exception{
        manager.registerConsumer(createBadSubscription());
    }

    @Test
    public void testInitTopic() throws SchedulerException {
        QName testTopic = new QName("http://www.test.com", "testTopic");  /*NON-NLS*/
        manager.initTopic(testTopic);
    }

    @Test
    public Subscription testRegisterConsumer() throws MessagingException, SchedulerException {
        return subscript;
    }

    @Test
    public void testUnsubscribe1() throws MessagingException, SchedulerException {
        manager.unsubscribe(subscript);
        verify(scheduler).deleteJob(SUBSCRIPTION_ID, InMemorySubscriptionManager.JOB_GROUP_NAME);
        verify(notifierFactory).createNotificationSender(subscript);
        verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
        verify(topicManager).getTopic(topicName);
    }

    @Test
    public void testUnsubscribe2() throws MessagingException, SchedulerException {
        manager.unsubscribe(basicTopic.getQname(), SUBSCRIPTION_ID);
        verify(scheduler).deleteJob(SUBSCRIPTION_ID, InMemorySubscriptionManager.JOB_GROUP_NAME);
        verify(notifierFactory).createNotificationSender(subscript);
        verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
        verify(topicManager).getTopic(topicName);
    }

    @Test
    public void testRenew() throws SchedulerException {
        manager.renew(subscript);
        verify(notifierFactory).createNotificationSender(subscript);
        verify(scheduler, times(2)).scheduleJob(any(JobDetail.class), any(Trigger.class));
        verify(topicManager).getTopic(topicName);
    }

    @Test
    public void testGetSubscription() {
        // Add your code here
    }
}
