package org.globus.crux.wsrf.properties;

import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.InvalidResourcePropertyQNameFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.ResourceUnknownFault;

import javax.xml.namespace.QName;

/**
 * @author turtlebender
 */
public interface ResourcePropertySet extends Iterable<QName>{
    public boolean containsResourceProperty(QName qname);
    public Object getResourceProperty(QName qname) throws InvalidResourcePropertyQNameFault, ResourceUnknownFault;
    public QName getResourceName();
}
