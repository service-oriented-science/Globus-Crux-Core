/*
 * Copyright 2002-2008 Xebia and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.globus.crux.messaging.utils;

import org.springframework.core.style.ToStringCreator;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

/**
 * <p>
 * JAXB2 based <code>MessageConverter</code>.
 * </p>
 *
 * <p>
 * Marshalling : converts the given object into a {@link TextMessage} thanks to {@link Marshaller#marshal(Object, java.io.OutputStream)} .
 * </p>
 * <p>
 * Unmarshalling : converts the given {@link TextMessage} or {@link BytesMessage} body into an object thanks to
 * {@link Unmarshaller#unmarshal(javax.xml.transform.Source)}.
 * </p>
 *
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class JAXBMessageConverter implements MessageConverter{

    protected JAXBContext context;
    private String resourceId;

    public JAXBMessageConverter(){

    }

    public JAXBMessageConverter(JAXBContext context){
        this.context = context;
    }

    /**
     * Keep a reference on <code>Jaxb2Marshaller.marshallerProperties</code> to be able to retreive the {@link Marshaller#JAXB_ENCODING}
     * if it has been set.
     */
    protected Map<String, ?> marshallerProperties;

    /**
     * <p>
     * Unmarshal given <code>message</code> into an <code>object</code>.
     * </p>
     *
     * <p>
     * Should we raise an exception if the XML message encoding is not in sync with the underlying TextMessage encoding when the JMS
     * Provider supports MOM message's encoding ?
     * </p>
     *
     * @param message
     *            message to unmarshal, MUST be an instance of {@link TextMessage} or of {@link BytesMessage}.
     * @see org.springframework.jms.support.converter.MessageConverter#fromMessage(javax.jms.Message)
     */
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {

        Source source;
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            source = new StreamSource(new StringReader(textMessage.getText()));
        } else if (message instanceof BytesMessage) {
            BytesMessage bytesMessage = (BytesMessage) message;
            byte[] bytes = new byte[(int) bytesMessage.getBodyLength()];
            bytesMessage.readBytes(bytes);
            source = new StreamSource(new ByteArrayInputStream(bytes));

        } else {
            throw new MessageConversionException("Unsupported JMS Message type " + message.getClass()
                    + ", expected instance of TextMessage or BytesMessage for " + message);
        }
        Object result;
        try {
            result = context.createUnmarshaller().unmarshal(source);
        } catch (JAXBException e) {
            throw new MessageConversionException("Error unmarshalling message", e);
        }

        return result;
    }

    /**
     * <p>
     * Intended to be overwritten for each JMS Provider implementation (Websphere MQ, Tibco EMS, Active MQ ...).
     * </p>
     * <p>
     * If JMS provider supports messages encoding, this charset must be in sync with the encoding used to generate the XML text output
     * </p>
     */
    protected void postProcessResponseMessage(Message textMessage) throws JMSException {
        textMessage.setJMSCorrelationID(this.resourceId);
    }


    /**
     * <p>
     * Marshal the given <code>object</code> into a text message.
     * </p>
     *
     * <p>
     * This method ensures that the message encoding supported by the underlying JMS provider is in sync with the encoding used to generate
     * the XML message.
     * </p>
     *
     * @param object
     * @see org.springframework.jms.support.converter.MessageConverter#toMessage(java.lang.Object, javax.jms.Session)
     */
    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
        StringWriter out = new StringWriter();
        try {
            context.createMarshaller().marshal(object, new StreamResult(out));
        } catch (JAXBException e) {
            throw new MessageConversionException("Error marshalling object to Text", e);
        }

        // create TextMessage result
        String text = out.toString();
        TextMessage textMessage = session.createTextMessage(text);

        postProcessResponseMessage(textMessage);

        return textMessage;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("context", this.context).toString();
    }

    public JAXBContext getContext() {
        return context;
    }

    public void setContext(JAXBContext context) {
        this.context = context;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
