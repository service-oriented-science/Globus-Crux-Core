/*
 * CogMediator.java
 * 
 * @version: $Id v1.0$
 * 
 */

package org.cyberaide.ws.mediator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

import org.apache.log4j.Logger;

import org.globus.cog.karajan.Loader;
import org.globus.cog.karajan.workflow.ElementTree;
import org.globus.cog.karajan.workflow.ExecutionContext;
import org.globus.myproxy.MyProxy;
import org.gridforum.jgss.ExtendedGSSCredential;
import org.ietf.jgss.GSSCredential;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

import org.cyberaide.execution.*;

/**
 * The server side mediator to fulfil the main application logic and expose them
 * as web services
 * 
 */
@WebService
public class CogMediator implements IMediator
{
  // uid generator seed
  Random uidGenerator;
  // cache the submitted job ids
  Set submittedIds;
  // db4o database
  ObjectContainer dbSubmittedWf, dbPublicSharedWf;
  static Logger log = Logger.getLogger(CogMediator.class);
  
  /**
   * Construct a new CogMediator and do the initial works
   * 
   */
  public CogMediator()
  {
    uidGenerator = new Random();
    submittedIds = new HashSet<Integer>();
    try
    {
      dbSubmittedWf = Db4o.openFile("dbSubmittedWf");
      Workflow tempWf =
          new Workflow(null, 0, null, null, false, null, null, null);
      ObjectSet Wfs = dbSubmittedWf.get(tempWf);
      while (Wfs.hasNext())
      {
        submittedIds.add(((Workflow) (Wfs.next())).getId());
      }
    } catch (Exception e)
    {
    } finally
    {
      if (dbSubmittedWf != null)
      {
        dbSubmittedWf.close();
      }
    }
  }

  /**
   * start the server and deploy the service endpoint
   * 
   * Usage: java CogMediator <endpoint url> e.g.,
   * java CogMediator http://localhost:8998/mediator
   * will deploy the service into
   * http://localhost:8998/mediator and start the server
   * 
   */
  public static void main(String[] args)
  {
    String endp = args[0];
    try
    {
      Endpoint.publish(endp, new CogMediator());
    } catch (Exception e)
    {
      log.error(e.getMessage());
      System.exit(1);
    }
  }

  //
  // the web services methods below
  //
  
  /**
   * list files of the specified directory
   * 
   * @param user the authenticated user
   * @param dir the relative dir, from the user's home
   * 
   * @return serialized filelist
   */
  @WebMethod(action = "listDir")
  public String listDir(String user, String dir)
  {
  	log.info("listDir called with dir \"" + dir + "\"");
  	String ret = "";
    File homeDir = null;
    File myDir = null;
    OutputStream out = null;
    String strHomeDir = "./gridusers/" + user + "/home/";
    String strMyDir = strHomeDir + "./" + dir;
    log.info("home: " + strHomeDir);
    log.info("my: " + strMyDir);
    try {
      homeDir = new File(strHomeDir);
      myDir = new File(strMyDir);
    } catch (Exception e){
    	log.error("exception in listDir");
    }
    
    // create the directory for the new workflow
    if (!homeDir.exists()) {
      homeDir.mkdirs();
    }
    if (!myDir.exists()) {
      return ret;
    }
    // set the process's environment and working directory
    ProcessBuilder pb = new ProcessBuilder("ls", "-l", strMyDir);

    // run the process and getting the output
    String line = null;
    Process Wfproc = null;
	StringBuffer retbuf = new StringBuffer();
	
    try {
      Wfproc = pb.start();
      BufferedReader input =
          new BufferedReader(new InputStreamReader(Wfproc.getInputStream()));
      
	  BufferedReader stdError =
		  new BufferedReader(new InputStreamReader(Wfproc.getErrorStream()));
		  
      while ((line = input.readLine()) != null) {
        retbuf.append(line);
        retbuf.append("\n");
      }
      
      while ((line = input.readLine()) != null) {
        retbuf.append(line);       
        retbuf.append("\n");
      }
      ret = new String(retbuf);
    } catch (IOException e){
    	log.error("exception while reading file list");
    	log.error(e.getMessage());
    }
    return ret;
  }
  
