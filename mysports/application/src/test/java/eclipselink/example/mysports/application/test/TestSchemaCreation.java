/*******************************************************************************
 * Copyright (c) 2010-2013 Oracle. All rights reserved.
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
package eclipselink.example.mysports.application.test;

import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eclipselink.example.mysports.application.model.Extensible;

/**
 * Create Schema and verify using integrity checker.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
public class TestSchemaCreation {

    private static TestingLeagueRepository repository;

    /**
     * Using EclipseLink's schema generation when the
     * {@link EntityManagerFactory} is created to drop and create the tables and
     * sequences.
     */
    @Test
    public void verifySharedMySportsSchema() throws Exception {
        Server session = repository.unwrap(Server.class);

        for (ClassDescriptor desc : session.getDescriptors().values()) {
            Object result = session.executeSQL("SELECT COUNT(*) FROM " + desc.getTableName());
            Assert.assertNotNull(result);

            //ArrayRecord record = (ArrayRecord) ((Vector<Object>) result).get(0);
            //Assert.assertEquals("COUNT " + desc.getTableName(), 13, ((Number) record.getValues().get(0)).intValue());

            if (Extensible.class.isAssignableFrom(desc.getJavaClass())) {
                Assert.assertNotNull(desc.getMappingForAttributeName("flex1"));
                session.executeSQL("SELECT FLEX_1 FROM " + desc.getTableName());
                Assert.assertNotNull(desc.getMappingForAttributeName("flex2"));
                session.executeSQL("SELECT FLEX_2 FROM " + desc.getTableName());
                Assert.assertNotNull(desc.getMappingForAttributeName("flex3"));
                session.executeSQL("SELECT FLEX_3 FROM " + desc.getTableName());
                Assert.assertNotNull(desc.getMappingForAttributeName("flex4"));
                session.executeSQL("SELECT FLEX_4 FROM " + desc.getTableName());
                Assert.assertNotNull(desc.getMappingForAttributeName("flex5"));
                session.executeSQL("SELECT FLEX_5 FROM " + desc.getTableName());
            }
        }
    }


    /**
     * Verify the schema using the integrity checker to compare database
     * structure to that expected in the mappings.
     */
    @Test
    public void integrityCheckSharedMySportsSchema() {
        Map<String, Object> properties = repository.get();

        properties.put(PersistenceUnitProperties.SESSION_CUSTOMIZER, EnableIntegrityChecker.class.getName());
        properties.put(PersistenceUnitProperties.DEPLOY_ON_STARTUP, Boolean.TRUE.toString());
        properties.put(PersistenceUnitProperties.MULTITENANT_SHARED_EMF, Boolean.TRUE.toString());
        properties.put("javax.persistence.provider", "org.eclipse.persistence.jpa.PersistenceProvider");

        repository.setLeagueId("ALL", properties);

        Server session = repository.unwrap(Server.class);

        IntegrityChecker ic = session.getIntegrityChecker();
        Assert.assertNotNull(ic);
        Assert.assertTrue(ic.getCaughtExceptions().isEmpty());
    }

    @BeforeClass
    public static void createLeagueRepository() {
        repository = new TestingLeagueRepository();
        repository.setLeagueId("OSL", null);
    }

    @AfterClass
    public static void closeLeagueRepository() {
        if (repository != null) {
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
