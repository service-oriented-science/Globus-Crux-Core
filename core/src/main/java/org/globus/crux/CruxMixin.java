package org.globus.crux;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.EndpointReference;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.globus.crux.service.CreateState;
import org.globus.crux.service.EPRFactory;
import org.globus.crux.service.EPRFactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This takes care of integrating the developer's service method with the operation providers as well
 * as injecting the necessary state info into the object.  Pretty standard AOP.
 *
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public class CruxMixin implements MethodInterceptor, InvocationHandler {
    private Map<MethodKey, MethodValue> methodMap = new HashMap<MethodKey, MethodValue>();
    private Set<Method> createMethods = new HashSet<Method>();
    private Set<Method> nonCreateMethods = new HashSet<Method>();
    private Map<Class, MethodCallWrapper> wrappers = new HashMap<Class, MethodCallWrapper>();
    private EPRFactory eprFactory;
    private MethodCallWrapper rpChangedMCW;
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("org.globus.crux.crux");/* NON-NLS */
    private Logger logger = LoggerFactory.getLogger(getClass());

    public CruxMixin(Object delegate, Class iface) throws NoSuchMethodException {
        Class delegateClass = delegate.getClass();
        for (Method method : iface.getMethods()) {
            String methodName = method.getName();
            Class[] parameterTypes = method.getParameterTypes();
            MethodKey key = new MethodKey(method.getName(), method.getParameterTypes());
            try {
                Method methodToCall = delegateClass.getMethod(methodName, parameterTypes);
                if(methodToCall.getAnnotation(CreateState.class) != null){
                    createMethods.add(methodToCall);
                }
                methodMap.put(key, new MethodValue(methodToCall, delegate));
            } catch (NoSuchMethodException nsme) {
                logger.debug("Method not present in delegate", nsme);
                //this is fine, the method is not exposed via the web service
            }
        }
    }

    //TODO: throw appropriate exception

    public void addProvider(OperationProvider provider) throws Exception {
        Class clazz = provider.getInterface();
        Object delegate = provider.getImplementation();
        for (Method method : clazz.getMethods()) {
            String methodName = method.getName();
            Class[] parameterType = method.getParameterTypes();
            MethodKey key = new MethodKey(methodName, parameterType);
            Class delegateClass = provider.getImplementation().getClass();
            Method methodToCall = delegateClass.getMethod(method.getName(), parameterType);
            methodMap.put(key, new MethodValue(methodToCall, delegate));
        }
    }
    
    public void addWrapper(MethodCallWrapper wrapper) throws Exception {
    	wrappers.put(wrapper.getAssociatedAnnotation(), wrapper);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodKey key = new MethodKey(method.getName(), method.getParameterTypes());
        MethodValue methodToCall = methodMap.get(key);
        if (methodToCall == null) {
            String message = resourceBundle.getString("no.such.service.method");
            logger.warn(message);
            throw new NoSuchMethodError(resourceBundle.getString("no.such.service.method"));
        }
        
        HashSet<MethodCallWrapper> wrappersToInvoke = new HashSet<MethodCallWrapper>();
        for (Class annotation : wrappers.keySet()) {
        	if (methodToCall.getMethod().isAnnotationPresent(annotation)) {
        		wrappersToInvoke.add(wrappers.get(annotation));
        	}
        }
        
        for (MethodCallWrapper wrapper : wrappersToInvoke) {
        	wrapper.doBefore(methodToCall.getMethod().getAnnotation(wrapper.getAssociatedAnnotation()));
        }
    	
        Object result = methodToCall.getMethod().invoke(methodToCall.getTarget(), args);

    	for (MethodCallWrapper wrapper : wrappersToInvoke) {
        	wrapper.doAfter(methodToCall.getMethod().getAnnotation(wrapper.getAssociatedAnnotation()));
        }

        if (createMethods.contains(methodToCall.getMethod())) {
            return processCreate(result, methodToCall.getMethod().getAnnotation(CreateState.class));
        }
        if (!nonCreateMethods.contains(methodToCall.getMethod())) {
            if (EndpointReference.class.isAssignableFrom(methodToCall.getMethod().getReturnType())) {
                createMethods.add(methodToCall.getMethod());
                return processCreate(result, methodToCall.getMethod().getAnnotation(CreateState.class));
            } else {
                nonCreateMethods.add(methodToCall.getMethod());
            }
        }
        
        return result;
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
        return invoke(null, mi.getMethod(), mi.getArguments());
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
    
    class MethodValue {
        private Method method;
        private Object target;

        MethodValue(Method method, Object target) {
            this.method = method;
            this.target = target;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public Object getTarget() {
            return target;
        }

        public void setTarget(Object target) {
            this.target = target;
        }
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
