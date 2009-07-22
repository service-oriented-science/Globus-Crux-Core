package org.globus.crux.stateful.internal;

import java.lang.reflect.Method;

/**
 * @author turtlebender
 */
public class StatefulOperationDesc {
    private boolean create;
    private boolean destroy;
    private Method targetMethod;
    private Class<?> paramType;
    private String action;
    private boolean stateful;
    private int stateParamIndex;

    public boolean isStateful() {
        return stateful;
    }

    public void setStateful(boolean stateful) {
        this.stateful = stateful;
    }

    public int getStateParamIndex() {
        return stateParamIndex;
    }

    public void setStateParamIndex(int stateParamIndex) {
        this.stateParamIndex = stateParamIndex;
    }

    public boolean isCreate() {
        return create;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }

    public boolean isDestroy() {
        return destroy;
    }

    public void setDestroy(boolean destroy) {
        this.destroy = destroy;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(Method targetMethod) {
        this.targetMethod = targetMethod;
    }

    public Class<?> getParamType() {
        return paramType;
    }

    public void setParamType(Class<?> paramType) {
        this.paramType = paramType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
