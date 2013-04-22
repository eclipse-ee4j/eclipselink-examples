/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dclarke - Dynamic Persistence INCUBATION - Enhancement 200045
 *               http://wiki.eclipse.org/EclipseLink/Development/JPA/Dynamic
 *     
 * This code is being developed under INCUBATION and is not currently included 
 * in the automated EclipseLink build. The API in this code may change, or 
 * may never be included in the product. Please provide feedback through mailing 
 * lists or the bug database.
 ******************************************************************************/
package tests;

import static example.PersistenceHelper.EMPLOYEE_API_PU;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.junit.Test;

import example.EmployeeDynamicMappings;
import example.Queries;
import example.Samples;
import example.Transactions;

public class TestCreatePUUsingAPI {

    @Test
    public void runDynamicAPITest() {
        // Create a dynamic class loader and create the types.
        DynamicClassLoader dcl = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
        DynamicType[] types = EmployeeDynamicMappings.createTypes(dcl, "example.jpa.dynamic.model.employee");

        // Create an entity manager factory.
        EntityManagerFactory emf = createEntityManagerFactory(dcl, EMPLOYEE_API_PU, true);

        // Create JPA Dynamic Helper (with the emf above) and after the types
        // have been created and add the types through the helper.
        JPADynamicHelper helper = new JPADynamicHelper(emf);
        helper.addTypes(true, true, types);

        // Create database and populate
        new SchemaManager(helper.getSession()).replaceDefaultTables();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        new Samples(emf).persistAll(em);
        em.getTransaction().commit();
        em.clear();

        // Lookup types
        DynamicType empType = helper.getType("Employee");

        // Run Queries
        Queries queries = new Queries();

        int minEmpId = queries.minimumEmployeeId(em);
        queries.findEmployee(em, empType, minEmpId);
        queries.findEmployeesUsingGenderIn(em);

        // Example transactions
        Transactions txn = new Transactions();

        txn.createUsingPersist(em);

        em.close();
        emf.close();
    }

    private EntityManagerFactory createEntityManagerFactory(DynamicClassLoader dcl, String persistenceUnit, boolean createTables) {
        Map<String, Object> props = new HashMap<String, Object>();

        // Ensure the persistence.xml provided data source are ignored for Java
        // SE testing
        props.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, "");
        props.put(PersistenceUnitProperties.JTA_DATASOURCE, "");
        
        if (createTables) {
            props.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
            props.put(PersistenceUnitProperties.DDL_GENERATION_MODE, PersistenceUnitProperties.DDL_DATABASE_GENERATION);
        }

        // Configure the use of embedded derby for the tests allowing system
        // properties of the same name to override
        props.put(PersistenceUnitProperties.JDBC_DRIVER, "org.apache.derby.jdbc.EmbeddedDriver");
        props.put(PersistenceUnitProperties.JDBC_URL, "jdbc:derby:target/derby/dynamic-api;create=true");
        props.put(PersistenceUnitProperties.JDBC_USER, "app");
        props.put(PersistenceUnitProperties.JDBC_PASSWORD, "app");
        props.put(PersistenceUnitProperties.CLASSLOADER, dcl);
        props.put(PersistenceUnitProperties.WEAVING, "static");
        props.put(PersistenceUnitProperties.LOGGING_LEVEL, SessionLog.FINE_LABEL);
        
        SEPersistenceUnitInfo info = new SEPersistenceUnitInfo();
        info.setClassLoader(dcl);
        info.setPersistenceUnitName("test");
        Properties p = new Properties();
        p.putAll(props);
        info.setProperties(p);
        
        EntityManagerSetupImpl setup = new EntityManagerSetupImpl("test", "test");
        setup.predeploy(info, props);
        DatabaseSessionImpl sessionImpl = setup.deploy(dcl, props);
        return new EntityManagerFactoryImpl(sessionImpl);
    }

}
