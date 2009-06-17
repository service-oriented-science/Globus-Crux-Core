package org.globus.crux.stateful.resource;

import org.globus.crux.stateful.StatefulServiceException;

public class ResourceLookupException extends StatefulServiceException{

    public ResourceLookupException() {
    }

    public ResourceLookupException(String message) {
        super(message);
    }

    public ResourceLookupException(Throwable t) {
        super(t);
    }

    public ResourceLookupException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
