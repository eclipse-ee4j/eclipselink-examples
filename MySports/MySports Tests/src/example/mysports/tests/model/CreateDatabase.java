/*******************************************************************************
 * Copyright (c) 2010-2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  dclarke - EclipseLink 2.3 - MySports Demo Bug 344608
 ******************************************************************************/
package example.mysports.tests.model;

import java.util.Map;
import java.util.Vector;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.Assert;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.internal.sessions.ArrayRecord;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import example.mysports.MySportsConfig;
import example.mysports.ejb.LeagueRepository;
import example.mysports.tests.TestingLeagueRepository;
import example.mysports.tests.admin.MockAdminServerConnector;

/**
 * Create Schema and verify using integrity checker.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
public class CreateDatabase {

    private static LeagueRepository repository;

    /**
     * Using EclipseLink's schema generation when the
     * {@link EntityManagerFactory} is created to drop and create the tables and
     * sequences.
     */
    @Test
    public void createSharedMySportsSchema() throws Exception {
        Map<String, Object> properties = TestingLeagueRepository.get();

        properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
        properties.put(PersistenceUnitProperties.DDL_GENERATION_MODE, PersistenceUnitProperties.DDL_DATABASE_GENERATION);
        properties.put(PersistenceUnitProperties.ALLOW_NATIVE_SQL_QUERIES, "true");

        repository.setLeagueId("ALL", properties);

        Server session = repository.unwrap(Server.class);

        for (ClassDescriptor desc : session.getDescriptors().values()) {
            Object result = session.executeSQL("SELECT COUNT(*) FROM " + desc.getTableName());
            Assert.assertNotNull(result);
            @SuppressWarnings("unchecked")
            ArrayRecord record = (ArrayRecord) ((Vector<Object>) result).get(0);
            Assert.assertEquals(0, ((Number) record.getValues().get(0)).intValue());
        }
    }

    @Test
    public void createMHLMySportsSchema() throws Exception {
        Map<String, Object> properties = TestingLeagueRepository.get("MHL");

        properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
        properties.put(PersistenceUnitProperties.DDL_GENERATION_MODE, PersistenceUnitProperties.DDL_DATABASE_GENERATION);
        properties.put(PersistenceUnitProperties.ALLOW_NATIVE_SQL_QUERIES, "true");

        repository.setLeagueId("MHL", properties);

        Server session = repository.unwrap(Server.class);

        for (ClassDescriptor desc : session.getDescriptors().values()) {
            Object result = session.executeSQL("SELECT COUNT(*) FROM " + desc.getTableName());
            Assert.assertNotNull(result);
            @SuppressWarnings("unchecked")
            ArrayRecord record = (ArrayRecord) ((Vector<Object>) result).get(0);
            Assert.assertEquals(0, ((Number) record.getValues().get(0)).intValue());
        }
    }

    /**
     * Verify the schema using the integrity checker to compare database
     * structure to that expected in the mappings.
     */
    @Test
    public void verifySharedMySportsSchema() {
        Map<String, Object> properties = TestingLeagueRepository.get();

        properties.put(PersistenceUnitProperties.SESSION_CUSTOMIZER, EnableIntegrityChecker.class.getName());
        properties.put(PersistenceUnitProperties.DEPLOY_ON_STARTUP, Boolean.TRUE.toString());

        EntityManagerFactory emf = null;

        try {
            emf = Persistence.createEntityManagerFactory(MySportsConfig.PU_NAME, properties);

            IntegrityChecker ic = JpaHelper.getServerSession(emf).getIntegrityChecker();
            Assert.assertNotNull(ic);
            Assert.assertTrue(ic.getCaughtExceptions().isEmpty());
        } finally {
            if (emf != null) {
                emf.close();
            }
        }
    }

    @BeforeClass
    public static void createLeagueRepository() {
        EntityManagerFactory adminEMF = Persistence.createEntityManagerFactory("mysports-admin", TestingLeagueRepository.get());
        MySportsConfig config = new MySportsConfig();
        repository = new TestingLeagueRepository(config);
        ((MockAdminServerConnector) config.getAdminConnector()).setEMF(adminEMF);
    }

    @AfterClass
    public static void closeLeagueRepository() {
        if (repository != null) {
            MySportsConfig config = repository.getConfig();
            ((MockAdminServerConnector) config.getAdminConnector()).getEMF().close();
            repository.close();
        }
    }

    /**
     * {@link SessionCustomizer} that enables the {@link IntegrityChecker}.
     * 
     * NOTE: If another {@link SessionCustomizer} is specified in the
     * persistence.xml this one will replace it.
     */
    public static class EnableIntegrityChecker implements SessionCustomizer {

        public void customize(Session session) throws Exception {
            IntegrityChecker ic = new IntegrityChecker();
            ic.setShouldCheckDatabase(true);
            session.setIntegrityChecker(ic);
        }

    }

}
