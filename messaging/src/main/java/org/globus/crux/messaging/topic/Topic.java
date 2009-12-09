package org.globus.crux.messaging.topic;

import org.globus.crux.messaging.utils.EndpointReference;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Basic;
import javax.persistence.Transient;
import javax.xml.namespace.QName;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
@Entity
public class Topic {
    @Basic
    private String qnameString;
    @Transient
    private QName qname;
    @Basic
    private EndpointReference producerReference;
    @Basic
    private String producerId;
    @Id
    private String topicId;
    @Basic
    private String currentMessage;


    public EndpointReference getProducerReference() {
        return producerReference;
    }

    public void setProducerReference(EndpointReference producerId) {
        this.producerReference = producerId;
    }

    public String getProducerId() {
        return producerId;
    }

    public void setProducerId(String producerId) {
        this.producerId = producerId;
    }

    public String getCurrentMessage() {
        return currentMessage;
    }

    public void setCurrentMessage(String currentMessage) {
        this.currentMessage = currentMessage;
    }

    public QName getQname() {
        return qname;
    }

    public void setQname(QName qname) {
        this.qname = qname;
        this.qnameString = qname.toString();
    }

    public String getQnameString() {
        return qnameString;
    }

    public void setQnameString(String qnameString) {
        this.qnameString = qnameString;
        this.qname = QName.valueOf(qnameString);
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
}
