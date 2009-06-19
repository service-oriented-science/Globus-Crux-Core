package org.globus.crux.stateful.resource;

import org.globus.crux.stateful.StatefulServiceFactoryBean;
import org.globus.crux.stateful.TestStateAdapter;
import org.globus.crux.stateful.StateAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.HashMap;

import static org.testng.AssertJUnit.*;
import static org.mockito.Mockito.*;

/**
 * @author turtlebender
 *         User: turtlebender
 *         Date: Jun 16, 2009
 *         Time: 12:44:41 PM
 */
@Test
public class ResourceStatefulServiceFactoryBeanTest {
    StatefulServiceFactoryBean<ResourceSampleBean, Integer> factory;
    @Mock ResourceManager<Integer, Object> manager;
    @Mock StateAdapter<Integer> adapter;
    Logger logger = LoggerFactory.getLogger(getClass());
    Map<Integer, String> resources = new HashMap<Integer, String>();

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
        MockitoAnnotations.initMocks(this);
    }

    public void testStoreGet() throws Exception {
        when(manager.findResource(0)).thenReturn(resources.get(0));
        when(adapter.getState()).thenReturn(0);
        ResourceSampleBean<String> bean = new DefaultResourceSampleBean<String>();
        factory.setTarget(bean);
        factory.setStateAdapter(this.adapter);
        factory.setResourceManager(this.manager);
        bean = factory.getStatefulService();
        assertEquals(resources.get(0), bean.getState());
        verify(manager, times(1)).findResource(0);
    }
}
