package org.globus.crux.wsrf.properties;

import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.GetResourcePropertyResponse;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.InvalidResourcePropertyQNameFault;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import static org.testng.Assert.assertEquals;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import javax.xml.namespace.QName;

/**
 * @author turtlebender
 */
@Test(groups = {"wsrf", "properties"})
public class GetResourcePropertyImplTest {
    @Mock
    ResourcePropertySet rps;
    private QName resourceName = new QName("http://test.com", "TestResource");
    private QName propName = new QName("http://test.com", "testProp");
    private QName fakePropName = new QName("http://fake.com", "fakeProp");
    private Object propValue = new Object();

    @BeforeTest
    public void setup() throws Exception {
        initMocks(this);
        when(rps.getResourceName()).thenReturn(resourceName);
        when(rps.containsResourceProperty(propName)).thenReturn(true).thenReturn(false);
        when(rps.getResourceProperty(propName)).thenReturn(propValue);
    }

    @Test(expectedExceptions = InvalidResourcePropertyQNameFault.class)
    public void testGetResourceProperty() throws Exception {
        GetResourcePropertyImpl grp = new GetResourcePropertyImpl();
        grp.setRpSet(rps);
        GetResourcePropertyResponse grpr = grp.getResourceProperty(propName);
        assertEquals(grpr.getAny().size(), 1);
        assertEquals(grpr.getAny().get(0), propValue);
        grp.withRPSet(rps);
        grp.getResourceProperty(fakePropName);
    }
}
