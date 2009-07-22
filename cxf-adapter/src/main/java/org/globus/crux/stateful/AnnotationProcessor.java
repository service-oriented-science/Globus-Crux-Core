package org.globus.crux.stateful;

import cxf.StatefulServiceWebProvider;
import cxf.jaxb.MethodProcessor;

import javax.xml.bind.JAXBContext;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;

/**
 * @author turtlebender
 */
public class AnnotationProcessor {
    private StatefulServiceWebProvider provider;
    private List<MethodProcessor> processors = new ArrayList<MethodProcessor>();

    public AnnotationProcessor withProvider(StatefulServiceWebProvider provider) {
        this.provider = provider;
        return this;
    }

    public AnnotationProcessor withProcessor(MethodProcessor processor){
        processors.add(processor);
        return this;
    }

    public void addProcessor(MethodProcessor processor) {
        this.processors.add(processor);
    }

    public void setProvider(StatefulServiceWebProvider provider) {
        this.provider = provider;
    }

    public void processObject(Object toProcess) {
        for (Method method : toProcess.getClass().getDeclaredMethods()) {
            for (MethodProcessor processor : processors) {
                if (processor.canProcess(method)) {
                    processor.process(toProcess, method, provider);
                    break;
                }
            }
        }
    }
}