  /**
   * retrieve a proxy credential from a myproxy server
   * 
   * @param host
   *          myproxy server host
   * @param port
   *          myproxy server's port
   * @param user
   *          username
   * @param password
   *          passphrase to login to the myproxy server
   * @param lifetime
   *          the lifetime of the proxy credential (in seconds)
   * 
   * @return true if retrieve successfully, false otherwise
   */
  @WebMethod(action = "retrieveCertificate")
  public boolean retrieveCertificate(String host, int port, String user,
      String password, int lifetime)
  {
	
	log.info("I'm running from inside CogMediator");
	log.info("host: " + host);
	log.info("port: " + port);
	log.info("user: " + user);
	log.info("password: " + password);
	log.info("lifetime: " + lifetime);
	
    if (lifetime == 0)
    {
      lifetime = 60 * 60 * 2; // default lifetime is 2 hours
    }
    try
    {
      MyProxy proxy = new MyProxy(host, port);
      GSSCredential cred = proxy.get(user, password, lifetime);
      int remainingtime = cred.getRemainingLifetime();
      // stem.out.println( "remaining time in second(s): " + remainingtime );

      // store the credential to different directory for the corresponding user
      String strMyDir = "./gridusers/" + user;
      String outputFile = strMyDir + "/x509proxy";

      // create necessary directory
      File dir = new File(strMyDir);
      if (!dir.exists())
      {
        dir.mkdirs();
      }
      // create file
      File f = new File(outputFile);
      if (f.exists())
      {
        f.delete();
      }
      // write content of the credential into the file
      String path = f.getAbsolutePath();
      OutputStream out = null;
      try
      {
        out = new FileOutputStream(f);
        // write the contents
        byte[] data =
            ((ExtendedGSSCredential) cred)
                .export(ExtendedGSSCredential.IMPEXP_OPAQUE);
        out.write(data);
      } catch (Exception e)
      {
        // e.printStackTrace(System.err);
      } finally
      {
        if (out != null)
        {
          try
          {
            out.close();
          } catch (Exception e)
          {
          }
        }
      }
      // set appropriate permissions
      ProcessBuilder pb = new ProcessBuilder("chmod", "600", outputFile);
      pb.start();
      cred.dispose();
      log.info("A proxy certificate has been received for user "
          + user + " in " + path);
      return true;
    } catch (Exception e)
    {
      log.info("Failed to retrieve proxy certificate!!\n" + e);
      return false;
    }
  }

  /**
   * user submit a workflow to execute
   *  
   * @param user the authenticated user
   * @param aProj the description of the workflow
   * 
   * @return the randomly assigned workflow id
   */
  @WebMethod(action = "exec")
  public int exec(String user, String aProj){
  	log.info("user: " + user);
	log.info("proj to be submitted: " + aProj);
    Executable task = new WfKarajan();
    task.addAttributes("Wfkarajan", aProj);
    final int WfId = uidGen();
    Workflow Wf =
        new Workflow(null, WfId, null, user, false, WfStatus.NOTSUBMITTED,
            WfType.KARAJAN, task);
    // record original workflow
    recordSubmittedWf(Wf);
    // add echo nodes
    final String theUser = user;
    final String modProj = WfKarajan.addEchos(aProj);
    // and submit the revised workflow
    Thread aExecThread = new Thread(new Runnable() {
      public void run() {
        cmdExec(theUser, WfId, modProj);
      }
    });
    aExecThread.start();   
    return WfId;
  }

  /**
   * third party file transfer
   *  
   * @param user the authenticated user
   * @param from the source URI
   * @param to the destination URI
   * 
   */
  @WebMethod(action = "transfer")
  public String transfer(String user, String from, String to)
  {
    return FileTransfer.transfer(user, from, to);
  }

  /**
   * user query the execution status by workflow id
   * 
   * @param Wfid
   *          the workflow id
   * 
   * @return a string combines the Wfid and status code (the job # finished)
   */
  @WebMethod(action = "statusQuery")
  public String statusQuery(int Wfid)
  {
    log.info("id querried: " + Wfid);
    // StatusEntry aStatusEntry = null;//new StatusEntry(Wfid, 0);
    String strDbStatus = "./gridusers/dbStatus_" + Wfid;
    ObjectContainer dbStatus = null;
    try
    {
      dbStatus = Db4o.openFile(strDbStatus);
    } catch (Exception e) {
      log.error("cannot open status record file!");
      return "0_0"; 
    }
    ObjectSet entries = dbStatus.get(null);
    StatusEntry entry = null;
    int status = 0;
    if (entries.hasNext())
    {// check the old status
      entry = (StatusEntry) (entries.next());
      status = entry.getStatus();
    }
    entries = dbStatus.get(null);// new StatusEntry(0, 0));
    while (entries.hasNext())
    {
      Object record = entries.next();
      if (record instanceof StatusEntry)
      {
        StatusEntry aEntry = (StatusEntry) record;
        // StatusEntry aEntry = (StatusEntry)(entries.next());
        log.info("Wfid: " + aEntry.uid + ", Status: "
            + aEntry.getStatus());
      }
    }
    //log.info("No entries found!");
    //if (dbStatus != null)
    //{
    //  dbStatus.close();
    //}
    while(!dbStatus.close()){}
    return Wfid + "_" + status;
  }

