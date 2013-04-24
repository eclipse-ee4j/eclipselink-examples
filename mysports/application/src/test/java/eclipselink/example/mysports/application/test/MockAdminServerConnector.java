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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappingsReader;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappingsWriter;
import org.eclipse.persistence.jpa.metadata.MetadataSource;
import org.eclipse.persistence.logging.SessionLog;

import eclipselink.example.mysports.admin.model.HostedLeague;
import eclipselink.example.mysports.admin.services.HostedLeagueRepository;
import eclipselink.example.mysports.admin.services.MappingsLoader;
import eclipselink.example.mysports.admin.services.test.AdminPersistenceTesting;
import eclipselink.example.mysports.application.MySportsConfig;
import eclipselink.example.mysports.application.admin.AdminServerConnector;
import eclipselink.example.mysports.application.admin.HostedLeagues;
import eclipselink.example.mysports.application.admin.League;
import eclipselink.example.mysports.application.admin.RESTAdminServerConnector;

/**
 * TODO
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
public class MockAdminServerConnector implements AdminServerConnector, MetadataSource {

    private HostedLeagueRepository leaguesRepository;

    public MockAdminServerConnector() {
        leaguesRepository = AdminPersistenceTesting.createTestRepository(true);
    }

    public HostedLeagueRepository getRepository() {
        return leaguesRepository;
    }

    public HostedLeagues getLeagues() {
        List<HostedLeague> allLeagues = getRepository().allSharedLeagues();

        for (HostedLeague hl : allLeagues) {
            hl.setUri(RESTAdminServerConnector.DEFAULT_ROOT_URI+ "/" + hl.getId());
        }

        HostedLeagues result = new HostedLeagues();
        for (HostedLeague hl : allLeagues) {
            League l = new League();
            l.setId(hl.getId());
            l.setName(hl.getName());
            l.setVersion(hl.getVersion());
            result.getLeagues().add(l);
        }

        return result;
    }

    public League getLeague(String leagueId) {
        HostedLeague hl = getRepository().getLeague(leagueId);
        
        if (hl == null && "ALL".equals(leagueId)) {
            hl = getAllExtensionsHostedLeague();
        }
        
        if (hl != null) {
            hl.setUri(RESTAdminServerConnector.DEFAULT_ROOT_URI + "/" + hl.getId());
        }

        League l = null;

        if (hl != null) {
            l = new League();
            l.setId(hl.getId());
            l.setName(hl.getName());
            l.setVersion(hl.getVersion());
            hl.getPlayerExtensions().size();
        }
        return l;
    }

    public InputStream getCss(String leagueId) {
        return new ByteArrayInputStream(getRepository().getCSS(leagueId).getBytes());
    }

    public InputStream getLogo(String leagueId) {
        return getRepository().getLogo(leagueId);
    }

    public String getOrmURL(String leagueId) {
        return leagueId;
    }

    public String getOxmURL(String leagueId) {
        return null;
    }

    /**
     * Default local resource. Only used when admin service cannot be accessed.
     */
    public static final String LOCAL_ECLIPSELINK_ORM = "META-INF/local-eclipselink-orm.xml";

    @Override
    public XMLEntityMappings getEntityMappings(Map<String, Object> properties, ClassLoader classLoader, SessionLog log) {
        String leagueId = (String) properties.get(MySportsConfig.LEAGUE_CONTEXT);
        String ormxml = null;
        
        try {
        ormxml = getRepository().getORM(leagueId);
        } catch (IllegalArgumentException e) {
            if ("ALL".equals(leagueId)) {
                 XMLEntityMappings xmlEntityMappings = MappingsLoader.getXMLEntityMappings(getAllExtensionsHostedLeague());
                 StringWriter writer = new StringWriter();
                 XMLEntityMappingsWriter.write(xmlEntityMappings, writer);
                 String xml = writer.toString();
                 //System.out.println("ORM-XML:\n" + xml + "\n");
                 return XMLEntityMappingsReader.read(getClass().getName(), new StringReader(xml), classLoader, properties);
            } else {
                throw e;
            }
        }
        Reader reader = new StringReader(ormxml);
        try {
            return XMLEntityMappingsReader.read(getClass().getName(), reader, classLoader, properties);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public Map<String, Object> getPropertyOverrides(Map<String, Object> arg0, ClassLoader arg1, SessionLog arg2) {
        return null;
    }

    private HostedLeague getAllExtensionsHostedLeague() {
        HostedLeague allLeague = new HostedLeague("ALL", "All League", "red");
        allLeague.addPlayerExtension("flex1", "java.lang.String", "FLEX_1");
        allLeague.addPlayerExtension("flex2", "java.lang.String", "FLEX_2");
        allLeague.addPlayerExtension("flex3", "java.lang.String", "FLEX_3");
        allLeague.addPlayerExtension("flex4", "java.lang.String", "FLEX_4");
        allLeague.addPlayerExtension("flex5", "java.lang.String", "FLEX_5");

        allLeague.addTeamExtension("flex1", "java.lang.String", "FLEX_1");
        allLeague.addTeamExtension("flex2", "java.lang.String", "FLEX_2");
        allLeague.addTeamExtension("flex3", "java.lang.String", "FLEX_3");
        allLeague.addTeamExtension("flex4", "java.lang.String", "FLEX_4");
        allLeague.addTeamExtension("flex5", "java.lang.String", "FLEX_5");

        allLeague.addDivisionExtension("flex1", "java.lang.String", "FLEX_1");
        allLeague.addDivisionExtension("flex2", "java.lang.String", "FLEX_2");
        allLeague.addDivisionExtension("flex3", "java.lang.String", "FLEX_3");
        allLeague.addDivisionExtension("flex4", "java.lang.String", "FLEX_4");
        allLeague.addDivisionExtension("flex5", "java.lang.String", "FLEX_5");

        return allLeague;
    }

 }
