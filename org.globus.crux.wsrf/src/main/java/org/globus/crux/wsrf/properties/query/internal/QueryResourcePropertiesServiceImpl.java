package org.globus.crux.wsrf.properties.query.internal;

import java.util.ArrayList;
import java.util.List;

import org.globus.crux.wsrf.properties.query.QueryEngine;
import org.globus.crux.wsrf.properties.query.QueryResourcePropertiesService;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.InvalidQueryExpressionFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.InvalidResourcePropertyQNameFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryEvaluationErrorFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryExpressionType;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryResourcePropertiesResponse;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryResourceProperties_Type;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.ResourceUnknownFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.UnknownQueryExpressionDialectFault;

/**
 * @author Doreen Seier
 */
public class QueryResourcePropertiesServiceImpl implements QueryResourcePropertiesService {

	 private List<QueryEngine<Object, Object>> queryEngines = new ArrayList<QueryEngine<Object, Object>>();
	 
    public QueryResourcePropertiesResponse queryResourceProperties(QueryResourceProperties_Type queryResourcePropertiesRequest)
            throws UnknownQueryExpressionDialectFault, InvalidResourcePropertyQNameFault, QueryEvaluationErrorFault,
            InvalidQueryExpressionFault, ResourceUnknownFault {
        String dialect = queryResourcePropertiesRequest.getQueryExpression().getDialect();
        QueryEngine<Object, Object> executor = null;
        for (QueryEngine<Object, Object> engine : queryEngines) {
            if (engine.canProcessDialect(dialect)) {
                executor = engine;
                break;
            }
        }
        if (executor == null) {
            throw new UnknownQueryExpressionDialectFault("Not Query Engine can process query of dialect: " + dialect);
        }
        QueryResourcePropertiesResponse response = new QueryResourcePropertiesResponse();
        List<Object> results = response.getContent();
        QueryExpressionType queryType = queryResourcePropertiesRequest.getQueryExpression();
        Object query = queryType.getContent().get(0);
        if(query == null){
            throw new InvalidQueryExpressionFault("No query supplied");
        }
        if (executor.getQueryType().isAssignableFrom(query.getClass())) {
            results.addAll(executor.executeQuery(query));
        } else {
            throw new InvalidQueryExpressionFault("The supplied query is not valid for the specified dialect: " + dialect);
        }
        return response;
    }
    
    public void setQueryEngines(List<QueryEngine<Object, Object>> queryEngines) {
        this.queryEngines = queryEngines;
    }
    
}
