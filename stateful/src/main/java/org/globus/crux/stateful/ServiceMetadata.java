package org.globus.crux.stateful;

import org.globus.crux.stateful.internal.ThreadLocalStateInfoAdapter;

import java.lang.reflect.Field;

/**
 * This is the metadata class for describing state that is not defined as tuples.
 *
 * @param <T> The type of the value of the fields associated with this metadata.
 *
 * @see org.globus.crux.stateful.StateInfo
 * @see org.globus.crux.stateful.internal.ThreadLocalStateInfoAdapter
 *
 * @author Tom Howe
 * @since 1.0
 * @version 1.0
 */
public class ServiceMetadata<T> extends AbstractServiceMetadata<T> {

    public ServiceMetadata(Class<?> clazz) {
        super(clazz);
    }

    /**
     * Create adapters for fields requiring StateInfo.
     *
     * @see org.globus.crux.stateful.StateInfo
     *
     * @param fields The filtered fields from the target class.
     */
    @Override
    protected void processFields(Field[] fields) {
        fillMap(fields, StateInfo.class, new ThreadLocalStateInfoAdapter<T>());
    }
}
