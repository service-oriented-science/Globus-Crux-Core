package org.globus.crux.stateful.resource;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import static org.testng.AssertJUnit.*;

/**
 * @author turtlebender
 */
@Test(groups = {"unit","stateful"})
public class InMemoryResourceManagerTest {
    private ResourceManager<Integer, String> manager;

    @BeforeTest
    public void init() throws ResourceException{
        manager = new InMemoryResourceManager<Integer, String>();
        manager.storeResource(1, "One");
    }

    @Test
    public void testInsert() throws ResourceException{
        try{
            manager.storeResource(1, "Two");
            fail();
        }catch(Exception e){
            //do nothing catch
        }
    }

    @Test(dependsOnMethods = {"testInsert"})
    public void testFind() throws ResourceException{
        String value = manager.findResource(1);
        assertEquals("One", value);
    }

    @Test(dependsOnMethods = {"testFind"})
    public void testUpdate() throws ResourceException{
        manager.updateResource(1, "Two");
        assertEquals("Two", manager.findResource(1));
    }

    @Test(dependsOnMethods = {"testUpdate"})
    public void testRemove() throws ResourceException{
        manager.removeResource(1);
        assertNull(manager.findResource(1));
    }    
}
