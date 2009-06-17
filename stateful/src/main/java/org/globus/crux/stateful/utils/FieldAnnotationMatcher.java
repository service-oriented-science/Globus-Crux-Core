package org.globus.crux.stateful.utils;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.lang.reflect.Field;
import java.lang.annotation.Annotation;


/**
 * This matcher finds fields that are tagged with a specified Annotation.
 * This is used in conjuction with the Hamcrest framework in lamdaj
 *
 * @author Tom Howe
 * @since 1.0
 * @version 1.0
 */
public class FieldAnnotationMatcher extends BaseMatcher<Field> {
    private Class<? extends Annotation> annoClass;

    public FieldAnnotationMatcher(Class<? extends Annotation> targetClass) {
        this.annoClass = targetClass;
    }

    /**
     * Matches any Fields that are tagged with the Annotation specified in the constructor.
     *
     * @param o The object to test.
     * @return True if the Field is tagged with the specified Annotation.
     */
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