  /**
   * list all the submitted workflow ids of the user
   * 
   * @param user
   *          the user's login name
   * 
   * @return a string containing all the workflow ids that user submitted
   */
  @WebMethod(action = "listExecIds")
  public String listExecIds(String user)
  {
    StringBuffer outputs = new StringBuffer();
    String path = "./gridusers/" + user + "/output";
    File f;
    String[] ids;
    try
    {
      f = new File(path);
      ids = f.list();
      for (String id : ids)
      {
        outputs.append(id);
        outputs.append("_");
      }
    } catch (Exception e)
    {
    }
    return new String(outputs);
  }

  /**
   * list all the output filenames for the specified workflow
   * 
   * @param user
   *          the user's login name
   * @param Wfid
   *          the workflow id
   * 
   * @return a string containing all the output filenames of the workflow
   */
  @WebMethod(action = "listOutput")
  public String listOutput(String user, int Wfid)
  {
    // int Wfidhead = Wfid;
    StringBuffer outputs = new StringBuffer();
    outputs.append(Wfid);
    String path = "./gridusers/" + user + "/output/" + Wfid;
    File f;
    String[] filenames;
    try
    {
      f = new File(path);
      filenames = f.list();
      for (String filename : filenames)
      {
        outputs.append('_');
        outputs.append(filename);
      }
    } catch (Exception e)
    {
    }
    return outputs.toString();
  }

  /**
   * display content of an output file
   * 
   * @param user
   *          the user's login name
   * @param Wfid
   *          the workflow id
   * @param outputfile
   *          the output filename
   * 
   * @return the content of the output file
   */
  @WebMethod(action = "fetchOutput")
  public String fetchOutput(String user, int Wfid, String outputfile)
  {
    String path = "./gridusers/" + user + "/output/" + Wfid + "/" + outputfile;
    File f = null;
    int filesize = 0;
    try
    {
      f = new File(path);
      filesize = (int) (f.length());
    } catch (Exception e)
    {
    }
    byte[] bytes = new byte[filesize];
    FileInputStream fis = null;
    try
    {
      fis = new FileInputStream(f);
      fis.read(bytes);
      fis.close();
    } catch (Exception e)
    {
    }
    String fileContent = new String(bytes);
    return Wfid + "_" + outputfile + "_" + fileContent;
  }

  /**
   * list all the public and/or the CogQueues that the user participated
   *  
   * @param user the authenticated user
   * @return serialized queue ids
   */
  @WebMethod(action = "listQueues")
  public String listQueues(String user)
  {
    return null;
  }

  /**
   * list participants of the specified CogQueue
   * 
   * @param queueId
   *          the queue id
   * 
   * @return serialized participants list
   */
  @WebMethod(action = "listParticipants")
  public String listParticipants(int queueId)
  {
    return null;
  }

  /**
   * list all workflows in a CogQueue
   * 
   * @param queueId
   *          the queue to be queried in
   * 
   * @return serialized workflow ids
   */
  @WebMethod(action = "listWf")
  public String listWf(int queueId)
  {
    return null;
  }

  /**
   * user upload shared workflows into CogQueue zone
   * 
   * @param aProj the description of the uploaded workflow
   * @param queueId the queue in which the workflow to be added in. 0 for a new queue.
   * @param Wfid the workflow id if it's updating the workflow. 0 for a new workflow.
   * 
   * @return the workflow id if successfully uploaded, 0 or negative number (as
   *         error code) if failed.
   */
  @WebMethod(action = "upload")
  public int upload(String aProj, int queueId, int Wfid)
  {
    return 0;
  }

  /**
   * download the a workflow from the CogQueue zone
   * 
   * @param queueId
   *          the queue id
   * @param Wfid
   *          the workflow id
   * 
   * @return the serialized workflow, including type and description
   */
  @WebMethod(action = "download")
  public String download(int queueId, int Wfid)
  {
    return null;
  }

  /**
   * user remove a workflow from a CogQueue
   * 
   * @param queueId
   *          the queue id
   * @param Wfid
   *          the workflow id
   * 
   * @return the workflow id if removed successfully, 0 or negative number (as
   *         error code) if failed.
   */
  @WebMethod(action = "remove")
  public int remove(int queueId, int Wfid)
  {
    return 0;
  }

