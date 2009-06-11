package org.globus.crux.utils;

public class AbstractThreadLocalAdapter<T> implements ThreadLocalAdapter<T> {

    private ThreadLocal<T> info = new ThreadLocal<T>();

    public T get() {
        return this.info.get();
    }

    public void remove() {
        this.info.remove();
    }

    public void set(T value) {
        this.info.set(value);
    }
}
