package org.globus.crux.stateful;


public class StatefulServiceException extends Exception {

    public StatefulServiceException() {
        super();
    }

    public StatefulServiceException(String message) {
        super(message);
    }

    public StatefulServiceException(Throwable t) {
        super(t);
    }

}