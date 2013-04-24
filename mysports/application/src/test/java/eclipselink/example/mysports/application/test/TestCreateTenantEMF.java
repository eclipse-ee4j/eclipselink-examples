package eclipselink.example.mysports.application.test;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.QueryException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eclipselink.example.mysports.application.MySportsConfig;
import eclipselink.example.mysports.application.model.Player;
import eclipselink.example.mysports.examples.ExampleLeague;
import eclipselink.example.mysports.examples.OttawaSoccerLeague;

public class TestCreateTenantEMF {

    private static ExampleLeague LEAGUE = new OttawaSoccerLeague();

    private static TestingLeagueRepository repository;

    @Test
    public void createEMWithoutLeague() throws Exception {
        Map<String, Object> properties = repository.get();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(MySportsConfig.PU_NAME, properties);

        Assert.assertNotNull(emf);

        EntityManager em = emf.createEntityManager();

        try {
            em.find(Player.class, 1);
        } catch (QueryException e) {
            return;
        } finally {
            em.close();
            emf.close();
        }
        // Assert.fail();
    }

    @Test
    public void createKFL() throws Exception {
        Map<String, Object> properties = repository.get();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(MySportsConfig.PU_NAME + ";league=KFL", properties);

        Assert.assertNotNull(emf);
        EntityManager em = emf.createEntityManager();

        try {
            em.find(Player.class, 1);
        } catch (QueryException e) {
            return;
        } finally {
            em.close();
            emf.close();
        }
    }

    @Test
    public void createEMWithInvalidLeague() throws Exception {
        Map<String, Object> properties = repository.get();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(MySportsConfig.PU_NAME + ";league=XXX", properties);

        Assert.assertNotNull(emf);
        EntityManager em = emf.createEntityManager();

        try {
            em.find(Player.class, 1);
        } catch (QueryException e) {
            return;
        } finally {
            em.close();
            emf.close();
        }
    }

    @Test
    public void createEM_OSL_WithNullMetadataSource() throws Exception {
        Map<String, Object> properties = repository.get();
        properties.put(PersistenceUnitProperties.METADATA_SOURCE, null);

        EntityManagerFactory emf = Persistence.createEntityManagerFactory(MySportsConfig.PU_NAME + ";league=OSL", properties);

        Assert.assertNotNull(emf);
        EntityManager em = emf.createEntityManager();

        try {
            em.find(Player.class, 1);
        } catch (QueryException e) {
            return;
        } finally {
            em.close();
            emf.close();
        }
    }

    @BeforeClass
    public static void setup() {
        repository = new TestingLeagueRepository(LEAGUE.getId());
        repository.createSharedMySportsSchema();
        LEAGUE.populate(repository);
    }

    @AfterClass
    public static void tearDown() {
        repository.close();
    }

}
