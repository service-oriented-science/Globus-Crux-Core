package org.globus.crux.stateful.internal;

import org.globus.crux.stateful.StatefulServiceException;
import org.globus.crux.service.StatefulService;
import org.globus.crux.service.DestroyState;
import org.globus.crux.service.CreateState;
import org.globus.crux.service.StatefulMethod;
import org.globus.crux.service.StateKeyParam;

import javax.xml.namespace.QName;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author turtlebender
 */
public class StatefulServiceDesc {
    //Maps paramter type to operation.  need to figure out how to do this with actions
    private ConcurrentHashMap<Class<?>, StatefulOperationDesc> operationMap =
            new ConcurrentHashMap<Class<?>, StatefulOperationDesc>();
    private QName keyQname;
    private Object serviceObject;

    public StatefulServiceDesc(Object serviceObject) throws StatefulServiceException {
        this.serviceObject = serviceObject;
        Class<?> statefulServiceClass = serviceObject.getClass();
        StatefulService serviceAnno = statefulServiceClass.getAnnotation(StatefulService.class);
        if (serviceAnno == null) {
            throw new StatefulServiceException(String.format("%s is not a stateful service",
                    statefulServiceClass.getName()));
        }
        keyQname = new QName(serviceAnno.namespace(), serviceAnno.keyName());
        if (serviceAnno.publicInterface() != Object.class) {
            for (Method method : serviceAnno.publicInterface().getMethods()) {
                Method serviceMethod = getServiceMethod(statefulServiceClass, method);
                if (isStatefulMethod(method) || isStatefulMethod(serviceMethod)) {
                    addStatefulOperation(serviceMethod);
                } else if (isCreateMethod(method) || isCreateMethod(serviceMethod)) {
                    addCreateMethod(serviceMethod);
                } else if (isDestroyMethod(method) || isDestroyMethod(serviceMethod)) {
                    addDestroyMethod(serviceMethod);
                } else {
                    addNonStatefulOperation(serviceMethod);
                }
            }
        } else {
            for (Method method : statefulServiceClass.getDeclaredMethods()) {
                if (isStatefulMethod(method)) {
                    addStatefulOperation(method);
                } else if (isCreateMethod(method)) {
                    addCreateMethod(method);
                } else if (isDestroyMethod(method)) {
                    addDestroyMethod(method);
                } else {
                    addNonStatefulOperation(method);
                }
            }
        }
    }

    public Object invoke(Object parameter) throws StatefulServiceException {
        return invoke(parameter, null);
    }

    public Object invoke(Object parameter, Object key) throws StatefulServiceException {
        StatefulOperationDesc desc = this.operationMap.get(parameter.getClass());
        if (desc.isStateful()) {
            if (key == null) {
                throw new StatefulServiceException("State key is required");
            }
            int paramIndex = desc.getStateParamIndex();
            Object[] params;
            if (paramIndex == 0) {
                params = new Object[]{key, parameter};
            } else {
                params = new Object[]{parameter, key};
            }
            try {
                return desc.getTargetMethod().invoke(this.serviceObject, params);
            } catch (IllegalAccessException e) {
                throw new StatefulServiceException(e);
            } catch (InvocationTargetException e) {
                throw new StatefulServiceException(e);
            }
        } else if (desc.isCreate()) {
            try {
                return desc.getTargetMethod().invoke(this.serviceObject, parameter);
            } catch (IllegalAccessException e) {
                throw new StatefulServiceException(e);
            } catch (InvocationTargetException e) {
                throw new StatefulServiceException(e);
            }
        }
        return null;
    }

    private StatefulOperationDesc addCreateMethod(Method serviceMethod) {
        StatefulOperationDesc desc = new StatefulOperationDesc();
        desc.setCreate(true);
        desc.setTargetMethod(serviceMethod);
        desc.setParamType(serviceMethod.getParameterTypes()[0]);
        this.operationMap.put(desc.getParamType(), desc);
        return desc;
    }

    private StatefulOperationDesc addDestroyMethod(Method serviceMethod) throws StatefulServiceException {
        StatefulOperationDesc desc = addStatefulOperation(serviceMethod);
        desc.setStateful(false);
        desc.setDestroy(true);
        return desc;
    }

    private StatefulOperationDesc addNonStatefulOperation(Method method) {
        StatefulOperationDesc desc = new StatefulOperationDesc();
        desc.setStateful(false);
        desc.setTargetMethod(method);
        this.operationMap.put(method.getParameterTypes()[0], desc);
        return desc;
    }

    private StatefulOperationDesc addStatefulOperation(Method method) throws StatefulServiceException {
        Class<?>[] parameters = method.getParameterTypes();
        if (parameters.length > 2 || parameters.length == 0) {
            throw new StatefulServiceException("Stateful Method must have 1 or 2 parameters," +
                    "one of which is the state argument");
        }
        StatefulOperationDesc desc = new StatefulOperationDesc();
        desc.setTargetMethod(method);
        desc.setStateful(true);
        Annotation[][] paramAnnos = method.getParameterAnnotations();
        int i = 0;
        for (Annotation[] iParamAnno : paramAnnos) {
            for (Annotation paramAnno : iParamAnno) {
                if (paramAnno instanceof StateKeyParam) {
                    desc.setStateParamIndex(i);
                }
            }
            i++;
        }
        if (desc.getStateParamIndex() == 0) {
            Class<?> keyType = method.getParameterTypes()[1];
            desc.setParamType(keyType);
            this.operationMap.put(method.getParameterTypes()[1], desc);
        } else {
            Class<?> keyType = method.getParameterTypes()[0];
            desc.setParamType(keyType);
            this.operationMap.put(method.getParameterTypes()[0], desc);
        }
        return desc;
    }


    private boolean isDestroyMethod(Method method) {
        return method.isAnnotationPresent(DestroyState.class);
    }

    private boolean isCreateMethod(Method iMethod) throws StatefulServiceException {
        return iMethod.isAnnotationPresent(CreateState.class);
    }

    private boolean isStatefulMethod(Method iMethod) throws StatefulServiceException {
        return iMethod.isAnnotationPresent(StatefulMethod.class);
    }

    private Method getServiceMethod(Class<?> serviceClass, Method iMethod) throws StatefulServiceException {
        Method serviceMethod;
        try {
            serviceMethod = serviceClass.getMethod(iMethod.getName(), iMethod.getParameterTypes());
        } catch (NoSuchMethodException e) {
            throw new StatefulServiceException(e);
        }
        return serviceMethod;
    }

    public QName getKeyQname() {
        return keyQname;
    }

    public void setKeyQname(QName keyQname) {
        this.keyQname = keyQname;
    }
}
