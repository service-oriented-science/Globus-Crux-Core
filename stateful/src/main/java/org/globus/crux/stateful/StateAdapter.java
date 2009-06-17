package org.globus.crux.stateful;


/**
 * Implementors of this interface need to provide the current state.
 *
 * @param <T> The type of the state.
 *
 * @author Tom Howe
 * @since 1.0
 * @version 1.0
 */
public interface StateAdapter<T> {
    T getState(); 
}
