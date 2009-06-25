package org.globus.crux.stateful.jpa;

import org.globus.crux.stateful.resource.ResourceIdentityProvider;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.OpenJPAEntityManager;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityManager;

/**
 * Determines the Id for objects which are PersistenceCapable via JPA.  Specifically,
 * this works on classes which have been enhanced via the OpenJPA library.  Classes which
 * work with other JPA implementations will not work with this Provider.
 *
 * @author Tom Howe
 * @since 1.0
 * @version 1.0
 */
public class OpenJPAResourceIdentityProvider implements ResourceIdentityProvider{
    private EntityManagerFactory emf;

    /**
     * Get the id for the resource.  This uses a number of OpenJPA specific tools, so this
     * will not work with other JPA implementations.
     *
     * @param resource The resource
     * @return The id for the resource.
     */
    public Object getResourceId(Object resource) {
        EntityManager em = emf.createEntityManager();
        //cast the generic EntityManager into an OpenJPA manager
        OpenJPAEntityManager ojem = OpenJPAPersistence.cast(em);
        return ojem.getObjectId(resource);        
    }

    /**
     * This Provider accepts classes which are managed by OpenJPA.
     *
     * @param resourceType Type of resource
     * @return True if class is managed by OpenJPA.
     */
    public boolean accepts(Class resourceType) {
        return OpenJPAPersistence.isManagedType(emf.createEntityManager(), resourceType);
    }

    public EntityManagerFactory getEmf() {
        return emf;
    }

    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }
}
