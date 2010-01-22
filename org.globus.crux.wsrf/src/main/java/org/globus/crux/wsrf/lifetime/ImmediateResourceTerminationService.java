package org.globus.crux.wsrf.lifetime;

import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourcelifetime_1_2_draft_01.ResourceNotDestroyedFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourcelifetime_1_2_draft_01.ResourceUnknownFault;

/**
 * @author Doreen Seider
 */
public interface ImmediateResourceTerminationService {

	void destroy(Object serviceImpl) throws ResourceNotDestroyedFault, ResourceUnknownFault;
}
			
			