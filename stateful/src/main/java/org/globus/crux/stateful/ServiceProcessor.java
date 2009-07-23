package org.globus.crux.stateful;


import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;

/**
 * @author turtlebender
 */
public class ServiceProcessor {
    private List<MethodProcessor> processors = new ArrayList<MethodProcessor>();

    public ServiceProcessor withProcessor(MethodProcessor processor){
        processors.add(processor);
        return this;
    }

    public void addProcessor(MethodProcessor processor) {
        this.processors.add(processor);
    }

    public void processObject(Object toProcess) {
        for (Method method : toProcess.getClass().getDeclaredMethods()) {
            for (MethodProcessor processor : processors) {
                if (processor.canProcess(method)) {
                    processor.process(toProcess, method);
                    break;
                }
            }
        }
    }
}