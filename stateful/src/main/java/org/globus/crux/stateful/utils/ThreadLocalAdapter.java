package org.globus.crux.stateful.utils;

public interface ThreadLocalAdapter<T> {

    T get();

    void remove();

    void set(T value);
}
