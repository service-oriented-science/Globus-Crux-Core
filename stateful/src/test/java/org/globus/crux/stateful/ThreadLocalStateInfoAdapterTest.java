package org.globus.crux.stateful;

import org.testng.annotations.Test;
import org.globus.crux.stateful.internal.ThreadLocalStateInfoAdapter;
import static org.testng.AssertJUnit.*;

/**
 * @author turtlebender
 */
@Test(groups = {"stateful", "unit"})
public class ThreadLocalStateInfoAdapterTest {

    public void testGet(){
        ThreadLocalStateInfoAdapter<Object> adapter = new ThreadLocalStateInfoAdapter<Object>();
        Object testObject = new Object();
        adapter.set(testObject);
        assertEquals(testObject, adapter.get());
        assertEquals(testObject, adapter.getState());
    }
}
