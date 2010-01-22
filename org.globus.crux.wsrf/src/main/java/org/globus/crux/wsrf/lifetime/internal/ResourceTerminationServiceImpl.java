package org.globus.crux.wsrf.lifetime.internal;

import java.util.Calendar;
import java.util.Date;

import javax.xml.ws.Holder;

import org.globus.crux.wsrf.lifetime.ImmediateResourceTerminationService;
import org.globus.crux.wsrf.lifetime.ScheduledResourceTerminationService;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourcelifetime_1_2_draft_01.ResourceNotDestroyedFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourcelifetime_1_2_draft_01.ResourceUnknownFault;
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
 * @author Doreen Seider
 */
public class ResourceTerminationServiceImpl
	implements ImmediateResourceTerminationService, ScheduledResourceTerminationService {

	public static final String SERVICE_IMPL_KEY = "org.globus.crux.wsrf.lifetime.serviceimpl";

	private final String jobName = "org.globus.crux.wsrf.lifetime.job.";
	private final String triggerName = "org.globus.crux.wsrf.lifetime.trigger.";

	private Scheduler scheduler;

	public ResourceTerminationServiceImpl() {
		SchedulerFactory schedulerFac = new StdSchedulerFactory();
		try {
			scheduler = schedulerFac.getScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public void destroy(Object serviceImpl) throws ResourceUnknownFault, ResourceNotDestroyedFault {
		
		JobDetail jobDetail = new JobDetail(jobName + serviceImpl.hashCode(), AnnotationDestroyerJob.class);
		jobDetail.getJobDataMap().put(SERVICE_IMPL_KEY, serviceImpl);
		
		try {
			if (scheduler.getTrigger(triggerName + serviceImpl.hashCode(), "DEFAULT") == null) {
				scheduler.scheduleJob(jobDetail, new SimpleTrigger(triggerName + serviceImpl.hashCode(), new Date()));
			} else {
				throw new IllegalStateException("resource is allready being destroyed");
			}
		} catch (SchedulerException e) {
			throw new ResourceNotDestroyedFault();
		}
	}

    public void setTerminationTime(Object serviceImpl,
    		Calendar requestedTerminationTime,
    		Holder<Calendar> newTerminationTime,
    		Holder<Calendar> currentTime)
    	throws UnableToSetTerminationTimeFault, TerminationTimeChangeRejectedFault, ResourceUnknownFault {
		
		try {
			Trigger trigger = scheduler.getTrigger(triggerName, "DEFAULT");
			if (trigger != null) {
				scheduler.rescheduleJob(triggerName + serviceImpl.hashCode(), "DEFAULT", trigger);
			} else {
		    	trigger = new SimpleTrigger(triggerName + serviceImpl.hashCode(), new Date(requestedTerminationTime.getTimeInMillis()));
		    	JobDetail jobDetail = new JobDetail(jobName + serviceImpl.hashCode(), AnnotationDestroyerJob.class);
				jobDetail.getJobDataMap().put(SERVICE_IMPL_KEY, serviceImpl);
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
