package org.globus.crux;

import java.lang.annotation.Annotation;

/**
 * Interface defining a wrapper for a method. It provides a method to call before
 * the appropriate annotated method will be called and one to call after the method was called.
 * 
 * @author Doreen Seider
 */
public interface MethodCallWrapper {

	/**
	 * Returns the associated annotation. Any method with that given
	 * annotation will be wrapped by this method call wrapper.
	 * @return the associated annotation class.
	 */
	Class getAssociatedAnnotation();
	
	/**
	 * Method which will be called before the method will be called.
	 * @param annotation The {@link Annotation} of the method to wrap.
	 */
	Object doBefore(Annotation annotation);
	
	/**
	 * Method which will be called after the method was called.
	 * @param annotation The {@link Annotation} of the method to wrap.
	 */
	void doAfter(Annotation annotation, Object doBeforeResult);
}
