package org.globus.crux.wsrf.properties.query;

import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryEvaluationErrorFault;

import java.util.List;

/**
 * @author turtlebender
 */
public interface QueryEngine<T, V> {
    boolean canProcessDialect(String dialect);
    List<T> executeQuery(V query) throws QueryEvaluationErrorFault;
    Class<V> getQueryType();
}
