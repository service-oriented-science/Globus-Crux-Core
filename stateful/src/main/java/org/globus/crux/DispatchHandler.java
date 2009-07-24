package org.globus.crux;

import org.globus.crux.stateful.CruxContext;

import javax.xml.transform.Source;

/**
 * @author turtlebender
 */
public interface DispatchHandler<T extends CruxContext> {
    public Source handle(T context, Source request) throws Exception;        
}
