package org.globus.crux.stateful.jpa;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.api.mockito.PowerMockito;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.mockito.Mock;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityManager;

/**
 * @author turtlebender
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(OpenJPAPersistence.class)
public class OpenJPAResourceIdentityProviderTest {

    @Mock
    EntityManagerFactory emf;

    @Mock
    EntityManager em;

    @Mock
    OpenJPAEntityManager ojem;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(OpenJPAPersistence.class);
    }

    @Test
    public void testAccepts() {
        when(emf.createEntityManager()).thenReturn(em);
        when(OpenJPAPersistence.isManagedType(em, MyPersistentStatefulBean.class)).thenReturn(true);
        when(OpenJPAPersistence.isManagedType(em, Object.class)).thenReturn(false);
        OpenJPAResourceIdentityProvider provider = new OpenJPAResourceIdentityProvider();
        provider.setEmf(this.emf);
        assertTrue(provider.accepts(MyPersistentStatefulBean.class));
        assertFalse(provider.accepts(Object.class));
        assertEquals(this.emf, provider.getEmf());
    }

    @Test
    public void testGetResourceId() {
        MyPersistentStatefulBean bean = new MyPersistentStatefulBean();
        bean.setId(10);
        when(emf.createEntityManager()).thenReturn(em);
        when(OpenJPAPersistence.cast(em)).thenReturn(this.ojem);
        when(ojem.getObjectId(bean)).thenReturn(bean.getId());
        OpenJPAResourceIdentityProvider provider = new OpenJPAResourceIdentityProvider();
        provider.setEmf(this.emf);
        assertEquals(bean.getId(), provider.getResourceId(bean));
        assertNull(provider.getResourceId(new Object()));
        verify(emf, times(2)).createEntityManager();
                
    }
}
