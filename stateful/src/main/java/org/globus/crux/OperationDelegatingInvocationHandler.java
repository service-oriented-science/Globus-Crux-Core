package org.globus.crux;

import org.globus.crux.stateful.ServiceMethodProcessor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.aop.Advisor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.awt.*;

/**
 * @author turtlebender
 */
public class OperationDelegatingInvocationHandler implements InvocationHandler {
    private Map<Class, Object> providerMap = new ConcurrentHashMap<Class, Object>();
    private Object targetObject;

    public OperationDelegatingInvocationHandler(Object targetObject) {
        this.targetObject = targetObject;
    }

    public void registerProvider(Class<?> i, Object o) {
        providerMap.put(i, o);
    }

    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        Object target = providerMap.get(method.getDeclaringClass());
        String methodName = method.getName();
        if (target == null) {
            Method m = targetObject.getClass().getMethod(methodName, method.getParameterTypes());
            return m.invoke(targetObject, objects);
        } else {
            Method m = target.getClass().getMethod(methodName, method.getParameterTypes());
            return m.invoke(target, objects);
        }

    }

    public static void main(String[] args) throws Exception {
        ProxyFactory factory = new ProxyFactory(new ServiceMethodProcessor());
        factory.addAdvisor(new DefaultOperationProvider(List.class, new ArrayList()).getAdvisor());
        factory.setProxyTargetClass(true);
        List proxy = (List) factory.getProxy();
        proxy.add("foo");
        ServiceMethodProcessor proc = (ServiceMethodProcessor) proxy;
        proc.addProcessor(null);
        proxy.get(0);
//        OperationDelegatingInvocationHandler handler = new OperationDelegatingInvocationHandler(target);
//        handler.registerProvider(List.class, new ArrayList());
//        List list = (List) Proxy.newProxyInstance(Object.class.getClassLoader(),
//                new Class[]{List.class}, handler);
//        list.add("foo");
//        list.toString();
    }
}
