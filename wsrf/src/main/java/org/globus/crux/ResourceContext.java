package org.globus.crux;

/**
 * @author turtlebender
 */
public interface ResourceContext<V, T> {
    public T getCurrentResource();

    public void storeResource(V key, T value);
}
