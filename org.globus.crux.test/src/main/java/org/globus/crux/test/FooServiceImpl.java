package org.globus.crux.test;

import org.globus.crux.core.CruxService;
import org.globus.crux.core.state.DestroyState;
import org.globus.crux.core.state.StatefulService;

@StatefulService(namespace = "http://foo.com", keyName = "FooKey", resourceName = "FooRP")
public class FooServiceImpl implements FooService, CruxService {

	public Class getInterface() {
		return FooService.class;
	}

    @DestroyState
	public void method(Object param) {
		System.out.println(">>> method of test service called");
	}

}