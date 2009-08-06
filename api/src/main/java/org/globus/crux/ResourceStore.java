package org.globus.crux;

import org.globus.crux.service.ResourceStoreException;

/**
 * @author turtlebender
 */
public interface ResourceStore<T, V> {
    void storeResource(T key, V resource) throws ResourceStoreException;

    V findResourceByKey(T key) throws ResourceStoreException;
}
