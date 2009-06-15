package org.globus.crux.utils;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.lang.reflect.Field;


public class FieldTypeInstanceOf extends BaseMatcher<Field> {
    private Class<?> fieldType;

    public FieldTypeInstanceOf(Class<?> fieldType) {
        this.fieldType = fieldType;
    }

    public boolean matches(Object o) {
        if (!(o instanceof Field)) {
            return false;
        }
        Field field = (Field) o;
        return fieldType.isAssignableFrom(field.getType());
    }

    public void describeTo(Description description) {
        description.appendText("Determines if the field is of an appropriate type");
    }
}
