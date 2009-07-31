/*
 * IMediator.java
 * 
 * @version: $Id v1.0$
 * 
 */

package org.cyberaide.ws.mediator;

import javax.jws.WebService;
import javax.jws.WebMethod;

import org.cyberaide.execution.*;

/**
 * The server side mediator to fulfil the main application logic and expose them
 * as web services
 * 
 */
@WebService
public interface IMediator
{
  /**
   * list files of the specified directory
   * 
   * @param user the authenticated user
   * @param dir the relative dir, from the user's homeuser's home
   * 
   * @return serialized filelist
   */
  @WebMethod(action = "listDir")
  public String listDir(String user, String dir);

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
      String password, int lifetime);

  /**
   * user submit a workflow to execute
   * 
   * @param aProj
   *          the description of the workflow
   * 
   * @return the randomly assigned workflow id
   */
  @WebMethod(action = "exec")
  public int exec(String user, String aProj);

    /**
   * third party file transfer
   *  
   * @param user the authenticated user
   * @param from the source URI
   * @param to the destination URI
   * 
   */
  @WebMethod(action = "transfer")
  public String transfer(String user, String from, String to);

  /**
   * user query the execution status by workflow id
   * 
   * @param Wfid
   *          the workflow id
   * 
   * @return a string combines the Wfid and status code (the job # finished)
   */
  @WebMethod(action = "statusQuery")
  public String statusQuery(int Wfid);

  /**
   * list all the submitted workflow ids of the user
   * 
   * @param user
   *          the user's login name
   * 
   * @return a string containing all the workflow ids that user submitted
   */
  @WebMethod(action = "listExecIds")
  public String listExecIds(String user);

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
  public String listOutput(String user, int Wfid);

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
  public String fetchOutput(String user, int Wfid, String outputfile);

    /**
   * list all the public and/or the CogQueues that the user participated
   *  
   * @param user the authenticated user
   * @return serialized queue ids
   */
  @WebMethod(action = "listQueues")
  public String listQueues(String user);

  /**
   * list participants of the specified CogQueue
   * 
   * @param queueId
   *          the queue id
   * 
   * @return serialized participants list
   */
  @WebMethod(action = "listParticipants")
  public String listParticipants(int queueId);

  /**
   * list all workflows in a CogQueue
   * 
   * @param queueId
   *          the queue to be queried in
   * 
   * @return serialized workflow ids
   */
  @WebMethod(action = "listWf")
  public String listWf(int queueId);

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
  public int upload(String aProj, int queueId, int Wfid);

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
  public String download(int queueId, int Wfid);

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
  public int remove(int queueId, int Wfid);

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
  public String listWfByUser(int queueId, String user);

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
  public String listWfByType(int queueId, WfType theType);

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
  public int grantAccess(int queueId, String userlist);

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
  public String checkLock(int queueId, int Wfid);

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
  public int obtainLock(int queueId, int Wfid);

}
