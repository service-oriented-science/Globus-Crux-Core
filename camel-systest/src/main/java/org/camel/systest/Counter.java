package org.camel.systest;

/**
 * @author turtlebender
 */
public class Counter {
    int value = 0;

    @ResourceProperty(namespace = "http://www.counter.com", localpart = "value")
    public int getValue() {
        return value;
    }

}
