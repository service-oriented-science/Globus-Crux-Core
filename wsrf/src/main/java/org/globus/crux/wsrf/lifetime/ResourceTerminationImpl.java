package org.globus.crux.wsrf.lifetime;

import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.oasis_open.docs.wsrf.rl_2.SetTerminationTime;
import org.oasis_open.docs.wsrf.rl_2.SetTerminationTimeResponse;
import org.oasis_open.docs.wsrf.rlw_2.ImmediateResourceTermination;
import org.oasis_open.docs.wsrf.rlw_2.ResourceNotDestroyedFault;
import org.oasis_open.docs.wsrf.rlw_2.ScheduledResourceTermination;
import org.oasis_open.docs.wsrf.rlw_2.TerminationTimeChangeRejectedFault;
import org.oasis_open.docs.wsrf.rlw_2.UnableToSetTerminationTimeFault;
import org.oasis_open.docs.wsrf.rw_2.ResourceUnavailableFault;
import org.oasis_open.docs.wsrf.rw_2.ResourceUnknownFault;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Implementation of {@link ImmediateResourceTermination} and {@link ScheduledResourceTermination}.
 *
 * @author Doreen Seider
 */
public class ResourceTerminationImpl implements ImmediateResourceTermination, ScheduledResourceTermination {

	public static final String TARGET_KEY = "target";

	private static final String jobName = "org.globus.crux.wsrf.lifetime.job";

	private Scheduler scheduler;
	private JobDetail jobDetail;

	/**
	 * Constructor setting up the scheduler context.
	 *
	 * @param target The service implementation.
	 */
	public ResourceTerminationImpl(Object target) {
		SchedulerFactory schedulerFac = new StdSchedulerFactory();
		try {
			scheduler = schedulerFac.getScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}

		jobDetail = new JobDetail(jobName, AnnotationDestroyerJob.class);
		jobDetail.getJobDataMap().put(TARGET_KEY, target);
	}

	public void destroy() throws ResourceUnknownFault, ResourceUnavailableFault, ResourceNotDestroyedFault {
		scheduleJob(new SimpleTrigger("org.globus.crux.wsrf.lifetime.trigger.immediate", new Date()));
	}

	public SetTerminationTimeResponse setTerminationTime(SetTerminationTime setTerminationTimeRequest)
			throws ResourceUnknownFault, UnableToSetTerminationTimeFault,
			TerminationTimeChangeRejectedFault, ResourceUnavailableFault {

		XMLGregorianCalendar xmlTerminationTime = setTerminationTimeRequest.getRequestedTerminationTime().getValue();
		Calendar terminationTime = Calendar.getInstance();
		terminationTime.set(xmlTerminationTime.getYear(),
				xmlTerminationTime.getMonth(),
				xmlTerminationTime.getDay(),
				xmlTerminationTime.getHour(),
				xmlTerminationTime.getMinute(),
				xmlTerminationTime.getSecond());

		String triggerName = "org.globus.crux.wsrf.lifetime.trigger.scheduled";
		try {
			if (scheduler.getJobNames(triggerName).length != 0) {
				scheduler.unscheduleJob(jobName, triggerName);
			}
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
		scheduleJob(new SimpleTrigger(triggerName, new Date(terminationTime.getTimeInMillis())));
		// FIXME create and return response object
		return null;
	}

	/**
	 * Helper method used for scheduling a job.
	 *
	 * @param trigger The trigger to use when scheduling.
	 */
	private void scheduleJob(Trigger trigger) {
		try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}
}
