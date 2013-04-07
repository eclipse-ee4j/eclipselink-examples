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
 *  dclarke - example
 ******************************************************************************/
package example;

import static org.eclipse.persistence.jaxb.MarshallerProperties.MEDIA_TYPE;
import static org.eclipse.persistence.jaxb.UnmarshallerProperties.JSON_INCLUDE_ROOT;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.queries.ConstructorReportItem;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.server.Server;

/**
 * Simple helper responsible for creation of JPA and MOXy contexts.
 * 
 * @author dclarke
 */
public class PersistenceHelper {
    
    public static final String EMPLOYEE_XML_PU = "employee-xml";

    public static final String EMPLOYEE_API_PU = "employee-api";

    public static EntityManagerFactory createEntityManagerFactory(DynamicClassLoader dcl, String persistenceUnit, boolean createTables) {
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
        props.put(PersistenceUnitProperties.JDBC_URL, "jdbc:derby:target/derby/mysports;create=true");
        props.put(PersistenceUnitProperties.JDBC_USER, "app");
        props.put(PersistenceUnitProperties.JDBC_PASSWORD, "app");
        props.put(PersistenceUnitProperties.CLASSLOADER, dcl);
        props.put(PersistenceUnitProperties.WEAVING, "static");
        return Persistence.createEntityManagerFactory(persistenceUnit, props);
    }

    private static JAXBContext context;

    public static JAXBContext getContext(EntityManager em) throws JAXBException {
        if (context == null) {
            Set<Class<?>> classes = new HashSet<Class<?>>();

            Server serverSession = em.unwrap(Server.class);

            for (List<DatabaseQuery> queryList : serverSession.getQueries().values()) {
                for (DatabaseQuery query : queryList) {
                    if (query.isReportQuery()) {
                        ReportQuery rq = (ReportQuery) query;
                        for (ReportItem item : rq.getItems()) {
                            if (item.isConstructorItem()) {
                                classes.add(((ConstructorReportItem) item).getResultType());
                            }
                        }
                    }
                }
            }

            context = JAXBContextFactory.createContext(classes.toArray(new Class[classes.size()]), null);
        }
        return context;
    }

    public static Marshaller createMarshaller(EntityManager em, MediaType mediaType) throws JAXBException {
        Marshaller marshaller = getContext(em).createMarshaller();
        marshaller.setProperty(MEDIA_TYPE, mediaType.getMediaType());
        return marshaller;
    }

    public static Unmarshaller createUnmarshaller(EntityManager em, MediaType mediaType) throws JAXBException {
        Unmarshaller unmarshaller = getContext(em).createUnmarshaller();
        unmarshaller.setProperty(MEDIA_TYPE, mediaType.getMediaType());
        unmarshaller.setProperty(JSON_INCLUDE_ROOT, false);
        return unmarshaller;
    }

}
