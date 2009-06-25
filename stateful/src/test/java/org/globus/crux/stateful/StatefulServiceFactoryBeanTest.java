package org.globus.crux.stateful;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.testng.AssertJUnit.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import org.aspectj.lang.ProceedingJoinPoint;
import org.globus.crux.stateful.resource.ResourceManager;

/**
 * StatefulServiceFactoryBean Tester.
 *
 * @author Tom Howe
 * @version 1.0
 * @since <pre>06/09/2009</pre>
 */
@Test(groups = {"unit", "stateful"})
public class StatefulServiceFactoryBeanTest {
    StatefulServiceFactoryBean<SampleBean, Integer, String> factory;
    MySampleBean bean;
    @Mock
    StateAdapter<Integer> adapter;
    @Mock
    ResourceManager<Integer, String> rm;

    Logger logger = LoggerFactory.getLogger(getClass());

    @BeforeMethod
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        factory = new StatefulServiceFactoryBean<SampleBean, Integer, String>();
    }

    class DummyException extends Exception {
    }

    @Test(expectedExceptions = StatefulServiceException.class)
    @SuppressWarnings("unchecked")
    public void testAspectAccessors() throws Throwable {
        StatefulService ss = mock(StatefulService.class);
        when(ss.autoCommit()).thenReturn(true);
        ServiceMetadata<String> asm = mock(ServiceMetadata.class);
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        StatefulServiceAspect<Integer, String> aspect =
                new StatefulServiceAspect<Integer, String>(asm, this.adapter);
        aspect.setStateAdapter(adapter);
        aspect.setResourceManager(this.rm);
        doReturn(new Object()).doThrow(new DummyException()).when(pjp).proceed();
        assertEquals(adapter, aspect.getStateAdapter());
        aspect.instantiateState(pjp, ss);
        verify(pjp, times(1)).proceed();
        aspect.instantiateState(pjp, ss);
    }


    public void testGetObjectNoAC() throws Exception {
        SampleBean bean = new MySampleBeanNoAutoCommit();
        factory.setTarget(bean);
        factory.setStateAdapter(this.adapter);
        factory.setResourceManager(this.rm);
        bean = factory.getStatefulService();
        assertEquals(this.adapter, factory.getStateAdapter());
        assertEquals(bean, factory.getStatefulService());
        assertEquals(bean, factory.getTarget());
        assertEquals(bean, factory.getObject());
        assertEquals(MySampleBeanNoAutoCommit.class, factory.getObjectType());
        assertTrue(factory.isSingleton());
        //Create mock assumptions
        when(adapter.getState()).thenReturn(0);
        when(rm.findResource(0)).thenReturn("TestResult");
        //Test assumptions
        assertEquals(0, adapter.getState().intValue());
        assertEquals(bean.getState(), "TestResult");
        //Verify executions
        verify(adapter, times(2)).getState();
    }

    /**
     * Method: getObject()
     */
    public void testGetObject() throws Exception {
        SampleBean bean = new MySampleBean();
        factory.setTarget(bean);
        factory.setStateAdapter(this.adapter);
        factory.setResourceManager(this.rm);
        bean = factory.getStatefulService();
        assertEquals(this.adapter, factory.getStateAdapter());
        assertEquals(bean, factory.getStatefulService());
        assertEquals(bean, factory.getTarget());
        assertEquals(bean, factory.getObject());
        assertEquals(MySampleBean.class, factory.getObjectType());
        assertTrue(factory.isSingleton());
        //Create mock assumptions
        when(adapter.getState()).thenReturn(0);
        when(rm.findResource(0)).thenReturn("TestResult");
        //Test assumptions
        assertEquals(0, adapter.getState().intValue());
        assertEquals(bean.getState(), "TestResult");
        //Verify executions
        verify(adapter, times(2)).getState();
    }
}
