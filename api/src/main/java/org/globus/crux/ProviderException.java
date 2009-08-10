package org.globus.crux;

/**
 * @author turtlebender
 */
public class ProviderException extends Exception{
    public ProviderException() {
    }

    public ProviderException(String s) {
        super(s);
    }

    public ProviderException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ProviderException(Throwable throwable) {
        super(throwable);
    }
}
