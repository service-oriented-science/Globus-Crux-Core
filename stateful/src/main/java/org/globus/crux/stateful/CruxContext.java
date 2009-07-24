package org.globus.crux.stateful;

/**
 * @author turtlebender
 */
public interface CruxContext {
    public Object getKey() throws CruxServiceException;

    public Object get(String key) throws CruxServiceException;
}
