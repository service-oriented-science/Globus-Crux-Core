package org.globus.crux.stateful.resource;

import org.globus.crux.stateful.SampleBean;

/**
 * @author turtlebender
 *         User: turtlebender
 *         Date: Jun 16, 2009
 *         Time: 3:25:15 PM
 */
public interface ResourceSampleBean<T> extends SampleBean<T> {
    ResourcefulStateInfo<Integer, T> getContext();

    void setContext(ResourcefulStateInfo<Integer, T> context);

    int getKey();
}
