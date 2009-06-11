package org.globus.crux.utils;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.lang.reflect.Field;
import java.lang.annotation.Annotation;


public class FieldAnnotationMatcher extends BaseMatcher<Field> {
    Class<? extends Annotation> annoClass;

    public FieldAnnotationMatcher(Class<? extends Annotation> targetClass) {
        this.annoClass = targetClass;
    }

    public boolean matches(Object o) {
        if (o instanceof Field) {
            Field field = (Field) o;
            if (field.getAnnotation(annoClass) != null) {
                return true;
            }
        }
        return false;
    }

    public void describeTo(Description description) {
        description.appendText("Field Annotation Matcher");
    }
}
