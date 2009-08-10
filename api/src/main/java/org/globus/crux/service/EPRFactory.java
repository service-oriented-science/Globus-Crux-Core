package org.globus.crux.service;

import javax.xml.ws.wsaddressing.W3CEndpointReference;

/**
 * @author turtlebender
 */
public interface EPRFactory {
    W3CEndpointReference createEPRWithId(Object id) throws EPRFactoryException;
}
