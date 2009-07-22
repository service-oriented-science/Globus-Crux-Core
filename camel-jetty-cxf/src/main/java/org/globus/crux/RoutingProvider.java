package org.globus.crux;

import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.transform.Source;

/**
 * @author turtlebender
 */
@WebServiceProvider
@ServiceMode(value= Service.Mode.PAYLOAD)
public class RoutingProvider implements Provider<Source>{
    public Source invoke(Source source) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
