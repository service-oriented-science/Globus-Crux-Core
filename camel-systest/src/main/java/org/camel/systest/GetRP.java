package org.camel.systest;

import org.globus.crux.stateful.StateInfo;
import org.globus.crux.stateful.StatefulServiceException;
import org.globus.crux.stateful.StatefulService;
import org.globus.crux.stateful.StatefulContext;
import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.util.List;
import static java.util.Arrays.asList;
import java.security.AccessController;
import java.security.PrivilegedAction;

import static ch.lambdaj.Lambda.*;

//@StatefulService
public class GetRP {
    Logger logger = LoggerFactory.getLogger(getClass());
    @StatefulContext
    private StateInfo<Object> context;

    public GetRP() {
    }

    public Object getRP(QName prop) throws StatefulServiceException {
        final Object resource = context.getState();
        Matcher propMatcher = new ResourcePropertyMatcher(prop);
        Object result;

        result = findField(resource, propMatcher);
        if (result == null) {
            result = findMethod(resource, propMatcher);
        }
        return result;
    }

    private Object findMethod(final Object resource, Matcher propMatcher) {
        Object result = null;
        Method[] methods = resource.getClass().getMethods();
        List<Method> methodList = asList(methods);
        final Method method = selectFirst(methodList, propMatcher);
        if (method != null) {
            result = AccessController.doPrivileged(new PrivilegedAction<Object>() {
                public Object run() {
                    method.setAccessible(true);
                    try {
                        return method.invoke(resource);
                    } catch (IllegalAccessException ex) {
                        logger.error("Method_ACCESS_FAILURE", ex, method.getReturnType().getName());
                    } catch (InvocationTargetException e) {
                        logger.error("Method_ACCESS_FAILURE", e, method.getReturnType().getName());
                    }
                    return null;
                }
            });
        }
        return result;
    }

    private Object findField(final Object resource, Matcher propMatcher) {
        Object result = null;
        Field[] fields = resource.getClass().getFields();
        List<Field> fieldList = asList(fields);
        final Field field = selectFirst(fieldList, propMatcher);
        if (field != null) {
            result = AccessController.doPrivileged(new PrivilegedAction<Object>() {
                public Object run() {
                    field.setAccessible(true);
                    try {
                        return field.get(resource);
                    } catch (IllegalAccessException ex) {
                        logger.error("FIELD_ACCESS_FAILURE", field.getType().getName());
                    }
                    return null;
                }
            });
        }
        return result;
    }

    public StateInfo<Object> getContext() {
        return context;
    }

    public void setContext(StateInfo<Object> context) {
        this.context = context;
    }
}