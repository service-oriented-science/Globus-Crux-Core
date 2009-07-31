/*
 * Workflow.java
 * 
 * @version:
 *     $Id v1.0$
 *
 */

package org.cyberaide.execution;

/**
 * define a workflow that will be stored in CogQueue zone
 *
 */
public class Workflow {
	String cogQueue;
	int uid;
	String[] tags;
	String owner;
	boolean locked;//true while being edited by some user
	WfStatus status;
	WfType type;
	Executable wfobj;
	
	/**
     * Construct a new workflow
     *
     * @param uid the uid
     * @param owner owner of the workflow
     * @param status latest status
     * @param type the workflow type
     * @param wfobj the essential workflow object
     */
	public Workflow(int uid, String owner, WfStatus status, WfType type, Executable wfobj){
		this.cogQueue = null;
		this.uid = uid;
		this.tags = null;
		this.owner = owner;
		this.locked = false;
		this.status = status;
		this.type = type;
		this.wfobj = wfobj;
	}
	
	/**
     * Construct a new workflow
     *
     * @param cogQueue cog queue in which to be stored
     * @param uid the uid
     * @param tags add some tags
     * @param owner owner of the workflow
     * @param locked whether the workflow is locked(protect from simultaneous modification)
     * @param status latest status
     * @param type the workflow type
     * @param wfobj the essential workflow object
     */
	public Workflow(String cogQueue, int uid, String[] tags, String owner, boolean locked,
			WfStatus status, WfType type, Executable wfobj){
		this.cogQueue = cogQueue;
		this.uid = uid;
		this.tags = tags;
		this.owner = owner;
		this.locked = locked;
		this.status = status;
		this.type = type;
		this.wfobj = wfobj;
	}
	
	/**
     * get workflow id
     *
     * @return the workflow Id
     */
	public int getId(){
		return this.uid;
	}
	
	/**
     * output workflow infos
     *
     * @return the string representation of the workflow
     */
	public String toString(){
		StringBuffer ret = new StringBuffer("[cogQueue]:" + cogQueue + "\n");
		ret.append("[uid]:" + uid + "\n[tags]:");
		for(String aString: tags){
			ret.append(" ");
			ret.append(aString);
		}
		ret.append("\n[owner]:" + owner +
				"\n[locked]:" + locked +
				"\n[status]:" + status +
				"\n[type]:" + type +
				"\n[wfobj]:\n" + wfobj);
		return new String(ret);
	}
}
