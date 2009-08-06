package org.globus.crux;

import org.springframework.beans.factory.FactoryBean;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * @author turtlebender
 */
public class JAXBContextFactory implements FactoryBean {
    private JAXBContext jaxb;

    public Object getObject() throws Exception {
        return jaxb;
    }

    public Class getObjectType() {
        return JAXBContext.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setContextPath(String contextPath) throws JAXBException {
        jaxb = JAXBContext.newInstance(contextPath);
    }
}
