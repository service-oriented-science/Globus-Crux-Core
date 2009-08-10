package org.globus.crux.service;

/**
 * @author turtlebender
 */
public class EPRFactoryException extends Exception{
    public EPRFactoryException() {
    }

    public EPRFactoryException(String s) {
        super(s);
    }

    public EPRFactoryException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public EPRFactoryException(Throwable throwable) {
        super(throwable);
    }
}
