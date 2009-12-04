package org.globus.crux.wsrf.lifetime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ResourceBundle;

import org.globus.crux.service.DestroyState;
import org.globus.crux.service.StatefulService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Quartz job responsible for destroying the resource of a stateful web service.
 *
 * @author Doreen Seider
 */
public class AnnotationDestroyerJob implements Job {

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("org.globus.crux.wsrf.wsrf");/*NON-NLS*/

    /**
     * Method called on execution time.
     */
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			Object target = context.getJobDetail().getJobDataMap().get(ResourceTerminationImpl.TARGET_KEY);
			getAnnotedDestroyMethod(target).invoke(target);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Looks up the method of the service implementation which is annotated with
	 * DestroyState and therefore the method to delegate the destroy call to.
	 *
	 * @param target The service implementation.
	 * @return The annotated method.
	 */
	private Method getAnnotedDestroyMethod(Object target) {

		Method destroyMethod = null;

        if (!target.getClass().isAnnotationPresent(StatefulService.class)) {
            String message = resourceBundle.getString("class.does.not.represent.a.resource");
            throw new IllegalArgumentException(message);
        }

        for (Method method : target.getClass().getMethods()) {
            if (method.isAnnotationPresent(DestroyState.class)) {
		if (destroyMethod == null) {
			destroyMethod = method;
		} else {
			throw new IllegalStateException("only one DestroyState annotated method is allowed");
		}
            }
        }

        if (destroyMethod == null) {
		throw new IllegalStateException("no DestroyState annotated method fonud");
        }

        return destroyMethod;
	}
}
