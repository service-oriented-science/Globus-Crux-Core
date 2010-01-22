package org.globus.crux.wsrf.lifetime;

import java.util.Calendar;

import javax.xml.ws.Holder;

import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourcelifetime_1_2_draft_01.ResourceUnknownFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourcelifetime_1_2_draft_01.TerminationTimeChangeRejectedFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourcelifetime_1_2_draft_01.UnableToSetTerminationTimeFault;

/**
* @author Doreen Seider
 */
public interface ScheduledResourceTerminationService {

    void setTerminationTime(Object serviceImpl,
    		Calendar requestedTerminationTime,
    		Holder<java.util.Calendar> newTerminationTime,
    		Holder<java.util.Calendar> currentTime)
    	throws TerminationTimeChangeRejectedFault, ResourceUnknownFault, UnableToSetTerminationTimeFault;
			
}
			
			