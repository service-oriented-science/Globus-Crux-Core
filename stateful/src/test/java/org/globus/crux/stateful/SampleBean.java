package org.globus.crux.stateful;

import org.globus.crux.stateful.StatefulServiceException;

/**
 * @author turtlebender
 *         User: turtlebender
 *         Date: Jun 16, 2009
 *         Time: 3:03:03 PM
 */
public interface SampleBean<T> {
    T getState() throws StatefulServiceException;
}
