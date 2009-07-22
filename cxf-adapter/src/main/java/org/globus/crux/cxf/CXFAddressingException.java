package org.globus.crux.cxf;

import org.globus.crux.stateful.StatefulServiceException;

/**
 * @author turtlebender
 */
public class CXFAddressingException extends StatefulServiceException {
    public CXFAddressingException() {
    }

    public CXFAddressingException(String s) {
        super(s);
    }

    public CXFAddressingException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public CXFAddressingException(Throwable throwable) {
        super(throwable);
    }
}
