package org.globus.crux.stateful.utils;

/**
 * This is a very simple class which wraps a ThreadLocal.  Extensions of this will
 * likely use these methods to set and unset state.
 *
 * @param <T> The type of value stored in the ThreadLocal
 * @see ThreadLocal
 *
 * @author Tom Howe
 * @since 1.0
 * @version 1.0
 */
public class AbstractThreadLocalAdapter<T> implements ThreadLocalAdapter<T> {

    private ThreadLocal<T> info = new ThreadLocal<T>();

    /**
     * Get the current value on this thread.
     *
     * @return The current value
     */
    public T get() {
        return this.info.get();
    }

    /**
     * Remove the current value from this thread.
     */
    public void remove() {
        this.info.remove();
    }

    /**
     * Set the current value on this thread.  This will overwrite any existing value.
     *
     * @param value The value to set.
     */
    public void set(T value) {
        this.info.set(value);
    }
}
