package cxf.jaxb;

import cxf.StatefulServiceWebProvider;

import java.lang.reflect.Method;

/**
 * @author turtlebender
 */
public interface MethodProcessor {
    public boolean canProcess(Method method);

    public void process(Object toProcess, Method method, StatefulServiceWebProvider provider);

}
