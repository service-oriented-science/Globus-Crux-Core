package org.globus.crux.stateful;

import java.lang.reflect.Method;

/**
 * @author turtlebender
 */
public interface MethodProcessor {
    public boolean canProcess(Method method);

    public void process(Object toProcess, Method method);

}
