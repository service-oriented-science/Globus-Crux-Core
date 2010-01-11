package org.globus.crux.wsrf.query;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.globus.crux.ProviderException;
import org.globus.crux.wsrf.properties.ResourcePropertySet;
import org.mockito.Mock;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryResourceProperties;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Doreen Seider
 */
@Test(groups = {"wsrf", "query"})
public class QueryRPProviderTest {

	@Mock
    ResourcePropertySet rps;
	List<QueryEngine<Object, Object>> engines;
	@Mock
	QueryEngine<Object, Object> engine;
	
	QueryRPProvider classUnderTest;
    
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
    	classUnderTest = new QueryRPProvider();
    	engines = new ArrayList<QueryEngine<Object, Object>>();
    }

    @Test
    public void testGetIInterface() {
    	assertEquals(QueryResourceProperties.class, classUnderTest.getInterface());
    }
    
    @Test
    public void testGetImplementation() throws Exception {
    	engines.add((QueryEngine<Object, Object>) engine);
    	classUnderTest.setEngines(engines);
    	classUnderTest.getImplementation();
    }
    
    @Test(expectedExceptions = ProviderException.class)
    public void testGetImplementationWithoutEnginesSet() throws Exception {
    	classUnderTest.getImplementation();
    }
    
    @Test(expectedExceptions = ProviderException.class)
    public void testGetImplementationWithZeroEngines() throws Exception {
    	classUnderTest.setEngines(engines);
    	classUnderTest.getImplementation();
    }

}
