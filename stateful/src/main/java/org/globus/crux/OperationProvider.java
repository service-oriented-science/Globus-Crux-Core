package org.globus.crux;

import org.springframework.aop.Advisor;

/**
 * @author turtlebender
 */
public interface OperationProvider{
    public Advisor getAdvisor();     
}
