package org.globus.crux.stateful;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author turtlebender
 */
//@StatefulService(autoCommit = false)
public class MySampleBeanNoAutoCommit<T> implements SampleBean<T>{
    Logger logger = LoggerFactory.getLogger(MySampleBean.class);
        @StatefulContext
        StateInfo<T> context;

        public T getState() throws StatefulServiceException{
            return context.getState();
        }

}
