package org.globus.crux.wsrf.query;

import java.util.ArrayList;
import java.util.List;

import org.globus.crux.OperationProvider;
import org.globus.crux.ProviderException;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryResourceProperties;

public class QueryRPProvider implements OperationProvider<QueryResourceProperties> {

	private DefaultQueryResourceProperties rpImpl;
	private List<QueryEngine<Object, Object>> engines = new ArrayList<QueryEngine<Object, Object>>();

	public QueryResourceProperties getImplementation() throws ProviderException {

		if (rpImpl == null && engines.size() == 0) {
			throw new ProviderException("At least one QueryEngine is required.");
        } else if (rpImpl == null){
            rpImpl = new DefaultQueryResourceProperties();
            rpImpl.setEngines(engines);
        }
		return rpImpl;
	}

	public Class<QueryResourceProperties> getInterface() {
		return QueryResourceProperties.class;
	}

    public QueryRPProvider addQueryEngine(QueryEngine<Object, Object> engine) {
        this.engines.add(engine);
        return this;
    }

    public void setEngines(List<QueryEngine<Object, Object>> engines) {
        this.engines = engines;
    }

}