package org.globus.crux.wsrf.lifetime;

import java.util.Calendar;
import java.util.Date;

import javax.jws.WebParam;
import javax.xml.ws.Holder;

import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourcelifetime_1_2_draft_01.ImmediateResourceTermination;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourcelifetime_1_2_draft_01.ResourceNotDestroyedFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourcelifetime_1_2_draft_01.ResourceUnknownFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourcelifetime_1_2_draft_01.ScheduledResourceTermination;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourcelifetime_1_2_draft_01.SetTerminationTimeResponse;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourcelifetime_1_2_draft_01.TerminationTimeChangeRejectedFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourcelifetime_1_2_draft_01.UnableToSetTerminationTimeFault;
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

	private final String jobName;
	private final String triggerName;

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

		jobName = "org.globus.crux.wsrf.lifetime.job." + target.toString();
		
		jobDetail = new JobDetail(jobName, AnnotationDestroyerJob.class);
		jobDetail.getJobDataMap().put(TARGET_KEY, target);
		
		triggerName = "org.globus.crux.wsrf.lifetime.trigger." + target.toString();
	}

	public void destroy() throws ResourceUnknownFault, ResourceNotDestroyedFault {
		try {
			if (scheduler.getTrigger(triggerName, "DEFAULT") == null) {
				scheduler.scheduleJob(jobDetail, new SimpleTrigger(triggerName, new Date()));
			} else {
				throw new IllegalStateException("resource is allready being destroyed");
			}
		} catch (SchedulerException e) {
			throw new ResourceNotDestroyedFault();
		}
	}

    public void setTerminationTime(@WebParam(name = "RequestedTerminationTime",
            targetNamespace = "http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd") Calendar requestedTerminationTime, @WebParam(
            mode = WebParam.Mode.OUT, name = "NewTerminationTime",
            targetNamespace = "http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd") Holder<Calendar> newTerminationTime, @WebParam(
            mode = WebParam.Mode.OUT, name = "CurrentTime",
            targetNamespace = "http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd") Holder<Calendar> currentTime) throws UnableToSetTerminationTimeFault, TerminationTimeChangeRejectedFault, ResourceUnknownFault {

		try {
			Trigger trigger = scheduler.getTrigger(triggerName, "DEFAULT");
			if (trigger != null) {
				scheduler.rescheduleJob(triggerName, "DEFAULT", trigger);
			} else {
		    	trigger = new SimpleTrigger(triggerName, new Date(requestedTerminationTime.getTimeInMillis()));
				scheduler.scheduleJob(jobDetail, trigger);
			}
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
        SetTerminationTimeResponse response = new SetTerminationTimeResponse();
        response.setCurrentTime(Calendar.getInstance());
        response.setNewTerminationTime(requestedTerminationTime);
    }
}
