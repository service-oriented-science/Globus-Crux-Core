package org.globus.crux.test;

import org.globus.crux.wsrf.properties.get.GetResourcePropertyService;

public class TestServiceConsumer {
	
	public void setGetResourcePropertyService(GetResourcePropertyService service) {
		System.out.println(">>> service injected: " + service.toString());
	}

}
