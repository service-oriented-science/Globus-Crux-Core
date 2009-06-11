package org.globus.crux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@StatefulService
public class MySampleBean {
    Logger logger = LoggerFactory.getLogger(MySampleBean.class);
    @StatefulContext
    StateInfo<Integer> context;
    
    public Integer getState() {
        return context.getResource();
    }

}
