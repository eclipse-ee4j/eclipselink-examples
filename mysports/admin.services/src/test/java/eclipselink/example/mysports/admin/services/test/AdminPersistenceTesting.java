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
 *  dclarke - EclipseLink 2.4 - MySports Demo Bug 344608
 ******************************************************************************/
package eclipselink.example.mysports.admin.services.test;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.tools.schemaframework.DefaultTableGenerator;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.TableCreator;

import eclipselink.example.mysports.admin.examples.ExampleLeagueDefinition;
import eclipselink.example.mysports.admin.services.AdminSchemaManager;
import eclipselink.example.mysports.admin.services.HostedLeagueRepository;

public class AdminPersistenceTesting {

    public static Map<String, Object> add(Map<String, Object> properties) {
        Map<String, Object> props = properties;

        if (props == null) {
            props = new HashMap<String, Object>();
        }
        // Ensure the persistence.xml provided data source are ignored for Java
        // SE testing
        props.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, "");
        props.put(PersistenceUnitProperties.JTA_DATASOURCE, "");

        // Configure the use of embedded derby for the tests allowing system
        // properties of the same name to override
        setProperty(props, PersistenceUnitProperties.JDBC_DRIVER, "org.apache.derby.jdbc.EmbeddedDriver");
        setProperty(props, PersistenceUnitProperties.JDBC_URL, "jdbc:derby:target/derby/mysports;create=true");
        setProperty(props, PersistenceUnitProperties.JDBC_USER, "app");
        setProperty(props, PersistenceUnitProperties.JDBC_PASSWORD, "app");

        // Ensure weaving is used
        props.put(PersistenceUnitProperties.WEAVING, "true");

        return props;
    }

    public static Map<String, Object> get() {
        return add(null);
    }

    public static HostedLeagueRepository createTestRepository(boolean replaceSchema, boolean populate) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("MySportsAdmin", get());
        if (replaceSchema) {
            AdminSchemaManager.createTables(emf);
        }
        HostedLeagueRepository repository = (HostedLeagueRepository) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { HostedLeagueRepository.class }, new HostedLeagueRepositoryHandler(emf));

        if (populate) {
            ExampleLeagueDefinition.populateAll(repository);
        }
        return repository;
    }

    public static HostedLeagueRepository createTestRepository(boolean replaceSchema) {
        return createTestRepository(replaceSchema, true);
    }

    public static EntityManagerFactory getEMF(HostedLeagueRepository repository) {
        HostedLeagueRepositoryHandler handler = (HostedLeagueRepositoryHandler) Proxy.getInvocationHandler(repository);
        return handler.getEmf();
    }

    public static void closeTestingRepository(HostedLeagueRepository repository, boolean dropTables) {
        EntityManagerFactory emf = getEMF(repository);
        if (dropTables) {
            dropTables(emf);
        }
        emf.close();
    }

    public static void dropTables(EntityManagerFactory emf) {
        Server session = JpaHelper.getServerSession(emf);
        SchemaManager sm = new SchemaManager(session);

        TableCreator tc = new DefaultTableGenerator(session.getProject(), true).generateDefaultTableCreator();
        tc.setIgnoreDatabaseException(true);
        tc.dropTables(session, sm, true);
    }


    /**
     * Add the system property value if it exists, otherwise use the default
     * value.
     */
    private static void setProperty(Map<String, Object> props, String key, String defaultValue) {
        String value = defaultValue;
        if (System.getProperties().containsKey(key)) {
            value = System.getProperty(key);
        }
        props.put(key, value);
    }

}
