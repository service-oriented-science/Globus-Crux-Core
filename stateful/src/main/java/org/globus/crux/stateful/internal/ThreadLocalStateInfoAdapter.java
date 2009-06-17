package org.globus.crux.stateful.internal;

import org.globus.crux.stateful.StateInfo;
import org.globus.crux.stateful.utils.AbstractThreadLocalAdapter;

/**
 * This is a simple adapter that just gets the state value associated with this call.
 *
 * @param <T> The type of object stored.
 *
 * @see ThreadLocal
 * @author Tom Howe
 * @since 1.0
 * @version 1.0
 */
public class ThreadLocalStateInfoAdapter<T> extends AbstractThreadLocalAdapter<T> implements StateInfo<T> {

    /**
     * Get the state associated with this request.
     *
     * @return The associated state. 
     */
    public T getState() {
        return this.get();        
    }
}
