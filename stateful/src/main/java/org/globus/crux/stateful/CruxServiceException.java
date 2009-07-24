package org.globus.crux.stateful;

/**
 * @author turtlebender
 */
public class CruxServiceException extends Exception{
    public CruxServiceException() {
    }

    public CruxServiceException(String s) {
        super(s);
    }

    public CruxServiceException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public CruxServiceException(Throwable throwable) {
        super(throwable);
    }
}
