package org.globus.crux.wsrf.properties;

import java.lang.annotation.Annotation;

import javax.xml.namespace.QName;

import org.globus.crux.MethodCallWrapper;
import org.globus.crux.service.ResourcePropertyChange;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.InvalidResourcePropertyQNameFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.ResourceUnknownFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for firing a notification if a resource property was changed.
 * Therefore this class implements the {@link MethodCallWrapper} interface in
 * order to be able to wrapp the call of the method which will change the
 * resource property.
 * 
 * @author Doreen Seider
 */
public class ResourcePropertyChangeNotifier implements MethodCallWrapper {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private ResourcePropertySet rpSet;
	
	public Class getAssociatedAnnotation() {
		return ResourcePropertyChange.class;
	}
	
	public void doBefore(Annotation annotation) {
		String namespace = ((ResourcePropertyChange) annotation).namespace();
		String[] localparts = ((ResourcePropertyChange) annotation).localparts();	
		
		for (String localpart : localparts) {
			QName rpQName = new QName(namespace, localpart);	
			Object rp = getRP(rpQName);
			if (rp != null) {
				logger.info("rp '" + rpQName + "' changed. old value: " + rp.toString());
				System.out.println("rp '" + rpQName + "' changed. old value: " + rp.toString());
			}
		}
	}

	public void doAfter(Annotation annotation) {
		String namespace = ((ResourcePropertyChange) annotation).namespace();
		String[] localparts = ((ResourcePropertyChange) annotation).localparts();	
		
		for (String localpart : localparts) {
			QName rpQName = new QName(namespace, localpart);	
			Object rp = getRP(rpQName);
			if (rp != null) {
				logger.info("rp '" + rpQName + "' changed. new value: " + rp.toString());				
				System.out.println("rp '" + rpQName + "' changed. new value: " + rp.toString());
			}
		}
	}
	
	/**
	 * Returns a resource property value contained within the given
	 * resource property set.
	 * 
	 * @param rpQName FQN of the resource property.
	 * @return the value of the desired resource property or null if there is none.
	 */
	private Object getRP(QName rpQName) {
		if (rpSet.containsResourceProperty(rpQName)) {
			try {
				return rpSet.getResourceProperty(rpQName);
			} catch (InvalidResourcePropertyQNameFault e) {
				logger.error("rp not found: " + rpQName, e);
				System.out.println("rp not found.");
			} catch (ResourceUnknownFault e) {
				logger.error("rp not found: " + rpQName, e);
				System.out.println("rp not found.");
			}
		}
		
		return null;
	}
	
    public void setRPSet(ResourcePropertySet rpSet) {
        this.rpSet = rpSet;
    }
}
