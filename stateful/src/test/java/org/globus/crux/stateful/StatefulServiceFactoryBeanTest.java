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

/**
 * StatefulServiceFactoryBean Tester.
 *
 * @author Tom Howe
 * @version 1.0
 * @since <pre>06/09/2009</pre>
 */
@Test(groups = {"unit", "stateful"})
public class StatefulServiceFactoryBeanTest {
    StatefulServiceFactoryBean<SampleBean, Integer> factory;
    MySampleBean bean;
    @Mock
    StateAdapter<Integer> adapter;

    Logger logger = LoggerFactory.getLogger(getClass());

    @BeforeMethod
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        factory = new StatefulServiceFactoryBean<SampleBean, Integer>();
    }

    class DummyException extends Exception{}

    @Test(expectedExceptions = StatefulServiceException.class)
    @SuppressWarnings("unchecked")
    public void testAspectAccessors() throws Throwable{
        AbstractServiceMetadata<Integer> asm = mock(AbstractServiceMetadata.class);
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        StatefulServiceAspect<Integer> aspect = new StatefulServiceAspect<Integer>(asm, this.adapter);
        aspect.setStateAdapter(adapter);
        doReturn(new Object()).doThrow(new DummyException()).when(pjp).proceed();
        assertEquals(adapter, aspect.getStateAdapter());
        aspect.instantiateState(pjp);
        verify(pjp, times(1)).proceed();
        aspect.instantiateState(pjp);                
    }

    /**
     * Method: getObject()
     */
    public void testGetObject() throws Exception {
        SampleBean bean = new MySampleBean();
        factory.setTarget(bean);
        factory.setStateAdapter(this.adapter);
        bean = factory.getStatefulService();
        assertEquals(this.adapter, factory.getStateAdapter());
        assertEquals(bean, factory.getStatefulService());
        assertEquals(bean, factory.getTarget());
        assertEquals(bean, factory.getObject());
        assertEquals(MySampleBean.class, factory.getObjectType());
        assertTrue(factory.isSingleton());
        //Create mock assumptions
        when(adapter.getState()).thenReturn(0);
        //Test assumptions
        assertEquals(0, adapter.getState().intValue());
        //Verify executions
        verify(adapter, times(1)).getState();
    }
}
