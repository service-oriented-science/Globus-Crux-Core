package org.globus.crux.stateful.resource;

import org.globus.crux.stateful.StateInfo;
import org.globus.crux.stateful.StatefulServiceException;


public interface ResourcefulStateInfo<T, V> extends StateInfo<V> {
    T getResourceId() throws StatefulServiceException;    
}
