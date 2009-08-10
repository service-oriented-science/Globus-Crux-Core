package org.globus.crux.wsrf.query;

import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.InvalidQueryExpressionFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.InvalidResourcePropertyQNameFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryEvaluationErrorFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryResourceProperties;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryResourcePropertiesResponse;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryResourceProperties_Type;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.ResourceUnknownFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.UnknownQueryExpressionDialectFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryExpressionType;
import org.globus.crux.wsrf.properties.ResourcePropertySet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author turtlebender
 */
public class DefaultQueryResourceProperties implements QueryResourceProperties {
    private List<QueryEngine<Object, Object>> engines = new ArrayList<QueryEngine<Object, Object>>();
    private ResourcePropertySet rps;

    public QueryResourcePropertiesResponse queryResourceProperties(QueryResourceProperties_Type queryResourcePropertiesRequest)
            throws UnknownQueryExpressionDialectFault, InvalidResourcePropertyQNameFault, QueryEvaluationErrorFault,
            InvalidQueryExpressionFault, ResourceUnknownFault {
        String dialect = queryResourcePropertiesRequest.getQueryExpression().getDialect();
        QueryEngine<Object, Object> executor = null;
        for (QueryEngine<Object, Object> engine : engines) {
            if (engine.canProcessDialect(dialect)) {
                executor = engine;
                break;
            }
        }
        if (executor == null) {
            throw new UnknownQueryExpressionDialectFault("No Query Engine can process query of dialect: " + dialect);
        }
        QueryResourcePropertiesResponse response = new QueryResourcePropertiesResponse();
        List<Object> results = response.getContent();
        QueryExpressionType queryType = queryResourcePropertiesRequest.getQueryExpression();
        Object query = queryType.getContent().get(0);
        if(query == null){
            throw new InvalidQueryExpressionFault("No query supplied");
        }
        if (executor.getQueryType().isAssignableFrom(query.getClass())) {
            results.addAll(executor.executeQuery(query, rps));
        } else {
            throw new InvalidQueryExpressionFault("The supplied query is not valid for the specified dialect: " + dialect);
        }
        return response;
    }

    public DefaultQueryResourceProperties addQueryEngine(QueryEngine<Object, Object> engine) {
        this.engines.add(engine);
        return this;
    }

    public void setEngines(List<QueryEngine<Object, Object>> engines) {
        this.engines = engines;
    }

    public void setRps(ResourcePropertySet rps) {
        this.rps = rps;
    }
}
