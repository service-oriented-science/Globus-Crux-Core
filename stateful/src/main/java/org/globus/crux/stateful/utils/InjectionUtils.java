package org.globus.crux.stateful.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivilegedAction;
import java.security.AccessController;
import java.lang.reflect.Field;

/**
 * Utilities for injecting values into fields and methods.
 *
 * @author Tom Howe
 * @since 1.0
 * @version 1.0
 */
public final class InjectionUtils {
    private static Logger logger = LoggerFactory.getLogger(InjectionUtils.class);

    private InjectionUtils() {
    }

    /**
     * Inject value into field
     *
     * @param f The field into which to inject the value
     * @param o The Object into which to inject the value
     * @param v The field to inject
     */
    @SuppressWarnings("unchecked")
    public static void injectFieldValue(final Field f,
                                        final Object o,
                                        final Object v) {
        AccessController.doPrivileged(new PrivilegedAction() {

            public Object run() {
                logger.debug("Injecting {} into {} via field: {}", new Object[]{v, o, f});
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