  /**
   * list workflows owned by a specified user in a CogQueue
   * 
   * @param queueId
   *          the queue to be queried in
   * @param user
   *          specify user
   * 
   * @return the workflow ids owned by the specified user from the specified
   *         queue
   */
  @WebMethod(action = "listWfByUser")
  public String listWfByUser(int queueId, String user)
  {
    return null;
  }

  /**
   * list workflows of a specified type in a CogQueue
   * 
   * @param queueId
   *          the queue to be queried in
   * @param theType
   *          specify workflow type
   * 
   * @return the workflow ids with the specified type from the specified queue
   */
  @WebMethod(action = "listWfByType")
  public String listWfByType(int queueId, WfType theType)
  {
    return null;
  }

  /**
   * grant access to the users in the list
   * 
   * @param queueId
   *          the queue to be queried in
   * @param userlist
   *          serialized userlist
   * 
   * @return the queue id if grant sccessfully, 0 or negative number (as error
   *         code) if failed.
   */
  @WebMethod(action = "grantAccess")
  public int grantAccess(int queueId, String userlist)
  {
    return 0;
  }

  /**
   * check whether the workflow is being edited (locked) by someone else
   * 
   * @param queueId
   *          the queue to be queried in
   * @param Wfid
   *          the workflow id to be checked
   * 
   * @return the user that is editing the workflow, null if none is editing the
   *         workflow
   */
  @WebMethod(action = "checkLock")
  public String checkLock(int queueId, int Wfid)
  {
    return null;
  }

  /**
   * obtain the write token and lock the workflow
   * 
   * @param queueId
   *          the queue to be queried in
   * @param Wfid
   *          the workflow id to be checked
   * 
   * @return the workflow id that locked, 0 or negative number (as error code)
   *         if failed.
   */
  @WebMethod(action = "obtainLock")
  public int obtainLock(int queueId, int Wfid)
  {
    return 0;
  }

  //
  // private methods below
  //

  /**
   * randomly generate uid (1 ~ 2^31)
   * 
   * @return the randomly generated id
   */
  private int uidGen()
  {
    int id = uidGenerator.nextInt(Integer.MAX_VALUE);
    while (submittedIds.contains(id))
    {
      id = uidGenerator.nextInt(Integer.MAX_VALUE);
    }
    submittedIds.add(id);
    return id;
  }

  /**
   * record the original workflow user submitted
   * 
   * @param Wf
   *          the original workflow
   */
  private void recordSubmittedWf(Workflow Wf)
  {
    try {
      dbSubmittedWf = Db4o.openFile("dbSubmittedWf");
      ObjectSet Wfs = dbSubmittedWf.get(Wf);
      if (!Wfs.hasNext()) {
        dbSubmittedWf.set(Wf);
        dbSubmittedWf.commit();
      }
    } catch (Exception e) {
    } finally {
      if (dbSubmittedWf != null) {
        dbSubmittedWf.close();
      }
    }
  }

