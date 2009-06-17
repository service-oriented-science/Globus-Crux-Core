package org.globus.crux.stateful;

/**
 * This is a basic interface for acquiring state for a particular service call.
 *
 * @param <T> The type of the state.
 */
public interface StateInfo<T> {
    /**
     * Get the state associated with the current method call.
     *
     * @return Current state
     * @throws StatefulServiceException If the state cannot be retrieved.
     */
    T getState() throws StatefulServiceException;
}
