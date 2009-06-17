package org.globus.crux.stateful.resource;

import org.globus.crux.stateful.StateInfo;
import org.globus.crux.stateful.StatefulServiceException;


public interface ResourcefulStateInfo<KEY, VALUE> extends StateInfo<VALUE> {
    KEY getResourceId() throws StatefulServiceException;
}
