package org.globus.crux;

import java.lang.reflect.Method;

/**
 * Interface defining a wrapper for a method. It provides a method to call before
 * the method will be called and one to call after the method was called.
 * 
 * @author Doreen Seider
 */
public interface MethodCallWrapper {

	/**
	 * Method which will be called before the method will be called.
	 */
	void doBefore(Object target, Method method);
	
	/**
	 * Method which will be called after the method was called.
	 */
	void doAfter(Object target, Method method);
}
