package org.globus.crux;

import static ch.lambdaj.Lambda.filter;
import org.globus.crux.internal.ThreadLocalStateInfoAdapter;
import org.globus.crux.utils.ThreadLocalAdapter;
import org.globus.crux.utils.FieldAnnotationMatcher;
import org.globus.crux.utils.FieldTypeInstanceOf;
import org.hamcrest.Matcher;
import org.hamcrest.core.AllOf;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceMetadata<T, V> {
    private List<Field> stateInfoFields = new ArrayList<Field>();
    private Map<Field, ThreadLocalAdapter<V>> proxyMap = new HashMap<Field, ThreadLocalAdapter<V>>();

    private List<Matcher<? extends Field>> getMatchers(){
        List<Matcher<? extends Field>> matchers = new ArrayList<Matcher<? extends Field>>();
        matchers.add(new FieldAnnotationMatcher(StatefulContext.class));
        matchers.add(new FieldTypeInstanceOf(StateInfo.class));
        return matchers;
    }

    public ServiceMetadata(T target) {
        Class targetClass = target.getClass();
        Field[] fields = targetClass.getDeclaredFields();
        Matcher<Field> matcher = new AllOf<Field>(getMatchers());
        this.stateInfoFields = filter(matcher, Arrays.asList(fields));
        for(Field f: stateInfoFields){
            proxyMap.put(f, new ThreadLocalStateInfoAdapter<V>());
        }
    }

    public ThreadLocalAdapter<V> getStateInfoProxy(Field f) {
        return proxyMap.get(f);
    }

    public List<Field> getStateInfoFields() {
        return stateInfoFields;
    }
}
