package org.globus.crux.stateful.resource;

import org.globus.crux.stateful.StatefulServiceException;


/**
 * Exception for error working with Resources defined as tuples (key->value)
 *
 * @author Tom Howe
 * @since 1.0
 * @version 1.0
 */
public class ResourceException extends StatefulServiceException{
    public ResourceException() {
    }

    public ResourceException(String s) {
        super(s);
    }

    public ResourceException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ResourceException(Throwable throwable) {
        super(throwable);
    }
}
