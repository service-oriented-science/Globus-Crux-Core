package org.globus.crux.messaging.topic;

import org.globus.crux.messaging.SampleNotification;
import org.globus.crux.messaging.topic.impl.BasicTopicListener;
import org.globus.crux.messaging.sender.Notifier;
import org.globus.crux.messaging.sender.NotifierFactory;
import org.globus.crux.messaging.subscription.Subscription;
import static org.mockito.Mockito.*;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
@Test(groups = "messaging")
public class BasicTopicListenerTest {
    BasicTopicListener listener;
    NotifierFactory fac;
    Notifier notifier;
    SampleNotification notification;
    JAXBContext jaxb;

    @BeforeTest
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        fac = mock(NotifierFactory.class);
        notifier = mock(Notifier.class);
        when(fac.createNotificationSender(any(Subscription.class))).thenReturn(notifier);
        jaxb = JAXBContext.newInstance(SampleNotification.class);
        listener = new BasicTopicListener("sampleTopic");  /* NON-NLS */
        listener.setNotifierFactory(fac);
    }

    @Test(dependsOnMethods = "testAddSubscriber")

    public void testHandleMessage() throws Exception {
        TextMessage message = mock(TextMessage.class);
        notification = new SampleNotification();
        StringWriter writer = new StringWriter();
        jaxb.createMarshaller().marshal(notification, new StreamResult(writer));
        String messageString = writer.toString();
        when(message.getText()).thenReturn(messageString);
        listener.handleMessage(message);
        verify(notifier).sendNotification(any(Source.class));
    }

    @Test
    public void testAddSubscriber() {
        Subscription subscription = new Subscription();
        subscription.setId("testSubscription"); /*NON-NLS*/
        listener.addSubscriber(subscription);
        verify(fac).createNotificationSender(subscription);
    }

    @Test(dependsOnMethods = "testAddSubscriber")
    public void testRemoveSubscriber() {
        listener.removeSubscriber("testSubscription"); /*NON-NLS*/
    }
}
