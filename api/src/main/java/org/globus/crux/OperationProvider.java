package org.globus.crux;

/**
 * @author turtlebender
 */
public interface OperationProvider<T>{
    public T getImplementation();
    public Class<T> getInterface();
}
