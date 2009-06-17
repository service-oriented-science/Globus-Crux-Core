package org.globus.crux.stateful;

/**
 * Base exception for stateful services.
 *
 * @author Tom Howe
 * @since 1.0
 * @version 1.0
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