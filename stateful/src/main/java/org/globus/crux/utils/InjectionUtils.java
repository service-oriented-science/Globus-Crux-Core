package org.globus.crux.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivilegedAction;
import java.security.AccessController;
import java.lang.reflect.Field;


public class InjectionUtils {
    Logger logger = LoggerFactory.getLogger(getClass());

    @SuppressWarnings("unchecked")
    public static void injectFieldValue(final Field f,
                                        final Object o,
                                        final Object v) {
        AccessController.doPrivileged(new PrivilegedAction() {
            Logger logger = LoggerFactory.getLogger(getClass());

            public Object run() {
                f.setAccessible(true);
                try {
                    f.set(o, v);
                } catch (IllegalAccessException ex) {
                    logger.error("FIELD_ACCESS_FAILURE", f.getType().getName());
                }
                return null;
            }
        });

    }

}
