package org.globus.crux.messaging.sender.impl;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.globus.crux.messaging.sender.Notifier;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

/**
 * Notifier implementation that uses JMS to send messages.  This implementation also Marshals Objects
 * to XML using JAXB.
 *
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public class JMSNotifier implements Notifier {

    private JmsTemplate template;
    private String resourceId;

    /**
     * Create a new JMSNotifier.
     *
     * @param topicName The topic to send messages to.
     * @param connFac   The JMS ConnectionFactory.
     */
    public JMSNotifier(QName topicName, ConnectionFactory connFac) {
        this(topicName, null, connFac);
    }

    /**
     * Create a new JMSNotifier tied to a specific resourceKey.
     *
     * @param topicName     The topic to send messages to.
     * @param resourceKeyId The resource key associated with this notifier.
     * @param connFac       The JMS ConnectionFactory.
     */
    public JMSNotifier(QName topicName, String resourceKeyId, ConnectionFactory connFac) {
        this.resourceId = resourceKeyId;
        template = new JmsTemplate(connFac);
        template.setPubSubDomain(true);
        template.setDefaultDestinationName(topicName.toString());
    }

    /**
     * Fire a notification via JMS.
     *
     * @param notification The object to send.
     */
    public void sendNotification(final Source notification) {
        template.send(new MessageCreator() {
            TransformerFactory fac = TransformerFactory.newInstance();

            public Message createMessage(Session session) throws JMSException {
                Transformer transformer;
                try {
                    transformer = fac.newTransformer();
                    StringWriter writer = new StringWriter();
                    StreamResult result = new StreamResult(writer);
                    transformer.transform(notification, result);
                    TextMessage message = session.createTextMessage(writer.toString());
                    message.setJMSCorrelationID(resourceId);
                    return message;
                } catch (TransformerConfigurationException e) {
                    throw new JMSException(e.getMessageAndLocation());
                } catch (TransformerException e) {
                    throw new JMSException(e.getMessageAndLocation());
                }
            }
        });
    }
}
