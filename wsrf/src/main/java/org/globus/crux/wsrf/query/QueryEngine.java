package org.globus.crux.wsrf.query;

import org.oasis.wsrf.properties.QueryEvaluationErrorFault;

import java.util.List;

/**
 * @author turtlebender
 */
public interface QueryEngine<T, V> {
    boolean canProcessDialect(String dialect);
    List<T> executeQuery(V query) throws QueryEvaluationErrorFault;
    Class<V> getQueryType();
}
