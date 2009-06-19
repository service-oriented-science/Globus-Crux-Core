package org.globus.crux.stateful;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.testng.AssertJUnit.assertEquals;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

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
    @Mock StateAdapter<Integer> adapter;

    Logger logger = LoggerFactory.getLogger(getClass());

    @BeforeMethod
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        factory = new StatefulServiceFactoryBean<SampleBean, Integer>();
    }

    /**
     * Method: getObject()
     */
    public void testGetObject() throws Exception {
        SampleBean bean = new MySampleBean();
        factory.setTarget(bean);
        factory.setStateAdapter(this.adapter);
        bean = factory.getStatefulService();
        //Create mock assumptions
        when(adapter.getState()).thenReturn(0);
        //Test assumptions
        assertEquals(0, adapter.getState().intValue());
        //Verify executions
        verify(adapter, times(1)).getState();
    }
}
