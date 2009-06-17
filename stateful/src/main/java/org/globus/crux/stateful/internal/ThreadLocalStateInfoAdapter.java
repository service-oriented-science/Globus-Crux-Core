package org.globus.crux.stateful.internal;

import org.globus.crux.stateful.StateInfo;
import org.globus.crux.stateful.utils.AbstractThreadLocalAdapter;


public class ThreadLocalStateInfoAdapter<T> extends AbstractThreadLocalAdapter<T> implements StateInfo<T> {

    public T getResource() {
        return this.get();        
    }
}
