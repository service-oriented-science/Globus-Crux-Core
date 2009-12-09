package org.globus.crux.messaging;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public class MessagingException extends Exception{
    public MessagingException() {
    }

    public MessagingException(String s) {
        super(s);
    }

    public MessagingException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public MessagingException(Throwable throwable) {
        super(throwable);
    }
}
