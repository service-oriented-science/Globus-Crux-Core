package org.globus.crux.stateful;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@StatefulService
public class MySampleBean {
    Logger logger = LoggerFactory.getLogger(MySampleBean.class);
    @StatefulContext
    StateInfo<Integer> context;
    
    public Integer getState() throws StatefulServiceException{
        return context.getResource();
    }

}
