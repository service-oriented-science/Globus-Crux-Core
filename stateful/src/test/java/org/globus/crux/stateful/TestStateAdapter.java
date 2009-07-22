package org.globus.crux.stateful;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author turtlebender
 *         User: turtlebender
 *         Date: Jun 16, 2009
 *         Time: 12:59:34 PM
 */
public class TestStateAdapter<T> implements StateAdapter<T> {
    Map<Thread, T> stateMap = new ConcurrentHashMap<Thread, T>();

    public T getState() {
        return stateMap.get(Thread.currentThread());
    }

    public void setState(T state) {
        stateMap.put(Thread.currentThread(), state);
    }

}
