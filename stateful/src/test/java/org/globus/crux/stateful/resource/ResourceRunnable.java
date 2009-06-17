package org.globus.crux.stateful.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.globus.crux.stateful.SampleBean;
import org.globus.crux.stateful.TestStateAdapter;
import static org.testng.AssertJUnit.assertEquals;

import java.util.concurrent.Callable;

/**
 * @author turtlebender
 *         User: turtlebender
 *         Date: Jun 16, 2009
 *         Time: 4:33:06 PM
 */
public class ResourceRunnable implements Callable<String> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ResourceSampleBean<String> bean;
    private TestStateAdapter<Integer> adapter;
    private String value;
    private int id;

    public ResourceRunnable(ResourceSampleBean<String> bean, int id, String value, TestStateAdapter<Integer> adapter) {
        this.bean = bean;
        this.value = value;
        this.id = id;
        this.adapter = adapter;
        logger.info("Client Id: " + id);
    }

    public String call() throws Exception {
        adapter.addState(id);
        String value = bean.getState();
        int id = bean.getKey();
//        logger.info("bean state = {}", bean.getState());
        assertEquals(this.id, id);
        assertEquals(this.value, value);
        return value;
    }
}
