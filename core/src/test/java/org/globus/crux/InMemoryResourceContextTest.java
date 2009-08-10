package org.globus.crux;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import org.globus.crux.service.ResourceStoreException;

/**
 * @author turtlebender
 */
public class InMemoryResourceContextTest {
    private DefaultResourceContext<Object, Object> context = new DefaultResourceContext<Object,Object>();

//    @Test
//    public void testAdd() throws ResourceStoreException {
//        Object key = new Object();
//        Object value = new Object();
//        context.storeResource(key, value);
//        assertEquals(context.getResource(key), value);
//    }
//
//    @Test
//    public void testCurrentResource() throws ResourceStoreException {
//        Object key = new Object();
//        Object value = new Object();
//        context.storeResource(key, value);
//        context.setCurrentResourceKey(key);
//        assertEquals(context.getCurrentResource(), value);
//    }
}
