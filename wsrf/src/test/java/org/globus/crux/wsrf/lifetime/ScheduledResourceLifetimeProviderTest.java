package org.globus.crux.wsrf.lifetime;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.globus.crux.ProviderException;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourcelifetime_1_2_draft_01.ScheduledResourceTermination;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Doreen Seider
 */
@Test(groups = {"wsrf", "lifetime"})
public class ScheduledResourceLifetimeProviderTest {

	ScheduledResourceLifetimeProvider classUnderTest;
	
	@BeforeMethod(alwaysRun = true)
    public void setUp() {
		classUnderTest = new ScheduledResourceLifetimeProvider();
    }
	
	@Test
    public void testGetImplementationForSuccess() throws Exception {
		classUnderTest.setTarget(new Object());
		ScheduledResourceTermination impl = classUnderTest.getImplementation();
		assertNotNull(impl);
		assertEquals(classUnderTest.getImplementation(), impl);
    }
	
	@Test(expectedExceptions = ProviderException.class)
    public void testGetImplementationForFailure() throws Exception {
		classUnderTest.getImplementation();
    }
	
	@Test
    public void testGetInterface() {
        assertEquals(classUnderTest.getInterface(), ScheduledResourceTermination.class);
    }
}
