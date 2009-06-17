package org.globus.crux.stateful;

import net.sf.cglib.proxy.Callback;

import java.util.concurrent.Callable;

import static org.testng.AssertJUnit.assertEquals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author turtlebender
 *         User: turtlebender
 *         Date: Jun 16, 2009
 *         Time: 12:58:12 PM
 */
public class TestRunnable implements Callable<Integer> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private SampleBean<Integer> bean;
    private TestStateAdapter<Integer> adapter;
    private int id;

    public TestRunnable(SampleBean<Integer> bean, int id, TestStateAdapter<Integer> adapter) {
        this.bean = bean;
        this.id = id;
        this.adapter = adapter;
        logger.info("Client Id: " + id);
    }

    public Integer call() throws Exception {
        adapter.addState(id);
//        logger.info("bean state = {}", bean.getState());
        assertEquals(id, bean.getState().intValue());
        return id;
    }
}
