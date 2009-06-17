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
 * @author turtlebender
 *         User: turtlebender
 *         Date: Jun 16, 2009
 *         Time: 3:57:43 PM
 */
public abstract class AbstractServiceMetadata<KEY> {
    private List<Field> stateInfoFields = new ArrayList<Field>();
    private Map<Field, ThreadLocalAdapter<KEY>> proxyMap =
            new HashMap<Field, ThreadLocalAdapter<KEY>>();
    private Class<?> clazz;

    public AbstractServiceMetadata(Class<?> clazz) {
        this.clazz = clazz;
    }

    public void init() {
        processFields(clazz.getDeclaredFields());
    }

    protected abstract void processFields(Field[] fields);

    protected final void fillMap(Field[] fields, Class fieldType, ThreadLocalAdapter<KEY> adapter) {
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

    private List<Matcher<? extends Field>> getMatchers(Class fieldType) {
        List<Matcher<? extends Field>> matchers = new ArrayList<Matcher<? extends Field>>();
        matchers.add(new FieldAnnotationMatcher(StatefulContext.class));
        matchers.add(new FieldTypeInstanceOf(fieldType));
        return matchers;
    }

    public ThreadLocalAdapter<KEY> getStateInfoProxy(Field f) {
        return proxyMap.get(f);
    }

    public List<Field> getStateInfoFields() {
        return stateInfoFields;
    }
}
