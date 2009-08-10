package org.globus.crux;

/**
 * @author turtlebender
 */
public interface OperationProvider<T>{
    public T getImplementation() throws ProviderException;
    public Class<T> getInterface();
}
