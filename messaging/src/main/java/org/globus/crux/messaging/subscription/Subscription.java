package org.globus.crux.messaging.subscription;

import org.globus.crux.messaging.topic.Topic;
import org.globus.crux.messaging.utils.EndpointReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.util.Calendar;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
@Entity
public class Subscription {
    @Id
    private String id;
    @Basic
    private EndpointReference consumerReference;
    @Basic
    private EndpointReference producerReference;
    @Basic
    private String producerId;
    @Temporal(TemporalType.TIME)
    private Calendar terminationTime;
    @Temporal(TemporalType.TIME)
    private Calendar creationTime;
    @Basic
    private Topic topic;
    @Basic
    private boolean useRaw;
    @Transient
    private Logger logger = LoggerFactory.getLogger(getClass());


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isUseRaw() {
        return useRaw;
    }

    public void setUseRaw(boolean useRaw) {
        this.useRaw = useRaw;
    }

    public EndpointReference getConsumerReference() {
        return consumerReference;
    }

    public void setConsumerReference(EndpointReference consumerReference) {
        this.consumerReference = consumerReference;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public EndpointReference getProducerReference() {
        return producerReference;
    }

    public void setProducerReference(EndpointReference producerReference) {
        this.producerReference = producerReference;
    }

    public Calendar getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Calendar creationTime) {
        this.creationTime = creationTime;
    }

    public Calendar getTerminationTime() {
        return terminationTime;
    }

    public void setTerminationTime(Calendar terminationTime) {
        this.terminationTime = terminationTime;
    }

    public String getProducerId() {
        return producerId;
    }

    public void setProducerId(String producerId) {
        this.producerId = producerId;
    }


}
