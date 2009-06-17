package org.globus.crux.stateful.resource;

import org.globus.crux.stateful.AbstractServiceMetadata;

import java.lang.reflect.Field;

/**
 * This implementation of ServiceMetadata is based on a key->value tuple.  As such,
 * this class requires a ResourceManager which can be used to lookup the value from the key.
 *
 * @param <T> The key type of the tuple
 * @param <V> The value type of the tuple
 * @author turtlebender
 * @since 1.0
 * @version 1.0
 */
public class LookupServiceMetadata<T, V> extends AbstractServiceMetadata<T> {
    private ThreadLocalResourceStateInfoAdapter<T, V> resourceAdapter;

    public LookupServiceMetadata(Class<?> targetClass, ResourceManager<T, V> resourceManager) {
        super(targetClass);
        resourceAdapter = new ThreadLocalResourceStateInfoAdapter<T, V>(resourceManager);
    }

    @Override
    protected void processFields(Field[] fields) {
        fillMap(fields, ResourcefulStateInfo.class, this.resourceAdapter);
    }
}
