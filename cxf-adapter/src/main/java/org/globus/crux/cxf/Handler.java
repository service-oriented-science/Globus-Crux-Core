package org.globus.crux.cxf;

import javax.xml.transform.Source;
import javax.xml.ws.WebServiceContext;

/**
 * @author turtlebender
 */
public interface Handler {
    public Source handle(WebServiceContext wsc, Source request) throws Exception;        
}
