package org.globus.crux.service;

/**
 * @author turtlebender
 */
public class ResourceStoreException extends Exception{
    public ResourceStoreException() {
    }

    public ResourceStoreException(String s) {
        super(s);
    }

    public ResourceStoreException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ResourceStoreException(Throwable throwable) {
        super(throwable);
    }
}
