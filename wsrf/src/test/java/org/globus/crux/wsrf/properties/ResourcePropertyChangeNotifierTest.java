package org.globus.crux.wsrf.properties;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.globus.crux.messaging.sender.Notifier;
import org.globus.crux.messaging.sender.NotifierFactory;
import org.globus.crux.messaging.subscription.Subscription;
import org.globus.crux.service.ResourcePropertyChange;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.InvalidResourcePropertyQNameFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.ResourceUnknownFault;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Doreen Seider
 */
@Test(groups = {"wsrf","properties"})
public class ResourcePropertyChangeNotifierTest {
    
	private static final String namespace = "http://namespace/";

	private static final String rp1 = "rp1";
	private static final String rp2 = "rp2";
	private static final String rp3 = "rp3";

	private Map<QName, Object> beforeResult;
	
	private ResourcePropertyChangeNotifier classUnderTest;
	private ResourcePropertySet rpSet;
	private ResourcePropertyChange rpChangeAnnotation;
	
    NotifierFactory notifierFactory;

    @BeforeMethod(alwaysRun = true)
    public void setup() {

        classUnderTest = new ResourcePropertyChangeNotifier();
        
        rpChangeAnnotation = new ResourcePropertyChange() {
			
			public Class<? extends Annotation> annotationType() {
				return null;
			}
			
			public String namespace() {
				return "http://namespace/";
			}
			
			public String[] localparts() {
				return new String[] { rp1, rp2, rp3 };
			}
		};
		
		rpSet = new ResourcePropertySet() {
			
			public Iterator<QName> iterator() {
				return null;
			}
			
			public Object getResourceProperty(QName qname)
					throws InvalidResourcePropertyQNameFault, ResourceUnknownFault {
				if (qname.getLocalPart().equals(rp1)) {
					return 1;
				} else if (qname.getLocalPart().equals(rp2)) {
					return 2;
				} else if (qname.getLocalPart().equals(rp3)) {
					return 3;
				}
				throw new InvalidResourcePropertyQNameFault();
			}
			
			public QName getResourceName() {
				return null;
			}
			
			public boolean containsResourceProperty(QName qname) {
				if (qname.getLocalPart().equals(rp1) ||
					qname.getLocalPart().equals(rp2) ||
					qname.getLocalPart().equals(rp3)) {
					return true;
				} else {
					return false;					
				}
			}
		};
		
		notifierFactory = new NotifierFactory() {
			
			public Notifier createNotificationSender(QName topicName,
					String resourceKeyId) {
				return new Notifier() {
					
					public void sendNotification(Source notification) {
					}
				};
			}
			
			public Notifier createNotificationSender(Subscription subscript) {
				return null;
			}
		};
		
		beforeResult = new HashMap<QName, Object>();
		beforeResult.put(new QName(namespace, rp1), 1);
		beforeResult.put(new QName(namespace, rp2), 2);
		beforeResult.put(new QName(namespace, rp3), 3);

    }

    @Test
    public void testDoBefore() throws Exception {
    	classUnderTest.setRPSet(rpSet);
    	classUnderTest.doBefore(rpChangeAnnotation);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testDoBeforeForFailureWithoutRPSet() throws Exception {
    	classUnderTest.doBefore(rpChangeAnnotation);
    }

    @Test
    public void testGetDoAfter() throws Exception {
    	classUnderTest.setRPSet(rpSet);
    	classUnderTest.setNotifierFactory(notifierFactory);
        classUnderTest.doAfter(rpChangeAnnotation, beforeResult);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testDoAfterForFailureWithoutRPSet() throws Exception {
    	classUnderTest.setNotifierFactory(notifierFactory);
    	classUnderTest.doAfter(rpChangeAnnotation, beforeResult);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testDoAfterForFailureWithoutNotifierFac() throws Exception {
    	classUnderTest.setRPSet(rpSet);
    	classUnderTest.doAfter(rpChangeAnnotation, beforeResult);
    }
}
