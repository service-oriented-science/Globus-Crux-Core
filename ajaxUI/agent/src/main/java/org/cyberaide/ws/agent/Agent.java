/*
 * Agent.java
 * 
 * @version:
 *     $Id v1.0$
 *
 */
 
package org.cyberaide.ws.agent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.WSConstants;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;

import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;

import org.apache.log4j.Logger;

import org.cyberaide.ws.mediator.*;

/**
 * User agent to act on behalf of users. 
 * Functionalities are exposed as web services
 *
 */
@WebService
@SOAPBinding(style=Style.DOCUMENT, use=SOAPBinding.Use.LITERAL, parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
public class Agent {
    
	CogMediatorService service;
	IMediator mediator;
	Client client;
	Endpoint cxfEndpoint;
	Random tokenGenerator;
	static final Map<Integer, String> securityTokens = new HashMap<Integer, String>();
	static Logger log = Logger.getLogger(Agent.class);
	
	static String tgnewsfeeds = "http://news.teragrid.org/feeds/";
	static String[] feedsProviders = 
		{"indiana", "lsu", "ncar","ncsa","nics","ornl","psc","purdue","sdsc","tacc","anl"};
	static String tginfokit = "http://info.teragrid.org/web-apps/csv/kit-services-v1/";
	static final long PERIOD = 10 * 60 * 1000;//10 min
	static PeriodicTask pt = new PeriodicTask();
	static Timer timer = new Timer();
	
	static{
		timer.scheduleAtFixedRate(pt, 0, PERIOD);
	}
		
	/**
     * Construct a new user agent and setup the interaction with the mediator
     *
     */
	public Agent(){
		//log.info("Agent constructor entering...");
		service = new CogMediatorService();
		mediator = service.getCogMediatorPort();
		tokenGenerator = new Random();
		//getTeragridInfo();
		//timer.scheduleAtFixedRate(pt, 0, PERIOD);
		/*
		//securityTokens = new HashMap<Integer, String>();
		if(mediator instanceof org.apache.cxf.frontend.ClientProxy){
			log.info("I'm clientproxy");
		} else {
			log.info("I'm not clientproxy");
		}
		log.info("before client proxy construction");
		log.info(java.lang.reflect.Proxy.getInvocationHandler(mediator).getClass().getName());
		*/
		try{
		  	client = ClientProxy.getClient(mediator);
		  	cxfEndpoint = client.getEndpoint();
	  	} catch (Exception e) {
	  		log.info("----------------->" + e.getMessage());
	  	}
	  	log.info("after client proxy and endpoint construction");
	  	
	  	Map<String,Object> outProps = new HashMap<String,Object>();
	  	
	  	outProps.put(WSHandlerConstants.ACTION, 
		        WSHandlerConstants.USERNAME_TOKEN + " " + WSHandlerConstants.TIMESTAMP);
	  	// Specify the username
	  	//System.out.print("Username: ");
	  	//Scanner sc = new Scanner(System.in);
		//String user = sc.nextLine();
		String user = "grid";
	  	outProps.put(WSHandlerConstants.USER, user);
	  	// Password type : plain text
	  	//outProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
	  	// for hashed password use:
	  	outProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST);
		// How long ( in seconds ) message is valid since send
		outProps.put(WSHandlerConstants.TTL_TIMESTAMP, "30");
		// if you want to use millisecond precision use this
		//outProps.put(WSHandlerConstants.TIMESTAMP_PRECISION, “true”);
	  	// Callback used to retrieve password for given user.
	  	outProps.put(WSHandlerConstants.PW_CALLBACK_CLASS, 
	  	                AgentPasswdCallback.class.getName());
	  	
	  	WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(outProps);
	  	cxfEndpoint.getOutInterceptors().add(wssOut);
	  	log.info("leaving agent constructor");
	  	
	}
	
  /**
   * Used to test the communication between agent and mediator
   * 
   */
  public static void main(String[] args)
  {
  	Agent agent = new Agent();
	if(agent.mediator != null){
		boolean login = agent.mediator.retrieveCertificate("myproxy.teragrid.org", 7512, "quakesim", "VOID", 0);
		if(login){
			System.out.println("login successfully!");
		} else {
			System.out.println("failed to login!");
		}
	} else {
		System.out.println("mediator is null!");
	}
  }
  
	/**
     * randomly generate security token (as a session id) (1 ~ 2^31)
     * 
	 * @return the randomly generated token
     */
	private int tokenGen(){
		int id;
		synchronized(securityTokens){
			id = tokenGenerator.nextInt(Integer.MAX_VALUE);
			while(securityTokens.containsKey(id)){
				id = tokenGenerator.nextInt(Integer.MAX_VALUE);
			}
		}
	    return id;
	}
	
	private static boolean insession(String user, int token){
		boolean ret;
		synchronized(securityTokens){
			ret = (securityTokens.containsKey(token) 
				&& securityTokens.get(token).equals(user));
		}
		return ret;
	}
	
	/**
	 * list files of the specified directory
	 * 
	 * @param user the authenticated user
	 * @param token the user's token
     * @param dir the relative dir, from the user's home
	 * 
	 * @return serialized filelist
	 */
	@WebMethod(action = "listDir")
	public String listDir(@WebParam(name = "user") String user, 
						@WebParam(name = "token") int token, 
						@WebParam(name = "dir") String dir) {
	  	String ret = null;
		if(insession(user, token)){
			ret = mediator.listDir(user, dir);
		}
		return ret;
	}
  
	/**
     * retrieve a proxy credential from a myproxy server
     *
     * @param host myproxy server host
     * @param port myproxy server's port
     * @param user username
     * @param password passphrase to login to the myproxy server
	 * @param lifetime the lifetime of the proxy credential (in seconds)
	 *
	 * @return a non-negative Integer as security token (session id), zero for fail
     */
	@WebMethod(action="retrieveCertificate")
	public int retrieveCertificate(@WebParam(name = "host") String host,
										@WebParam(name = "port") int port,
										@WebParam(name = "user") String user,
										@WebParam(name = "password") String password,
										@WebParam(name = "lifetime") int lifetime){
										
		log.info("I'm running from inside Agent");
		log.info("host: " + host);
		log.info("port: " + port);
		log.info("user: " + user);
		log.info("password: " + password);
		log.info("lifetime: " + lifetime);
	
		boolean login = false;
		try{
			login = mediator.retrieveCertificate(host, port, user, password, lifetime);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
		}
		int token = 0;
		if(login){
			token = tokenGen();
			securityTokens.put(token, user);
		}
		return token;
	}
	
	/**
     * user submit a workflow to execute
     *
     * @param user the user's login name
     * @param proj the description of the workflow
     * @param token the security token
	 *
	 * @return the randomly assigned workflow id, zero for not accepted submission
     */
	@WebMethod(action="exec")
	public int exec(@WebParam(name = "user") String user, 
					@WebParam(name = "token") int token, 
					@WebParam(name = "proj") String proj){
		int ret = 0;
		log.info("user: " + user);
		log.info("proj to be submitted: " + proj);
		if(insession(user, token)){
			try{
				ret = mediator.exec(user,proj);
			} catch (Throwable t) {
				log.error(t.getMessage(), t);
			}
		}
		return ret;
	}
	
	/**
	 * third party file transfer
	 * 
	 * @param from the source URI
	 * @param to the destination URI
	 * 
	 */
	@WebMethod(action="transfer")
	public String transfer(@WebParam(name = "user") String user, 
							@WebParam(name = "token") int token, 
							@WebParam(name = "from") String from, 
							@WebParam(name = "to") String to){
		String ret = null;
		if(insession(user, token)){
			ret = mediator.transfer(user, from, to);
		}
		return ret;
	}
  
	/**
     * user query the execution status by workflow id
     *
     * @param user the user's login name
     * @param Wfid the workflow id
     * @param token the security token
	 *
	 * @return a string combines the Wfid and status code (the job # finished)
     */
	@WebMethod(action="statusQuery")
	public String statusQuery(@WebParam(name = "user") String user, 
								@WebParam(name = "token") int token, 
								@WebParam(name = "wfid") int Wfid){
		String ret = null;
		if(insession(user, token)){
			ret = mediator.statusQuery(Wfid);
		}
		return ret;
	}
	
	/**
     * list all the submitted workflow ids of the user
     *
     * @param user the user's login name
     * @param token the security token
	 *
	 * @return a string containing all the workflow ids that user submitted
     */	
	@WebMethod(action="listExecIds")
	public String listExecIds(@WebParam(name = "user") String user, 
								@WebParam(name = "token") int token){
		String ret = null;
		if(insession(user, token)){
			ret = mediator.listExecIds(user);
		} else {
			ret = "Not in session";
		}
		return ret;
	}
	
	/**
     * list all the output filenames for the specified workflow
     *
     * @param user the user's login name
     * @param Wfid the workflow id
     * @param token the security token
	 *
	 * @return a string containing all the output filenames of the workflow
     */	
	@WebMethod(action="listOutput")
	public String listOutput(@WebParam(name = "user") String user, 
								@WebParam(name = "token") int token, 
								@WebParam(name = "wfid") int Wfid){
		String ret = null;
		if(insession(user, token)){
			ret = mediator.listOutput(user, Wfid);
		}
		return ret;
	}
	
	/**
     * display content of an output file
     *
     * @param user the user's login name
     * @param Wfid the workflow id
     * @param outputfile the output filename
     * @param token the security token
	 *
	 * @return the content of the output file
     */	
	@WebMethod(action="fetchOutput")
	public String fetchOutput(@WebParam(name = "user") String user, 
								@WebParam(name = "token") int token, 
								@WebParam(name = "wfid") int Wfid, 
								@WebParam(name = "outputfile") String outputfile){
		String ret = null;
		if(insession(user, token)){
			ret = mediator.fetchOutput(user, Wfid, outputfile);
		}
		return ret;
	}
	
	/**
     * list all the public and/or the CogQueues that the user participated
	 *
     * @param user the user's login name
     * @param token the security token
     *
	 * @return serialized queue ids
     */
	@WebMethod(action="listQueues")
	public String listQueues(@WebParam(name = "user") String user, 
							@WebParam(name = "token") int token){
		String ret = null;
		if(insession(user, token)){
			//TODO
		}
		return ret;
	}
	
	/**
     * list participants of the specified CogQueue
     *
     * @param user the user's login name
     * @param queueId the queue id
     * @param token the security token
	 *
	 * @return serialized participants list
     */
	@WebMethod(action="listParticipants")
	public String listParticipants(@WebParam(name = "user") String user, 
									@WebParam(name = "token") int token, 
									@WebParam(name = "queueId") int queueId){
		String ret = null;
		if(insession(user, token)){
			//TODO
		}
		return ret;
	}
	
	/**
     * list all workflows in a CogQueue
     *
     * @param user the user's login name
     * @param queueId the queue to be queried in
     * @param token the security token
	 *
	 * @return serialized workflow ids
     */
	@WebMethod(action="listWf")
	public String listWf(@WebParam(name = "user") String user, 
							@WebParam(name = "token") int token, 
							@WebParam(name = "queueId") int queueId){
		String ret = null;
		if(insession(user, token)){
			//TODO
		}
		return ret;
	}
		
	/**
     * user upload shared workflows into CogQueue zone
     *
     * @param user the user's login name
     * @param aProj the description of the uploaded workflow
	 * @param queueId the queue in which the workflow to be added in. 0 for a new queue
	 * @param Wfid the workflow id if it's updating the workflow. 0 for a new workflow
     * @param token the security token
	 *
	 * @return the workflow id if successfully uploaded, 0 or negative number (as error code) if failed.
     */
	@WebMethod(action="upload")
	public int upload(@WebParam(name = "user") String user, 
						@WebParam(name = "token") int token, 
						@WebParam(name = "aProj") String aProj, 
						@WebParam(name = "queueId") int queueId, 
						@WebParam(name = "Wfid") int Wfid){
		int ret = 0;
		if(insession(user, token)){
			//TODO
		}
		return ret;
	}
	
	/**
     * download the a workflow from the CogQueue zone
     *
     * @param user the user's login name
     * @param queueId the queue id
     * @param Wfid the workflow id
     * @param token the security token
	 *
	 * @return the serialized workflow, including type and description
     */
	@WebMethod(action="download")
	public String download(@WebParam(name = "user") String user, 
							@WebParam(name = "token") int token, 
							@WebParam(name = "queueId") int queueId, 
							@WebParam(name = "Wfid") int Wfid){
		String ret = null;
		if(insession(user, token)){
			//TODO
		}
		return ret;
	}
	
	/**
     * user remove a workflow from a CogQueue
     *
     * @param user the user's login name
     * @param queueId the queue id
     * @param Wfid the workflow id
     * @param token the security token
	 *
	 * @return the workflow id if removed successfully, 0 or negative number (as error code) if failed.
     */
	@WebMethod(action="remove")
	public int remove(@WebParam(name = "user") String user, 
						@WebParam(name = "token") int token, 
						@WebParam(name = "queueId") int queueId, 
						@WebParam(name = "Wfid") int Wfid){
		int ret = 0;
		if(insession(user, token)){
			//TODO
		}
		return ret;
	}
	
	/**
     * list workflows owned by a specified user in a CogQueue
     *
     * @param user the user's login name
     * @param queueId the queue to be queried in
     * @param token the security token
	 *
	 * @return the workflow ids owned by the specified user from the specified queue
     */
	@WebMethod(action="listWfByUser")
	public String listWfByUser(@WebParam(name = "user") String user, 
							@WebParam(name = "token") int token, 
							@WebParam(name = "queueId") int queueId){
		String ret = null;
		if(insession(user, token)){
			//TODO
		}
		return ret;
	}
	
	/**
     * list workflows of a specified type in a CogQueue
     *
     * @param user the user's login name
     * @param queueId the queue to be queried in
     * @param theType specify workflow type
     * @param token the security token
	 *
	 * @return the workflow ids with the specified type from the specified queue
     */
	@WebMethod(action="listWfByType")
	public String listWfByType(@WebParam(name = "user") String user, 
							@WebParam(name = "token") int token, 
							@WebParam(name = "queueId") int queueId, 
							@WebParam(name = "theType") WfType theType){
		String ret = null;
		if(insession(user, token)){
			//TODO
		}
		return ret;
	}
	
	/**
     * grant access to the users in the list
     *
     * @param user the user's login name
     * @param queueId the queue to be queried in
     * @param userlist serialized userlist
     * @param token the security token
	 *
	 * @return the queue id if grant sccessfully, 0 or negative number (as error code) if failed.
     */
	@WebMethod(action="grantAccess")
	public int grantAccess(@WebParam(name = "user") String user, 
							@WebParam(name = "token") int token, 
							@WebParam(name = "queueId") int queueId, 
							@WebParam(name = "userlist") String userlist){
		int ret = 0;
		if(insession(user, token)){
			//TODO
		}
		return ret;
	}
	
	/**
     * check whether the workflow is being edited (locked) by someone else
     *
     * @param user the user's login name
     * @param queueId the queue to be queried in
     * @param Wfid the workflow id to be checked
     * @param token the security token
	 *
	 * @return the user that is editing the workflow, null if none is editing the workflow
     */
	@WebMethod(action="checkLock")
	public String checkLock(@WebParam(name = "user") String user, 
							@WebParam(name = "token") int token, 
							@WebParam(name = "queueId") int queueId, 
							@WebParam(name = "Wfid") int Wfid){
		String ret = null;
		if(insession(user, token)){
			//TODO
		}
		return ret;
	}
	
	/**
     * obtain the write token and lock the workflow
     *
     * @param user the user's login name
     * @param queueId the queue to be queried in
     * @param Wfid the workflow id to be checked
     * @param token the security token
	 *
	 * @return the workflow id that locked, 0 or negative number (as error code) if failed.
     */
	@WebMethod(action="obtainLock")
	public int obtainLock(@WebParam(name = "user") String user, 
						@WebParam(name = "token") int token, 
						@WebParam(name = "queueId") int queueId, 
						@WebParam(name = "Wfid") int Wfid){
		int ret = 0;
		if(insession(user, token)){
			//TODO
		}
		return ret;
	}
	
	static private class PeriodicTask extends TimerTask{
		public void run(){
			getTeragridInfo();
		}
	}
	
	static private void getTeragridInfo(){
		try{	
			// Get the location of the jar file and the jar file name
			URL outputURL = Agent.class.getProtectionDomain().getCodeSource()
					.getLocation();
			String outputString = outputURL.toString();
			String[] parseString;
			int index1 = outputString.indexOf(":");
			int index2 = outputString.lastIndexOf(":");
			if (index1 != index2)
			{ // Windows/DOS uses C: naming convention
				parseString = outputString.split("file:/");
			} else
			{
				parseString = outputString.split("file:");
			}
			String jarFilename = parseString[1];
			String[] tomcathome = jarFilename.split("/work/Catalina/");
			log.debug("full file URL: " + outputString);
			log.debug(tomcathome[0]);
			String portalhome = tomcathome[0] + "/webapps/grid";
			
			String tginfofile = portalhome + "/tginfo.csv";
			log.debug("grid app's home: " + portalhome);
			log.debug("tginfo file: " + tginfofile);	
			URL url = new URL(tginfokit);
			URLConnection con = url.openConnection();		
		    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuffer content = new StringBuffer();
		    String str;
		    while ((str = in.readLine()) != null){
		      content.append(str);
		      content.append("\n");
		    }
		    FileOutputStream fos = new FileOutputStream(tginfofile);
		    fos.write(content.toString().getBytes());
		    fos.close();
		    log.info(new Date().toString() + " : " + tginfofile + " saved!");
		    //retrieve rss feeds from teragrid news
		    for(String feedProvider : feedsProviders){
		    	String rssName = "rss-" + feedProvider + ".rss";
		    	String rssFile = portalhome + "/" + rssName;
		    	String strUrl = tgnewsfeeds + rssName;
		    	url = new URL(strUrl);
		    	con = url.openConnection();		
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				content = new StringBuffer();
				while ((str = in.readLine()) != null){
				  content.append(str);
				  content.append("\n");
				}
				fos = new FileOutputStream(rssFile);
				fos.write(content.toString().getBytes());
				fos.close();
				log.info(new Date().toString() + " : " + rssName + " saved!");
		    }//for
		    
		} catch (MalformedURLException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}
