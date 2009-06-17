package org.globus.crux.stateful.resource;

import org.globus.crux.stateful.StatefulServiceFactoryBean;
import org.globus.crux.stateful.TestRunnable;
import org.globus.crux.stateful.TestStateAdapter;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
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
 * @author turtlebender
 *         User: turtlebender
 *         Date: Jun 16, 2009
 *         Time: 12:44:41 PM
 */
@Test
public class ResourceStatefulServiceFactoryBeanTest {
    StatefulServiceFactoryBean<ResourceSampleBean, Integer> factory;
    ResourceManager<Integer, Object> manager;
    TestStateAdapter<Integer> adapter;
    Mockery context;
    Logger logger = LoggerFactory.getLogger(getClass());
    Map<Integer, String> resources = new ConcurrentHashMap<Integer, String>();

    @BeforeTest
    public void init() {
        resources.put(0, "Zero");
        resources.put(1, "One");
        resources.put(2, "Two");
        resources.put(3, "Three");
        resources.put(4, "Four");
    }

    @BeforeMethod
    public void setup() throws Exception {
        factory = new StatefulServiceFactoryBean<ResourceSampleBean, Integer>();
        adapter = new TestStateAdapter<Integer>();
        context = new Mockery();
        manager = context.mock(ResourceManager.class);
        context.checking(new Expectations() {{
            for(int i = 0 ; i < 5 ; i++){
                oneOf(manager).findResource(i);
                will(returnValue(resources.get(i)));
            }
        }});
    }

    public void testStoreGet() throws Exception {
        ExecutorService exec = Executors.newFixedThreadPool(5);
        ResourceSampleBean<String> bean = new DefaultResourceSampleBean<String>();
        factory.setTarget(bean);
        factory.setStateAdapter(this.adapter);
        factory.setResourceManager(this.manager);
        bean = factory.getStatefulService();
        List<Callable<String>> requests = new ArrayList<Callable<String>>();
        for (int x = 0; x < 5; x++) {
            ResourceRunnable client = new ResourceRunnable(bean, x, this.resources.get(x), this.adapter);
            requests.add(client);
        }
        exec.invokeAny(requests);
    }
}
