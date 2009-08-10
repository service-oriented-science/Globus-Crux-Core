package org.globus.crux.wsrf.query;

import org.oasis.wsrf.properties.QueryResourceProperties;
import org.oasis.wsrf.properties.QueryResourcePropertiesResponse;
import org.oasis.wsrf.properties.QueryResourceProperties_Type;
import org.oasis.wsrf.properties.UnknownQueryExpressionDialectFault;
import org.oasis.wsrf.properties.InvalidResourcePropertyQNameFault;
import org.oasis.wsrf.properties.QueryEvaluationErrorFault;
import org.oasis.wsrf.properties.InvalidQueryExpressionFault;
import org.oasis.wsrf.properties.ResourceUnknownFault;
import org.oasis.wsrf.properties.QueryExpressionType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author turtlebender
 */
public class DefaultQueryResourceProperties implements QueryResourceProperties {
    private List<QueryEngine<Object, Object>> engines = new ArrayList<QueryEngine<Object, Object>>();

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
            results.addAll(executor.executeQuery(query));
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
}
