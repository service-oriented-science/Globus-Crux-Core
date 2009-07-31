package org.cyberaide.ws.mediator;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
//import javax.xml.ws.Endpoint;//conflict and need to be removed

import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.WSConstants;

import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.jaxws.EndpointImpl;

import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;

public class MediatorService{  
  public static void startServer(String endp){
    EndpointImpl jaxWsEndpoint = (EndpointImpl) javax.xml.ws.Endpoint.publish(
  	                endp, new CogMediator());
  	Endpoint cxfEndpoint = jaxWsEndpoint.getServer().getEndpoint();
  	
  	Map<String,Object> inProps= new HashMap<String,Object>();
    
  	inProps.put(WSHandlerConstants.ACTION, 
		        WSHandlerConstants.USERNAME_TOKEN + " " + WSHandlerConstants.TIMESTAMP);
  	// Password type : plain text
  	//inProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
  	// for hashed password use:
  	inProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST);
  	// Callback used to retrieve password for given user.
  	inProps.put(WSHandlerConstants.PW_CALLBACK_CLASS, 
  	                MediatorServicePasswdCallback.class.getName());
  	
  	WSS4JInInterceptor wssIn = new WSS4JInInterceptor(inProps);
    
    //used to monitor the soap message as a debug assist or log
    cxfEndpoint.getInInterceptors().add(new MediatorServiceMsgMon());
    cxfEndpoint.getInInterceptors().add(wssIn);
  }
  
  public static void main(String[] args){
    if(args.length != 1) {
        System.err.println("Please specify server endpoint URL");
        System.exit(1);
    } else {
        String endp = args[0];
        System.out.println("Server is being started......");
        startServer(endp);
    }
  }
}
