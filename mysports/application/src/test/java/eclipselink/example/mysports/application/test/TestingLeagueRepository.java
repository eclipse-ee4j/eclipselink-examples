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

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.sessions.server.Server;

import eclipselink.example.mysports.admin.services.HostedLeagueRepository;
import eclipselink.example.mysports.admin.services.test.AdminPersistenceTesting;
import eclipselink.example.mysports.application.services.LeagueRepository;

/**
 * Add testing specific properties to override the non-JTA data-source
 * configured in the persistence.xml.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
public class TestingLeagueRepository extends LeagueRepository {
    private static final long serialVersionUID = 1L;

    public TestingLeagueRepository() {
        super(new MockAdminServerConnector());
    }

    public TestingLeagueRepository(String leagueId) {
        super(new MockAdminServerConnector());
        setLeagueId(leagueId, get(leagueId));
    }

    @Override
    public void setLeagueId(String leagueId, Map<String, Object> properties) {
        super.setLeagueId(leagueId, add(leagueId, properties));
    }

    public MockAdminServerConnector getMockAdminConnector() {
        return (MockAdminServerConnector) super.getAdminConnector();
    }

    public HostedLeagueRepository getHostedLeagueRepository() {
        return getMockAdminConnector().getRepository();
    }

    @Override
    public void close() {
        AdminPersistenceTesting.closeTestingRepository(getMockAdminConnector().getRepository(), false);

        super.close();
    }

    public Map<String, Object> add(String leagueId, Map<String, Object> properties) {
        Map<String, Object> props = AdminPersistenceTesting.add(properties);

        if (leagueId != null) {
            props.put(PersistenceUnitProperties.METADATA_SOURCE, getAdminConnector());
        }
        return props;
    }

    public Map<String, Object> get(String leagueId) {
        return add(leagueId, null);
    }

    public Map<String, Object> get() {
        return add(null, null);
    }

    public void createSharedMySportsSchema() {
        Map<String, Object> properties = get("ALL");

        properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
        properties.put(PersistenceUnitProperties.DDL_GENERATION_MODE, PersistenceUnitProperties.DDL_DATABASE_GENERATION);
        properties.put(PersistenceUnitProperties.ALLOW_NATIVE_SQL_QUERIES, "true");

        setLeagueId("ALL", properties);
    }

    public void dropLeagueTables(String leagueId) {
        Map<String, Object> properties = get(leagueId);
        properties.put(PersistenceUnitProperties.ALLOW_NATIVE_SQL_QUERIES, "true");
        setLeagueId(leagueId, properties);

        Server session = unwrap(Server.class);

        try {
            session.executeNonSelectingSQL("DROP TABLE MYS_PLAYER_" + leagueId);
        } catch (DatabaseException dbe) {
        }
        try {
            session.executeNonSelectingSQL("DROP TABLE MYS_TEAM_" + leagueId);
        } catch (DatabaseException dbe) {
        }
        try {
            session.executeNonSelectingSQL("DROP TABLE MYS_DIV_" + leagueId);
        } catch (DatabaseException dbe) {
        }
    }

    public void createLeagueTables(String leagueId) {
        Map<String, Object> properties = get(leagueId);
        properties.put(PersistenceUnitProperties.ALLOW_NATIVE_SQL_QUERIES, "true");
        setLeagueId(leagueId, properties);

        Server session = unwrap(Server.class);
        try {
            session.executeNonSelectingSQL("CREATE TABLE MYS_DIV_" + leagueId + " (ID INTEGER NOT NULL, DEF_DIV INTEGER default 0, NAME VARCHAR(255), FLEX_1 VARCHAR(255), FLEX_2 VARCHAR(255), FLEX_3 VARCHAR(255), FLEX_4 VARCHAR(255), FLEX_5 VARCHAR(255), VERSION INTEGER, PRIMARY KEY (ID))");
            session.executeNonSelectingSQL("CREATE TABLE MYS_PLAYER_" + leagueId + " (ID INTEGER NOT NULL, EMAIL VARCHAR(255), F_NAME VARCHAR(255), L_NAME VARCHAR(255), NUM INTEGER, FLEX_1 VARCHAR(255), FLEX_2 VARCHAR(255), FLEX_3 VARCHAR(255), FLEX_4 VARCHAR(255), FLEX_5 VARCHAR(255), USER_ID VARCHAR(255), VERSION INTEGER, TEAM_ID INTEGER, PRIMARY KEY (ID))");
            session.executeNonSelectingSQL("CREATE TABLE MYS_TEAM_" + leagueId + " (ID INTEGER NOT NULL, NAME VARCHAR(255), FLEX_1 VARCHAR(255), FLEX_2 VARCHAR(255), FLEX_3 VARCHAR(255), FLEX_4 VARCHAR(255), FLEX_5 VARCHAR(255), VERSION INTEGER, DIVISION_ID INTEGER, PRIMARY KEY (ID))");
        } catch (DatabaseException e) {

        }
    }
}
