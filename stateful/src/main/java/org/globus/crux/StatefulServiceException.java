package org.globus.crux;


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