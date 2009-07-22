package org.globus.crux.stateful.internal;

import org.globus.crux.stateful.StateAdapter;
import org.globus.crux.stateful.utils.AbstractThreadLocalAdapter;
import org.globus.crux.stateful.utils.ThreadLocalAdapter;

/**
 * @author turtlebender
 */
public class DefaultStateAdapter<T> implements StateAdapter<T> {
    private ThreadLocalAdapter<T> adapter = new AbstractThreadLocalAdapter<T>() {
        public T getState() {
            return get();
        }
    };

    public T getState() {
        return adapter.get();
    }

    public void setState(T state) {
        adapter.set(state);
    }
}
