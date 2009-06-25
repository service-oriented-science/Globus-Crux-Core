package org.globus.crux.stateful;

import org.springframework.beans.factory.FactoryBean;


/**
 * Spring factory bean for adapting a service object to a stateful service object.
 *
 * @param <T> The type of the bean to be created
 * @param <V> The type of the state.
 * @author Tom Howe
 * @since 1.0
 * @version 1.0
 */
public class StatefulServiceFactoryBean<T, K, V> extends StatefulServiceFactory<T, K, V> implements FactoryBean {

    /**
     * @see org.springframework.beans.factory.FactoryBean
     *
     * @return The created Object
     * @throws StatefulServiceException if creating the bean fails.
     */
    public Object getObject() throws StatefulServiceException {
        return getStatefulService();
    }

    /**
     * @see org.springframework.beans.factory.FactoryBean
     * @return Target class
     */
    public Class getObjectType() {
        return getTargetClass();
    }

    /**
     * Always a singleton.  This does not support Prototype scope.
     *
     * @return True.
     */
    public boolean isSingleton() {
        return true;
    }

}
