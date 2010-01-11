package org.globus.crux.wsrf.lifetime;

import java.util.Calendar;

import javax.xml.ws.Holder;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Doreen Seider
 */
@Test(groups = {"wsrf", "lifetime"})
public class ResourceTerminationImplTest {

	ResourceTerminationImpl classUnderTest;
	
	@BeforeMethod(alwaysRun = true)
    public void setUp() {
		classUnderTest = new ResourceTerminationImpl(new Object());
    }
	
	@Test
    public void testDestroyForSuccess() throws Exception {
		classUnderTest.destroy();
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testDestroyFailure() throws Exception {
		classUnderTest.destroy();
		classUnderTest.destroy();
    }
	
    @Test
	public void testSetTerminationTime() throws Exception {
		Holder<Calendar> currentTime = new Holder<Calendar>();
        Holder<Calendar> newTermTime = new Holder<Calendar>();
        Calendar termTime1 = Calendar.getInstance();
        termTime1.add(Calendar.SECOND, 1000);
		classUnderTest.setTerminationTime(termTime1, newTermTime, currentTime);
		
		Calendar termTime2 = Calendar.getInstance();
        termTime2.add(Calendar.SECOND, 1000);
		classUnderTest.setTerminationTime(termTime2, newTermTime, currentTime);
    }
}
