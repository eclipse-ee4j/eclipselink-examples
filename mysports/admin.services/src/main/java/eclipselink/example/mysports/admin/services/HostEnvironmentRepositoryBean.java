package eclipselink.example.mysports.admin.services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import eclipselink.example.mysports.admin.model.HostEnvironment;

/**
 * Repository providing the admin application with read and write access for
 * {@link HostEnvironment} definitions.
 * 
 * @author dclarke
 * @since EclipseLInk 2.4
 */
@Stateless
public class HostEnvironmentRepositoryBean implements HostEnvironmentRepository {

    @PersistenceContext(unitName = "MySportsAdmin")
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // TODO: maintain a list versus query every time?
    public List<HostEnvironment> getEnvironments() {
        return getEntityManager().createNamedQuery("HostEnvironment.all", HostEnvironment.class).getResultList();
    }

    @Override
    public void create(HostEnvironment env) {
        getEntityManager().persist(env);
    }

    @Override
    public void delete(HostEnvironment env) {
        getEntityManager().remove(env);
    }

    @Override
    public HostEnvironment find(String name) {
        // TODO Auto-generated method stub
        return null;
    }
}
