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
 *  dclarke - Employee Demo 2.4.2
 ******************************************************************************/
package eclipselink.example.jpa.employee.test;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 * Persistence testing helper which creates an EMF providing testing overrides
 * to use direct JDBC instead of a data source
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
public class PersistenceTesting {

    public static EntityManagerFactory createEMF(boolean replaceTables) {
        Map<String, Object> props = new HashMap<String, Object>();

        // Ensure the persistence.xml provided data source are ignored for Java
        // SE testing
        props.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, "");
        props.put(PersistenceUnitProperties.JTA_DATASOURCE, "");
        props.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");
        
        // Configure the use of embedded derby for the tests allowing system
        // properties of the same name to override
        setProperty(props, PersistenceUnitProperties.JDBC_DRIVER, "org.apache.derby.jdbc.EmbeddedDriver");
        setProperty(props, PersistenceUnitProperties.JDBC_URL, "jdbc:derby:target/derby/mysports;create=true");
        setProperty(props, PersistenceUnitProperties.JDBC_USER, "app");
        setProperty(props, PersistenceUnitProperties.JDBC_PASSWORD, "app");

        // Ensure weaving is used
        props.put(PersistenceUnitProperties.WEAVING, "true");

        if (replaceTables) {
            props.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
            props.put(PersistenceUnitProperties.DDL_GENERATION_MODE, PersistenceUnitProperties.DDL_DATABASE_GENERATION);
        }

        return Persistence.createEntityManagerFactory("employee", props);
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
