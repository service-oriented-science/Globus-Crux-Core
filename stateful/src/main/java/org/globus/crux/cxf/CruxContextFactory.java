package org.globus.crux.cxf;

import org.globus.crux.stateful.CruxContext;

import javax.xml.ws.WebServiceContext;

/**
 * @author turtlebender
 */
public interface CruxContextFactory<T, V extends CruxContext> {
    V createContext(T context);
}
