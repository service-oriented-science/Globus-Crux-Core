package org.globus.crux.stateful;

/**
 * @author turtlebender
 */
public class StatefulServiceException extends Exception {
    public StatefulServiceException() {
    }

    public StatefulServiceException(String s) {
        super(s);
    }

    public StatefulServiceException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public StatefulServiceException(Throwable throwable) {
        super(throwable);
    }
}
