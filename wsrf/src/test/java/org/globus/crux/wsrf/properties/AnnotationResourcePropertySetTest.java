package org.globus.crux.wsrf.properties;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertEquals;
import org.globus.crux.service.StatefulService;
import org.oasis.wsrf.properties.InvalidResourcePropertyQNameFault;

import java.util.Iterator;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;

import javax.xml.namespace.QName;

/**
 * @author turtlebender
 */
@Test(groups = {"wsrf", "properties"})
public class AnnotationResourcePropertySetTest {
    AnnotationResourcePropertySet rps;
    SampleResource sr = new SampleResource();
    QName propName = new QName("http://test.com", "testProp");

    @BeforeTest
    public void setUp() {
        rps = new AnnotationResourcePropertySet(sr);
    }

    public void testIterator() {
        Iterator<QName> iter = rps.iterator();
        assertNotNull(iter);
        assertTrue(iter.hasNext());
        assertEquals(iter.next(), propName);
        assertFalse(iter.hasNext());
    }

    @Test
    public void testContainsResourceProperty() {
        assertTrue(rps.containsResourceProperty(propName));
        assertFalse(rps.containsResourceProperty(new QName("http://fake.com", "fakeProp")));
    }

    @Test
    public void testGetResourceName() {
        assertEquals(rps.getResourceName(), new QName("http://test.com", "TestResource"));
    }

    @Test(expectedExceptions = InvalidResourcePropertyQNameFault.class)
    public void testGetResourceProperty() throws Exception{
        assertEquals(rps.getResourceProperty(propName), sr.object);
        rps.getResourceProperty(new QName("http://fake.com", "fakeProp"));
    }

    @StatefulService(namespace = "http://test.com", keyName = "TestKey", resourceName = "TestResource")
    class SampleResource {
        Object object = new Object();

        @ResourceProperty(namespace = "http://test.com", localpart = "testProp")
        public Object getResourceProperty() {
            return object;
        }

    }
}
