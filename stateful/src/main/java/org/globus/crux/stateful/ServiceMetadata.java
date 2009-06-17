package org.globus.crux.stateful;

import org.globus.crux.stateful.internal.ThreadLocalStateInfoAdapter;

import java.lang.reflect.Field;

public class ServiceMetadata<KEY> extends AbstractServiceMetadata<KEY> {

    public ServiceMetadata(Class<?> clazz) {
        super(clazz);
    }

    @Override
    protected void processFields(Field[] fields) {
        fillMap(fields, StateInfo.class, new ThreadLocalStateInfoAdapter<KEY>());
    }
}
