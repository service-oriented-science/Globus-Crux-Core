package org.globus.crux.stateful.resource;

import org.globus.crux.stateful.AbstractServiceMetadata;

import java.lang.reflect.Field;

/**
 * @author turtlebender
 *         User: turtlebender
 *         Date: Jun 16, 2009
 *         Time: 2:42:40 PM
 */
public class LookupServiceMetadata<KEY, VALUE> extends AbstractServiceMetadata<KEY> {
    private ThreadLocalResourceStateInfoAdapter<KEY, VALUE> resourceAdapter;

    public LookupServiceMetadata(Class<?> targetClass, ResourceManager<KEY, VALUE> resourceManager) {
        super(targetClass);
        resourceAdapter = new ThreadLocalResourceStateInfoAdapter<KEY, VALUE>(resourceManager);
    }

    @Override
    protected void processFields(Field[] fields) {
        fillMap(fields, ResourcefulStateInfo.class, this.resourceAdapter);
    }
}
