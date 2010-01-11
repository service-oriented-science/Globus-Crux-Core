package org.globus.crux;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Doreen Seider
 */
@Test(groups = {"core"})
public class CruxNamespaceHandlerTest {

	public CruxNamespaceHandler classUnderTest;
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() {
		classUnderTest = new CruxNamespaceHandler();
	}
	
	@Test
	public void testInit() {
		classUnderTest.init();
	}
}
