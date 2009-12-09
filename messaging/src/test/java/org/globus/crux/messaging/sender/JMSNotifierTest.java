package org.globus.crux.messaging.sender;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerRegistry;
import org.globus.crux.messaging.SampleNotification;
import org.globus.crux.messaging.sender.impl.JMSNotifier;

import javax.xml.namespace.QName;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.stream.StreamSource;
import javax.jms.ConnectionFactory;
import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.io.StringReader;

import static org.testng.Assert.*;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
@Test(groups = "messaging")
public class JMSNotifierTest {
    private QName topicQName;
    private JMSNotifier notifier;
    private ConnectionFactory connectionFactory;
    private JAXBContext jaxbContext;

    @BeforeTest
    public void setup() throws JAXBException {
        topicQName = new QName("http://www.test.com", "testTopic");
        connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        jaxbContext = JAXBContext.newInstance(SampleNotification.class);
        notifier =
                new JMSNotifier(topicQName, connectionFactory);
    }

    @Test
    public void testSendNotification() throws InterruptedException, JAXBException {
        final String sampleMessage = "Sample Message";
        final CountDownLatch latch = new CountDownLatch(1);
        Callable<Integer> listener = new Callable<Integer>() {
            public Integer call() throws Exception {
                Connection connection = connectionFactory.createConnection();
                connection.start();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = session.createTopic(topicQName.toString());
                MessageConsumer consumer = session.createConsumer(destination);
                Message message = consumer.receive(1000);
                if (message instanceof TextMessage) {
                    TextMessage tm = (TextMessage) message;
                    Object object = jaxbContext.createUnmarshaller().unmarshal(
                            new StreamSource(new StringReader(tm.getText())));
                    assertTrue(object instanceof SampleNotification);
                    SampleNotification not = (SampleNotification) object;
                    assertEquals(sampleMessage, not.getMessage());
                    latch.countDown();
                    return 0;
                }
                connection.close();
                assertNull(BrokerRegistry.getInstance().lookup("localhost"));
                latch.countDown();
                return -1;
            }
        };
        ExecutorService executor =Executors.newFixedThreadPool(1);
        executor.submit(listener);
        SampleNotification sampleNotification = new SampleNotification();
        sampleNotification.setMessage(sampleMessage);
        JAXBSource source = new JAXBSource(jaxbContext, sampleNotification);
        notifier.sendNotification(source);
        latch.await();
        executor.shutdown();
    }
}
