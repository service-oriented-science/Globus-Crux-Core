package org.globus.crux.test;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;

public class DummyInterceptor implements Interceptor {

	public void handleMessage(SoapMessage message) throws Fault {
	}

	public void handleFault(Message arg0) {
	}

	public void handleMessage(Message arg0) throws Fault {
	}

}
