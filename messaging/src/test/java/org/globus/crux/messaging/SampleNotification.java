package org.globus.crux.messaging;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
@XmlRootElement(namespace = "http://www.foo.com", name = "bar")
public class SampleNotification {
    private String message;

    public String getMessage() {
        return message;
    }

    @XmlAttribute
    public void setMessage(String message) {
        this.message = message;
    }
}
