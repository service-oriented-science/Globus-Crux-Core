package org.cyberaide.ws.agent;

import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFServlet;

public class AgentService{
	CXFServlet cxf = new CXFServlet(); 
	public AgentService(){
		Bus bus = cxf.getBus();
		BusFactory.setDefaultBus(bus);
		Endpoint.publish("/agent", new Agent());
	}
}
