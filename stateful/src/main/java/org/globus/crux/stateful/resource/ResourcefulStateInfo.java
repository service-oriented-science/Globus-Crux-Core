package org.globus.crux.stateful.resource;

import org.globus.crux.stateful.StateInfo;
import org.globus.crux.stateful.StatefulServiceException;

/**
 * This interface extends the basic StateInfo interface by supporting both a resourceId as well
 * as a stateful resource.  This is generally used in conjuction with a ResourceManager.
 *
 * @param <T> The type of the key
 * @param <V> The type of the value
 *
 * @author Tom Howe
 * @since 1.0
 * @version 1.0
 */
public interface ResourcefulStateInfo<T, V> extends StateInfo<V> {
    /**
     * Get the key associated with this request.
     *
     * @return The associated Key
     * @throws StatefulServiceException
     */
    T getResourceId() throws ResourceException;
}
