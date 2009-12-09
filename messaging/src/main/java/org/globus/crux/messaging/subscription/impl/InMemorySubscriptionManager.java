package org.globus.crux.messaging.subscription.impl;

import org.globus.crux.messaging.MessagingException;
import org.globus.crux.messaging.sender.NotifierFactory;
import org.globus.crux.messaging.subscription.Subscription;
import org.globus.crux.messaging.subscription.SubscriptionManager;
import org.globus.crux.messaging.topic.TopicListener;
import org.globus.crux.messaging.topic.TopicManager;
import org.globus.crux.messaging.topic.impl.ResourcefulTopicListener;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.ConnectionFactory;
import javax.xml.namespace.QName;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public class InMemorySubscriptionManager implements SubscriptionManager {
    public static final String TOPIC_QNAME = "TOPIC_QNAME";
    private static final String SUBSCRIPTION_KEY = "SUB_KEY";
    private static ResourceBundle resourceBundle =
            ResourceBundle.getBundle("org.globus.crux.messaging.Messaging"); /*NON-NLS*/
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Scheduler scheduler;
    private ConnectionFactory connFact;
    private TopicManager topicManager;
    private NotifierFactory fact;
    private Map<QName, TopicListener> containerMap = new HashMap<QName, TopicListener>();
    private Map<String, Subscription> subscriptMap = new HashMap<String, Subscription>();
    static final String TRIGGER_GROUP_NAME = "DESTROY_SUBSCRIPTION_TRIGGER_GROUP";
    static final String JOB_GROUP_NAME = "DESTROY_SUBSCRIPTION_JOB_GROUP";

    public void initialize() throws SchedulerException {
        logger.info("Initializing Subscription Manager");
        initTopic(new QName("http://sample.com", "SampleTopic"));
    }

    public void initTopic(QName topicName) throws SchedulerException {
        ResourcefulTopicListener sub = new ResourcefulTopicListener(topicName.toString(), connFact);
        sub.setNotifierSenderFactory(fact);
        sub.setSubManager(this);
        containerMap.put(topicName, sub);
    }

    public void registerConsumer(Subscription subscription) throws SchedulerException, MessagingException {
        QName topicName = subscription.getTopic().getQname();
        if (topicManager.getTopic(topicName) == null) {
            MessagingException exception = new MessagingException(resourceBundle.getString("no.such.topic.exists"));
            logger.info(resourceBundle.getString("no.such.topic.exists"), exception);
            throw exception;
        }
        if(!containerMap.containsKey(topicName)){
            initTopic(topicName);
        }
        TopicListener jmsSubListener = containerMap.get(topicName);
        jmsSubListener.addSubscriber(subscription);
        scheduleTermination(subscription);
        this.subscriptMap.put(subscription.getId(), subscription);
    }

    public void unsubscribe(Subscription subscription) throws MessagingException {
        QName topicName = subscription.getTopic().getQname();
        String id = subscription.getId();
        unsubscribe(topicName, id);
    }

    public void unsubscribe(QName topicName, String subId) throws MessagingException {
        TopicListener jmsSubListener = containerMap.get(topicName);
        jmsSubListener.removeSubscriber(subId);
        this.subscriptMap.remove(subId);
        try {
            scheduler.deleteJob(subId,JOB_GROUP_NAME);
        } catch (SchedulerException e) {
            throw new MessagingException(e);
        }
    }

    public void renew(Subscription subscription) throws SchedulerException {
        scheduleTermination(subscription);
    }

    private void scheduleTermination(Subscription subscription) throws SchedulerException {
        Calendar terminationTime = subscription.getTerminationTime();
        QName topicName = subscription.getTopic().getQname();
        Trigger trigger = new SimpleTrigger(subscription.getId(), TRIGGER_GROUP_NAME, terminationTime.getTime());
        JobDetail jobDetail = new JobDetail(subscription.getId(), JOB_GROUP_NAME, TerminateSubscriptionJob.class);
        jobDetail.getJobDataMap().put(InMemorySubscriptionManager.SUBSCRIPTION_KEY, subscription.getId());
        jobDetail.getJobDataMap().put(TOPIC_QNAME, topicName.toString());
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public Subscription getSubscription(String subId) {
        return subscriptMap.get(subId);
    }

    class TerminateSubscriptionJob implements Job {
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            String subId = jobExecutionContext.getMergedJobDataMap().
                    getString(InMemorySubscriptionManager.SUBSCRIPTION_KEY);
            String topicString = jobExecutionContext.getMergedJobDataMap().getString(TOPIC_QNAME);
            QName topicQName = QName.valueOf(topicString);
            try {
                unsubscribe(topicQName, subId);
            } catch (MessagingException e) {
                throw new JobExecutionException(e);
            }
        }
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public ConnectionFactory getConnFact() {
        return connFact;
    }

    public void setConnFact(ConnectionFactory connFact) {
        this.connFact = connFact;
    }

    public TopicManager getTopicManager() {
        return topicManager;
    }

    public void setTopicManager(TopicManager topicManager) {
        this.topicManager = topicManager;
    }

    public NotifierFactory getSenderFact() {
        return fact;
    }

    public void setSenderFact(NotifierFactory fact) {
        this.fact = fact;
    }
}
