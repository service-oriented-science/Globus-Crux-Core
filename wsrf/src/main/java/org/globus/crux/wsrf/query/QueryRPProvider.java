package org.globus.crux.wsrf.query;

import java.util.ArrayList;
import java.util.List;

import org.globus.crux.OperationProvider;
import org.globus.crux.ProviderException;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryResourceProperties;

public class QueryRPProvider implements OperationProvider<QueryResourceProperties> {

	private DefaultQueryResourceProperties qrpImpl;
	private List<QueryEngine<Object, Object>> engines = new ArrayList<QueryEngine<Object, Object>>();

	public QueryResourceProperties getImplementation() throws ProviderException {

		if (qrpImpl == null && engines.size() == 0) {
			throw new ProviderException("At least one QueryEngine is required.");
        } else if (qrpImpl == null){
            qrpImpl = new DefaultQueryResourceProperties();
            qrpImpl.setEngines(engines);
        }
		return qrpImpl;
	}

	public Class<QueryResourceProperties> getInterface() {
		return QueryResourceProperties.class;
	}

    public QueryRPProvider addQueryEngine(QueryEngine<Object, Object> engine) {
        this.engines.add(engine);
        qrpImpl = null;
        return this;
    }

    public void setEngines(List<QueryEngine<Object, Object>> engines) {
        this.engines = engines;
        qrpImpl = null;
    }

}
