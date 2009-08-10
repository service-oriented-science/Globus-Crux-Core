package org.globus.crux.wsrf.properties;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import static org.testng.Assert.assertEquals;
import org.mockito.Mock;
import static org.mockito.MockitoAnnotations.initMocks;
import org.globus.crux.ProviderException;
import org.oasis.wsrf.properties.GetResourceProperty;

@Test(groups = {"wsrf","properties"})
public class GetRPProviderTest {
    @Mock
    ResourcePropertySet rps;

    @BeforeTest
    public void setup() {
        initMocks(this);
    }

    @Test(expectedExceptions = ProviderException.class)
    public void testGetImplementation() throws Exception{
        GetRPProvider provider = new GetRPProvider();
        provider.withRps(rps);
        GetResourceProperty grp = provider.getImplementation();
        assertEquals(grp.getClass(), GetResourcePropertyImpl.class);
        assertEquals(provider.getInterface(), GetResourceProperty.class);
        provider.setRPSet(rps);
        assertEquals(grp.getClass(), GetResourcePropertyImpl.class);
        assertEquals(provider.getInterface(), GetResourceProperty.class);
        provider.setRPSet(null);
        provider.getImplementation();
        // Add your code here
    }
}
