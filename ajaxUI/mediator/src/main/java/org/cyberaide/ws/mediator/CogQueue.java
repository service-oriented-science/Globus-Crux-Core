/*
 * CogQueue.java
 * 
 * @version: $Id v1.0$
 * 
 */
package org.cyberaide.ws.mediator;

import java.util.Set;
import java.util.TreeSet;

import org.cyberaide.execution.*;

/**
 * The CogQueue is the shared zone that users use to share workflows and do the
 * elementary collaboration works
 * 
 */
public class CogQueue
{
  String queueName;
  int uid;
  String[] tags;
  String owner;
  Set<String> participantList;
  Set<Workflow> Wfobjs;

  /**
   * Construct a new CogQueue
   * 
   * @param queueName
   *          the queue's name
   */
  public CogQueue(String queueName)
  {
    this.queueName = queueName;
    participantList = new TreeSet<String>();
    Wfobjs = new TreeSet<Workflow>();
  }
}
