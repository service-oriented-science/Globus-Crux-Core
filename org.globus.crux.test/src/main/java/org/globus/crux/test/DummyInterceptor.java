package org.globus.crux.test;

import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;

public class DummyInterceptor implements Interceptor {

	public void handleFault(Message message) {
	}

	public void handleMessage(Message arg0) {
	}

}
