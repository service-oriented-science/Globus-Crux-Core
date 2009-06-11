package org.globus.crux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
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
    StatefulServiceFactoryBean<MySampleBean, Integer> factory;
    MySampleBean bean;
    TestStateAdapter adapter;

    Logger logger = LoggerFactory.getLogger(getClass());

    @BeforeTest
    public void init() throws Exception {
        factory = new StatefulServiceFactoryBean<MySampleBean, Integer>();
        adapter = new TestStateAdapter();
    }

    /**
     * Method: getObject()
     */
    public void testGetObject() throws Exception {
        ExecutorService exec = Executors.newFixedThreadPool(5);
        MySampleBean bean = new MySampleBean();
        factory.setTarget(bean);
        factory.setStateAdapter(this.adapter);
        bean = factory.getStatefulService();
        List<Callable<Integer>> requests = new ArrayList<Callable<Integer>>();
        for (int x = 0; x < 5; x++) {
            TestRunnable client = new TestRunnable(bean, x);
            requests.add(client);
        }
        exec.invokeAny(requests);
    }

    class TestRunnable implements Callable<Integer> {
        MySampleBean bean;
        int id;

        public TestRunnable(MySampleBean bean, int id) {
            this.bean = bean;
            this.id = id;
            logger.info("Client Id: " + id);
        }

        public Integer call() throws Exception {
            StatefulServiceFactoryBeanTest.this.adapter.addState(id);
            logger.info("bean state = {}", bean.getState());
            assertEquals((Integer) id, bean.getState());
            return id;
        }
    }

    class TestStateAdapter implements StateAdapter<Integer> {
        Map<Thread, Integer> stateMap = new ConcurrentHashMap<Thread, Integer>();

        public Integer getState() {
            return stateMap.get(Thread.currentThread());
        }

        public void addState(Integer state) {
            stateMap.put(Thread.currentThread(), state);
        }
    }

}
