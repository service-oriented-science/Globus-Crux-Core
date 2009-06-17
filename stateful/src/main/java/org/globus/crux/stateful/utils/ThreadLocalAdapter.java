package org.globus.crux.stateful.utils;

/**
 * This interface mirrors the interface for ThreadLocal and is used to give ThreadLocal characteristics
 * to other classes.  Implementors should assure that the methods are thread-safe.
 *
 * @param <T> The type of object stored.
 *
 * @see ThreadLocal
 * @author Tom Howe
 * @since 1.0
 * @version 1.0
 */
public interface ThreadLocalAdapter<T> {

    /**
     * Get the current value.
     *
     * @return The value associated with this call.
     */
    T get();

    /**
     * Remove the current value associated with this call.
     */
    void remove();

    /**
     * Set the value associated with this call.  This will overwrite any value currently stored.
     *
     * @param value The value to set.
     */
    void set(T value);
}
