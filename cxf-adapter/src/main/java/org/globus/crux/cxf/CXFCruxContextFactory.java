package org.globus.crux.cxf;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceContext;

/**
 * @author turtlebender
 */
public class CXFCruxContextFactory implements CruxContextFactory<WebServiceContext, CXFCruxContext> {
    private JAXBContext jaxb;
    private QName keyName;

    public CXFCruxContextFactory(JAXBContext jaxb, QName keyName) {
        this.jaxb = jaxb;
        this.keyName = keyName;
    }

    public CXFCruxContext createContext(WebServiceContext context) {
        return new CXFCruxContext(context, keyName, jaxb);
    }  
}
