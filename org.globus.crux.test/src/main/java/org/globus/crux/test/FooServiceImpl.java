package org.globus.crux.test;

import org.globus.crux.core.state.DestroyState;
import org.globus.crux.core.state.StatefulService;

@StatefulService(namespace = "http://foo.com", keyName = "FooKey", resourceName = "FooRP")
public class FooServiceImpl implements FooService {

    @DestroyState
	public void method(Object param) {
	}

}