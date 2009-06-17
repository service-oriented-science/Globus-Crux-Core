package org.globus.crux.stateful.utils;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.lang.reflect.Field;

/**
 * Matches fields of a particular type.  This is part of the Hamcrest framework used by lamdaj
 *
 * @author Tom Howe
 * @since 1.0
 * @version 1.0
 */
public class FieldTypeInstanceOf extends BaseMatcher<Field> {
    private Class<?> fieldType;

    public FieldTypeInstanceOf(Class<?> fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * Matches fields whose type is assignable to the specified type declared in the constructor.
     *
     * @param o The Object to match.
     * @return True if the object is a Field of the type declared in the constructor.
     */
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
