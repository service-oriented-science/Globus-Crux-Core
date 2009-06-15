package org.globus.crux.utils;

public interface ThreadLocalAdapter<T> {

    T get();

    void remove();

    void set(T value);
}
