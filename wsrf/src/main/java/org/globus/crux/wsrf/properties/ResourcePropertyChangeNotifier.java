package org.globus.crux.wsrf.properties;

import java.lang.reflect.Method;

import org.globus.crux.MethodCallWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for firing a notification if a resource property was changed.
 * Therefore this class implements the {@link MethodCallWrapper} interface in
 * order to be able to wrapp the call of the method which will change the
 * resource property.
 * 
 * @author Doreen Seider
 */
public class ResourcePropertyChangeNotifier implements MethodCallWrapper {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * Called before the method will be called which will change the resource property.
	 */
	public void doBefore(Object target, Method method) {
		logger.debug("here comes the actions after a resource property was changed");
	}

	/**
	 * Called after the method was called which will change the resource property.
	 */
	public void doAfter(Object target, Method method) {
		logger.debug("here comes the actions before a resource property will be changed");
	}
}
