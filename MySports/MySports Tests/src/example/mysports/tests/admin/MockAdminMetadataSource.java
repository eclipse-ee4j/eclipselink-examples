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
package example.mysports.tests.admin;

import static example.mysports.MySportsConfig.LEAGUE_CONTEXT;

import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappingsReader;
import org.eclipse.persistence.jpa.metadata.MetadataSource;
import org.eclipse.persistence.jpa.metadata.XMLMetadataSource;
import org.eclipse.persistence.logging.SessionLog;

import example.mysports.MySportsConfig;
import example.mysports.admin.jaxrs.MappingsLoader;
import example.mysports.tests.TestingLeagueRepository;

/**
 * {@link MetadataSource} which using REST calls to retrieve the league (tenant)
 * specific mappings from the MySports Admin application.
 * <p>
 * If the Admin application can not be reached a warning message is logged and
 * the contained 'local-eclipselink-orm.xml' is returned.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
public class MockAdminMetadataSource implements MetadataSource {

    private EntityManagerFactory adminEMF;

    /**
     * Default local resource. Only used when admin service cannot be accessed.
     */
    public static final String LOCAL_ECLIPSELINK_ORM = "META-INF/local-eclipselink-orm.xml";

    @Override
    public XMLEntityMappings getEntityMappings(Map<String, Object> properties, ClassLoader classLoader, SessionLog log) {
        String leagueId = (String) properties.get(LEAGUE_CONTEXT);
        EntityManager em = getAdminEMF().createEntityManager();

        try {
            String ormxml = MappingsLoader.getORMapping(em, leagueId);
            return readMappings(ormxml, properties,classLoader);
        } finally {
            em.close();
        }

    }

    private EntityManagerFactory getAdminEMF() {
        if (this.adminEMF == null) {
            this.adminEMF = Persistence.createEntityManagerFactory("mysports-admin", TestingLeagueRepository.get());
        }
        return this.adminEMF;
    }
    
    private XMLEntityMappings readMappings(String ormxml, Map<String, Object> properties, ClassLoader classLoader) {
        Reader reader = new StringReader(ormxml);
        try {
            return XMLEntityMappingsReader.read(getClass().getName(), reader, classLoader, properties);
        } finally {
            if (reader!=null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    //ignore so we rethrow original exception if there was one.
                }
            }
        }
    }

}
