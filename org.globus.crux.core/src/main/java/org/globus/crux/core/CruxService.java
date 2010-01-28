package org.globus.crux.core;

import java.util.Set;

import org.apache.cxf.interceptor.Interceptor;

/**
 * @author Doreen Seider
 */
public interface CruxService {

	public Object getServiceImpl();

	public Class getIface();
	
	public Set<Interceptor> getInterceptors();
	
	public String getServiceName();

	public String getEndpointName();

	public String getAddress();
	
}
