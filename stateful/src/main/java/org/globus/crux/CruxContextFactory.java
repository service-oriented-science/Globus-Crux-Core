package org.globus.crux;

import org.globus.crux.stateful.CruxContext;

/**
 * @author turtlebender
 */
public interface CruxContextFactory<T, V extends CruxContext> {
    V createContext(T context);
}
