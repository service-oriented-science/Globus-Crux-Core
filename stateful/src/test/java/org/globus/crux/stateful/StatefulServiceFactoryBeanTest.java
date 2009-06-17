package org.globus.crux.stateful;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * StatefulServiceFactoryBean Tester.
 *
 * @author Tom Howe
 * @version 1.0
 * @since <pre>06/09/2009</pre>
 */
@Test
public class StatefulServiceFactoryBeanTest {
    StatefulServiceFactoryBean<SampleBean, Integer> factory;
    MySampleBean bean;
    TestStateAdapter<Integer> adapter;

    Logger logger = LoggerFactory.getLogger(getClass());

    @BeforeTest
    public void init() throws Exception {
        factory = new StatefulServiceFactoryBean<SampleBean, Integer>();
        adapter = new TestStateAdapter<Integer>();
    }

    /**
     * Method: getObject()
     */
    public void testGetObject() throws Exception {
        ExecutorService exec = Executors.newFixedThreadPool(5);
        SampleBean bean = new MySampleBean();
        factory.setTarget(bean);
        factory.setStateAdapter(this.adapter);
        bean = factory.getStatefulService();
        List<Callable<Integer>> requests = new ArrayList<Callable<Integer>>();
        for (int x = 0; x < 5; x++) {
            TestRunnable client = new TestRunnable(bean, x, this.adapter);
            requests.add(client);
        }
        exec.invokeAny(requests);
    }
}
