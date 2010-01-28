package org.globus.crux.core;

import java.util.Set;

import org.apache.cxf.interceptor.Interceptor;

/**
 * @author Doreen Seider
 */
public class CruxServiceImpl implements CruxService {

	// defaults can be set here
	private Object serviceImpl;
	private Class iface;
	private Set<Interceptor> interceptors;
	private String serviceName;
	private String endpointName;
	private String address;
	
	public Object getServiceImpl() {
		return serviceImpl;
	}

	public void setServiceImpl(Object serviceImpl) {
		this.serviceImpl = serviceImpl;
	}

	public Class getIface() {
		return iface;
	}

	public void setIface(Class iface) {
		this.iface = iface;
	}
	
	public Set<Interceptor> getInterceptors() {
		return interceptors;
	}

	public void setInterceptors(Set<Interceptor> interceptors) {
		this.interceptors = interceptors;
	}
	
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getEndpointName() {
		return serviceName;
	}

	public void setEndpointName(String endpointName) {
		this.endpointName = endpointName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
