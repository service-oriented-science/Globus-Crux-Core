package org.globus.crux.wsrf.properties.get;

import javax.xml.namespace.QName;

import org.globus.crux.core.OperationProvider;
import org.globus.crux.core.state.ResourcePropertySet;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.GetResourcePropertyResponse;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.InvalidResourcePropertyQNameFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.ResourceUnknownFault;

/**
* @author Doreen Seider
 */
public interface GetResourcePropertyService extends OperationProvider {
	
	GetResourcePropertyResponse getResourceProperty(ResourcePropertySet rpSet, QName getResourcePropertyRequest)
		throws InvalidResourcePropertyQNameFault, ResourceUnknownFault;
			
}
			
			