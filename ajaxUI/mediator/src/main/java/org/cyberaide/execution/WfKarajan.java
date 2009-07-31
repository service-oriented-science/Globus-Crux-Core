/*
 * WfKarajan.java
 * 
 * @version: $Id v1.0$
 * 
 */
package org.cyberaide.execution;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * karajan workflow
 * 
 */
public class WfKarajan extends Executable
{

  /**
   * Construct a new karajan workflow
   * 
   * @param provider
   *          specify provider
   */
  public WfKarajan()
  {
    super(WfType.KARAJAN);
  }

  /**
   * revise original workflow to get fine-grained status info
   * 
   * @param aProj
   *          the name of the karajan project to be modified
   */
  public static String addEchos(String aProj)
  {
    Document origDoc = null;
    Document newDoc = null;

    // build xml doc from the project description
    try
    {
      origDoc =
          DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
              new ByteArrayInputStream(aProj.getBytes()));
    } catch (Exception e)
    {
      // e.printStackTrace(System.err);
      // System.out.println("original doc construction failed!");
    }

    // get all the original nodes
    Node root = origDoc.getFirstChild();
    NodeList nodes = root.getChildNodes();
    Element newElem = null;
    try
    {
      newDoc =
          DocumentBuilderFactory.newInstance().newDocumentBuilder()
              .newDocument();
      newDoc.appendChild(newDoc.createElement("project"));
    } catch (Exception e)
    {
    }
    Node newRoot = newDoc.getFirstChild();

    // add echo nodes after each original node
    for (int i = 0, nid = 0; i < nodes.getLength(); ++i)
    {
      Node aNode = nodes.item(i);
      String name = aNode.getNodeName();
      // String value = null;
      newRoot.appendChild(newDoc.importNode(aNode, true));
      if (!name.equals("#text") && !name.equals("include"))
      {
        newElem = newDoc.createElement("echo");
        newElem.setAttribute("message", (nid++) + "|finished");
        newRoot.appendChild(newElem);
      }
    }
    root = newDoc.getFirstChild();
    String newDocString = null;
    try
    {
      Source in = new DOMSource(newDoc);
      OutputStream os = new ByteArrayOutputStream();
      Result out = new StreamResult(os);
      Transformer trans = TransformerFactory.newInstance().newTransformer();
      trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      trans.transform(in, out);
      newDocString = os.toString();
    } catch (Exception e)
    {
    }
    return newDocString;
  }
}
