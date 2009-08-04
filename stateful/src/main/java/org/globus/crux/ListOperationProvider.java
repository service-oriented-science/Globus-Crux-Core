package org.globus.crux;

import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;

import java.util.List;
import java.util.ArrayList;

/**
 * @author turtlebender
 */
public class ListOperationProvider implements OperationProvider{
    public Advisor getAdvisor() {
        return new DefaultIntroductionAdvisor(
                        new DelegatingIntroductionInterceptor(new ArrayList()), List.class);
    }
}
