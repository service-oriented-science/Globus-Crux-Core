package org.globus.crux.messaging.wsn;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public class UnsupportedTopicExpressionDialectException extends Exception {
    public UnsupportedTopicExpressionDialectException() {
    }

    public UnsupportedTopicExpressionDialectException(String s) {
        super(s);
    }

    public UnsupportedTopicExpressionDialectException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public UnsupportedTopicExpressionDialectException(Throwable throwable) {
        super(throwable);
    }
}
