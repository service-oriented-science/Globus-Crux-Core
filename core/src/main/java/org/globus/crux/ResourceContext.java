package org.globus.crux;

import org.globus.crux.service.ResourceStoreException;

/**
 * @author turtlebender
 */
public interface ResourceContext<V, T> {
    public T getCurrentResource() throws ResourceStoreException;

    public void storeResource(V key, T value) throws ResourceStoreException;
}
