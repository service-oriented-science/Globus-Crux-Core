package org.globus.crux.cxf;

import org.globus.crux.stateful.CruxContext;

import javax.xml.transform.Source;
import javax.xml.ws.WebServiceContext;

/**
 * @author turtlebender
 */
public interface DispatchHandler<T extends CruxContext> {
    public Source handle(T context, Source request) throws Exception;        
}
