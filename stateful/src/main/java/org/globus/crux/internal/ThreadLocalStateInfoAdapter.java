package org.globus.crux.internal;

import org.globus.crux.StateInfo;
import org.globus.crux.utils.AbstractThreadLocalAdapter;


public class ThreadLocalStateInfoAdapter<T> extends AbstractThreadLocalAdapter<T> implements StateInfo<T> {

    public T getResource() {
        return this.get();        
    }
}
