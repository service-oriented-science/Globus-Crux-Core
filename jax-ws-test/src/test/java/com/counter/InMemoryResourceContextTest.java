package com.counter;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

/**
 * @author turtlebender
 */
public class InMemoryResourceContextTest {
    private InMemoryResourceContext<Object, Object> context = new InMemoryResourceContext<Object,Object>();

    @Test
    public void testAdd(){
        Object key = new Object();
        Object value = new Object();
        context.storeResource(key, value);
        assertEquals(context.getResource(key), value);
    }

    @Test
    public void testCurrentResource(){
        Object key = new Object();
        Object value = new Object();
        context.storeResource(key, value);
        context.setCurrentResourceKey(key);
        assertEquals(context.getCurrentResource(), value);
    }
}
