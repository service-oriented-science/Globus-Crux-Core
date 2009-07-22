package org.camel.systest;

/**
 * @author turtlebender
 */
public class DummyServiceGenerationException extends Exception{
    public DummyServiceGenerationException() {
    }

    public DummyServiceGenerationException(String s) {
        super(s);
    }

    public DummyServiceGenerationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public DummyServiceGenerationException(Throwable throwable) {
        super(throwable);
    }
}
