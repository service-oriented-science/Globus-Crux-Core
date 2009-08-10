package org.globus.crux;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.globus.crux.service.CreateState;
import org.globus.crux.service.EPRFactory;
import org.globus.crux.service.EPRFactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.EndpointReference;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.ResourceBundle;

/**
 * This takes care of integrating the developer's service method with the operation providers as well
 * as injecting the necessary state info into the object.  Pretty standard AOP.
 *
 * @author Tom Howe
 * @since 1.0
 * @version 1.0
 */
public class CruxMixin implements MethodInterceptor {
    private Map<MethodKey, Method> methodMap = new HashMap<MethodKey, Method>();
    private Set<Method> createMethods = new HashSet<Method>();
    private Set<Method> nonCreateMethods = new HashSet<Method>();
    private EPRFactory eprFactory;
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("org.globus.crux.crux");/* NON-NLS */
    private Logger logger = LoggerFactory.getLogger(getClass());

    public CruxMixin(Object delegate) {
        for (Method method : delegate.getClass().getMethods()) {
            MethodKey key = new MethodKey(method.getName(), method.getParameterTypes());
            methodMap.put(key, method);
        }
    }

    /**
     * This invokes the method.  In actuality, this invokes the method from the original object.
     * In this way, we can support all kinds of fun injections and the whatnot.
     *
     * @param mi
     * @return
     * @throws Throwable
     */
    public Object invoke(MethodInvocation mi) throws Throwable {
        Method method = mi.getMethod();
        MethodKey key = new MethodKey(method.getName(), method.getParameterTypes());
        Method createMethod = methodMap.get(key);
        if (createMethod == null) {
            String message = resourceBundle.getString("no.such.service.method");
            logger.warn(message);
            throw new RuntimeException(message);
        }
        Object result = createMethod.invoke(mi.getThis(), mi.getArguments());
        if (createMethods.contains(mi.getMethod())) {
            return processCreate(result, createMethod.getAnnotation(CreateState.class));
        }
        if (!nonCreateMethods.contains(mi.getMethod())) {
            if (EndpointReference.class.isAssignableFrom(mi.getMethod().getReturnType())) {
                createMethods.add(mi.getMethod());
                return processCreate(result, createMethod.getAnnotation(CreateState.class));
            } else {
                nonCreateMethods.add(mi.getMethod());
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Object processCreate(Object result, CreateState cs) throws EPRFactoryException {
        if (cs.localpart().length() > 0) {
            if (cs.namespace().length() > 0) {
                result = new JAXBElement(new QName(cs.namespace(), cs.localpart()), result.getClass(), result);
            } else {
                result = new JAXBElement(new QName(cs.localpart()), result.getClass(), result);
            }
        }
        return eprFactory.createEPRWithId(result);
    }

    public CruxMixin withEprFactory(EPRFactory eprFactory) {
        this.eprFactory = eprFactory;
        return this;
    }

    public void setEprFactory(EPRFactory eprFactory) {
        this.eprFactory = eprFactory;
    }

    class MethodKey {
        private String methodName;
        private Class[] argTypes;

        MethodKey(String methodName, Class[] argTypes) {
            this.methodName = methodName;
            this.argTypes = argTypes;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public Class[] getArgTypes() {
            return argTypes;
        }

        public void setArgTypes(Class[] argTypes) {
            this.argTypes = argTypes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MethodKey)) return false;

            MethodKey methodKey = (MethodKey) o;

            if (!Arrays.equals(argTypes, methodKey.argTypes)) return false;
            if (methodName != null ? !methodName.equals(methodKey.methodName) : methodKey.methodName != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = methodName != null ? methodName.hashCode() : 0;
            result = 31 * result + (argTypes != null ? Arrays.hashCode(argTypes) : 0);
            return result;
        }
    }
}
