package org.globus.crux.utils;

public interface ThreadLocalAdapter<T> {

    public T get();

    public void remove();

    public void set(T value);
}
