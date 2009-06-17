package org.globus.crux.stateful.resource;

import org.globus.crux.stateful.StatefulServiceException;


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
