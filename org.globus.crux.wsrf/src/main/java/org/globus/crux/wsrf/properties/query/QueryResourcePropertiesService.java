package org.globus.crux.wsrf.properties.query;

import org.globus.crux.core.OperationProvider;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.InvalidQueryExpressionFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.InvalidResourcePropertyQNameFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryEvaluationErrorFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryResourcePropertiesResponse;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryResourceProperties_Type;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.ResourceUnknownFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.UnknownQueryExpressionDialectFault;

/**
 * @author Doreen Seider
 */
public interface QueryResourcePropertiesService extends OperationProvider {

    QueryResourcePropertiesResponse queryResourceProperties(QueryResourceProperties_Type queryResourcePropertiesRequest)
    	throws UnknownQueryExpressionDialectFault, InvalidResourcePropertyQNameFault, QueryEvaluationErrorFault, InvalidQueryExpressionFault, ResourceUnknownFault;

}
