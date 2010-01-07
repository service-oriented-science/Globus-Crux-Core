package org.globus.crux.wsrf.properties;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.namespace.QName;

import org.globus.crux.MethodCallWrapper;
import org.globus.crux.messaging.sender.NotifierFactory;
import org.globus.crux.service.ResourcePropertyChange;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.InvalidResourcePropertyQNameFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.ResourcePropertyValueChangeNotificationType;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.ResourceUnknownFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.ResourcePropertyValueChangeNotificationType.NewValue;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.ResourcePropertyValueChangeNotificationType.OldValue;
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
	private NotifierFactory notifierFactory;
	
	public Class getAssociatedAnnotation() {
		return ResourcePropertyChange.class;
	}
	
	public Object doBefore(Annotation annotation) {
		return getValues(annotation);
	}

	public void doAfter(Annotation annotation, Object oldValues) {
		
		Map<QName, Object> newValues = getValues(annotation);
		sendNotifications((Map<QName, Object>) oldValues, newValues);
		
	}
	
	private Map<QName, Object> getValues(Annotation annotation) {
		String namespace = ((ResourcePropertyChange) annotation).namespace();
		String[] localparts = ((ResourcePropertyChange) annotation).localparts();	
		
		Map<QName, Object> values = new HashMap<QName, Object>();
		
		for (String localpart : localparts) {
			QName rpQName = new QName(namespace, localpart);	
			Object rp = getRP(rpQName);
			if (rp != null) {
				values.put(rpQName, rp);
			}
		}
		return values;
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
			} catch (ResourceUnknownFault e) {
				logger.error("rp not found: " + rpQName, e);
			}
		}
		
		return null;
	}
	
	/**
	 * Helper class used to send the notification about the resource property modification.
	 */
	private void sendNotifications(Map<QName, Object> oldValues, Map<QName, Object> newValues) {
		if (oldValues.size() != newValues.size()) {
			throw new IllegalArgumentException("number of old values does not match number of new values");
		}
		
		for (QName qname : oldValues.keySet()) {
			if (!newValues.containsKey(qname)) {
				throw new IllegalArgumentException("new value for old value is missing: " + qname.toString());
			}

			ResourcePropertyValueChangeNotificationType notification = new ResourcePropertyValueChangeNotificationType();
			
			OldValue oldValue = new OldValue();
			oldValue.setAny(new JAXBElement<Object>(qname, Object.class, oldValues.get(qname)));
			notification.setOldValue(new JAXBElement<ResourcePropertyValueChangeNotificationType.OldValue>(qname,
					ResourcePropertyValueChangeNotificationType.OldValue.class, oldValue));
			
			NewValue value = new NewValue();
			value.setAny(new JAXBElement<Object>(qname, Object.class, newValues.get(qname)));
			notification.setNewValue(value);
			
			
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(ResourcePropertyValueChangeNotificationType.class);
				JAXBElement<ResourcePropertyValueChangeNotificationType> notificationElement = new JAXBElement<ResourcePropertyValueChangeNotificationType>(qname, ResourcePropertyValueChangeNotificationType.class, notification);
				notifierFactory.createNotificationSender(qname, rpSet.toString()).sendNotification(new JAXBSource(jaxbContext, notificationElement));
			} catch (JAXBException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
    public void setRPSet(ResourcePropertySet rpSet) {
        this.rpSet = rpSet;
    }
    
    public void setNotifierFactory(NotifierFactory notifierFactory) {
        this.notifierFactory = notifierFactory;
    }
}
