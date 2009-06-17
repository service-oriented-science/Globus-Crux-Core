package org.globus.crux.stateful;

import org.globus.crux.stateful.utils.ThreadLocalAdapter;
import org.globus.crux.stateful.utils.FieldAnnotationMatcher;
import org.globus.crux.stateful.utils.FieldTypeInstanceOf;
import org.hamcrest.Matcher;
import org.hamcrest.core.AllOf;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * This is an abstract class for describing the stateful service metadata associated with a class.
 * This defines which fields/methods contain stateful information for the service targeted.
 *
 * @param <T> This is the type of the state value.
 *
 * @author Tom Howe
 * @since 1.0
 * @version 1.0
 */
public abstract class AbstractServiceMetadata<T> {
    private List<Field> stateInfoFields = new ArrayList<Field>();
    private Map<Field, ThreadLocalAdapter<T>> proxyMap =
            new HashMap<Field, ThreadLocalAdapter<T>>();
    private Class<?> clazz;

    /**
     * Standard constructor which takes the class associated with this metadata.
     *
     * @param clazz The associated target class.
     */
    public AbstractServiceMetadata(Class<?> clazz) {
        this.clazz = clazz;
    }

    public void init() {
        processFields(clazz.getDeclaredFields());
    }

    protected abstract void processFields(Field[] fields);

    /**
     * This will create the metadata by finding the fields that match the provided field type
     * and define them to the supplied ThreadLocalAdapter type.
     *
     * @see org.globus.crux.stateful.utils.ThreadLocalAdapter
     *
     * @param fields The candidate fields.
     * @param fieldType The field type to match.
     * @param adapter The ThreadLocalAdapter to assign to the field.
     */
    protected final void fillMap(Field[] fields, Class fieldType, ThreadLocalAdapter<T> adapter) {
        Matcher<Field> matcher = new AllOf<Field>(getMatchers(fieldType));
        List<Field> matchedFields = ch.lambdaj.Lambda.filter(matcher, Arrays.asList(fields));
        if (this.stateInfoFields != null) {
            this.stateInfoFields.addAll(matchedFields);
        } else {
            this.stateInfoFields = matchedFields;
        }
        for (Field f : stateInfoFields) {
            proxyMap.put(f, adapter);
        }
    }

    //This is the list of matchers currently assigned to filter the fields.
    private List<Matcher<? extends Field>> getMatchers(Class fieldType) {
        List<Matcher<? extends Field>> matchers = new ArrayList<Matcher<? extends Field>>();
        matchers.add(new FieldAnnotationMatcher(StatefulContext.class));
        matchers.add(new FieldTypeInstanceOf(fieldType));
        return matchers;
    }

    public ThreadLocalAdapter<T> getStateInfoProxy(Field f) {
        return proxyMap.get(f);
    }

    public List<Field> getStateInfoFields() {
        return stateInfoFields;
    }
}