  /**
   * execute a workflow through command line tools provied by Cog Kit
   * 
   * @param user
   *          the user who submitted this workflow
   * @param uid
   *          the workflow's id
   * @param theProj
   *          modified workflow
   */
  private synchronized String cmdExec(String user, int uid, String theProj)
  {
  	log.info("in cmdExec...");
    // get the cog install path
    String cog_workflow = System.getenv("COG_INSTALL_PATH");
    // the execution directory, i.e., under the output and workflowId
    File myDir = null;
    // the workflow file's location
    File WfFile = null;
    // proxy credential's path
    File proxyDir = null;
    OutputStream out = null;
    String strMyDir = "./gridusers/" + user + "/output/" + uid;
    String strProxyDir = "./gridusers/" + user;
    try {
      // store the workflow file under the user's directory
      String strWfFile = "./gridusers/" + user + "/proj_" + uid + ".xml";
      // create the three directories/files
      myDir = new File(strMyDir);
      proxyDir = new File(strProxyDir);
      WfFile = new File(strWfFile);
      // write the workflow file into file
      out = new FileOutputStream(WfFile);
      out.write(theProj.getBytes());
      out.close();
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    // create the directory for the new workflow
    if (!myDir.exists()){
      myDir.mkdirs();
    }
    log.info("before dbStatus...");
    // the status file's location and filename
    String strDbStatus = "./gridusers/dbStatus_" + uid;
    // open the Db4o database for writing the status info in
    ObjectContainer dbStatus = null;
    try {
      	dbStatus = Db4o.openFile(strDbStatus);
    } catch (Exception e) {
    	log.error(e.getMessage());
    }
    
	log.info("before process build...exec");
    // build the process to execute the workflow
    String cmd = cog_workflow + "/bin/cog-workflow";
    String filename = null;
    try{
      filename = WfFile.getCanonicalPath();
    } catch (Exception e){
    	String err = e.getMessage();
      log.error(err);
      return err;
    }
    // set the process's environment and working directory
    ProcessBuilder pb = new ProcessBuilder(cmd, filename);
    Map<String, String> envs = pb.environment();
    pb = pb.directory(myDir);
    String proxyPath = null;
    try{
    	proxyPath = proxyDir.getCanonicalPath() + "/x509proxy";
    } catch (Exception e){
    	log.error(e.getMessage());
    }
    envs.put("X509_USER_PROXY", proxyPath);
    // System.out.println(pb.directory().getAbsolutePath());
    // System.out.println(pb.environment().get("X509_USER_PROXY"));
    log.info("submit through command line ...");
    // List<String> cmdline = pb.command();
    // for(String str: cmdline){
    // System.out.println(str);
    // }

    // run the process and getting the output
    String line = "";
    String ret = "";
    Process Wfproc = null;
    // persistence of execution status
    try {
      Wfproc = pb.start();
      BufferedReader input =
          new BufferedReader(new InputStreamReader(Wfproc.getInputStream()));
      BufferedReader stdError =
		  new BufferedReader(new InputStreamReader(Wfproc.getErrorStream()));
	  //while ((line = stdError.readLine()) != null){
	  //	  return line;
	  //}
      while ((line = input.readLine()) != null){
        ret += line;
        int newStatus, oldStatus = 0;
        StatusEntry aStatusEntry;
        int idx = line.indexOf("|finished");
        if (idx != -1)
        {// a status notice message
          newStatus = Integer.parseInt(line.substring(0, idx));
          aStatusEntry = null;// new StatusEntry(uid, 0);
          ObjectSet entry = dbStatus.get(aStatusEntry);
          StatusEntry oldEntry = null;
          if (entry.hasNext())
          {// check the old status
            oldEntry = (StatusEntry) (entry.next());
            oldStatus = oldEntry.getStatus();
          }
          if (newStatus > oldStatus)
          {// update status if a newer is coming
            dbStatus.delete(oldEntry);
            // set the new entry
            aStatusEntry = new StatusEntry(uid, newStatus);
            dbStatus.set(aStatusEntry);
            dbStatus.commit();
          }
        }
      }// while execute and wait for status return
    } catch (Exception e) {
      	log.error(e.getMessage());
    }
    // close the status database file when finished execution
    //if (dbStatus != null)
    //{
    //  dbStatus.close();
    //}
    while(!dbStatus.close()){}
    // System.out.println(ret);
    // return ret;
    return null;
  }

  /**
   * execute workflow by using Cog Kit API
   * 
   * @param uid
   *          the workflow's id
   * @param theProj
   *          revised workflow
   */
  private String apiExec(int uid, String theProj)
  {
    final String proj = theProj;
    String ret = null;
    Thread jobFork = new Thread(new Runnable()
    {
      public void run()
      {
        try
        {
          ElementTree tree = Loader.loadFromString(proj);
          ExecutionContext ec = new ExecutionContext(tree);
          ec.start();
          ec.waitFor();
        } catch (Exception e)
        {
          // ret = "Exception occured!";
        }
        // ret = "Job is finished!";
      }
    });
    jobFork.start();
    /*
     * try{ tmpf = new File("/tmp/testtmp_" + System.getProperty("user.name") +
     * ".log"); out = new FileOutputStream(tmpf); out.write(proj.getBytes()); }
     * catch (Exception e) { ret = "exception!"; }
     */
    try
    {
      jobFork.join();
    } catch (InterruptedException e)
    {
      ret = "exception!";
    }
    ret = "Job is finished!";
    return ret;
  }

  //
  // inner helper clas
  //
  /**
   * inner class to represent a status entry in the db
   * 
   */
  private class StatusEntry
  {
    private int uid;
    private int finishedNodes;

    /**
     * construct a new entry
     * 
     * @param uid
     *          the workflow id
     * @param finishedNodes
     *          finished job numbers
     * 
     */
    public StatusEntry(int uid, int finishedNodes)
    {
      this.uid = uid;
      this.finishedNodes = finishedNodes;
    }

    /**
     * get the workflow id
     * 
     * @return the workflow id this entry belongs to
     */
    public int getWfid()
    {
      return uid;
    }

    /**
     * get the status
     * 
     * @return the number of the jobs finished
     */
    public int getStatus()
    {
      return finishedNodes;
    }

    /**
     * set the status
     * 
     * @param finishedNodes
     *          the number of the jobs finished
     */
    public void setStatus(int finishedNodes)
    {
      this.finishedNodes = finishedNodes;
    }
  }
}
