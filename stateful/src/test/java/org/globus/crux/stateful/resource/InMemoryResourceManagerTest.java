package org.globus.crux.stateful.resource;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import static org.testng.AssertJUnit.*;

/**
 * @author turtlebender
 */
public class InMemoryResourceManagerTest {
    private ResourceManager<Integer, String> manager;

    @BeforeTest
    public void init(){
        manager = new InMemoryResourceManager<Integer, String>();
    }

    @Test
    public void testInsert() throws ResourceException{
        manager.storeResource(1, "One");
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
