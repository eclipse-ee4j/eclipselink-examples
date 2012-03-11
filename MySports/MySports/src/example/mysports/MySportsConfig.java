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
package example.mysports;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.internal.helper.ConversionManager;

import example.mysports.admin.AdminServerConnector;
import example.mysports.admin.League;
import example.mysports.ejb.LeagueRepository;

/**
 * Responsible for managing application context configuration. For now this is
 * hard coded for local deployment but will be made more flexible in the future.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
public class MySportsConfig {

    public static final String ADMIN_SERVER_CONTEXT_PROPERTY = "mysports.admin.context";

    /**
     * Context property used to specify the tenant configuration within the
     * persistence unit. A value for this property must be available within the
     * persistence context to access the {@link Multitenant} types.
     */
    public static final String LEAGUE_CONTEXT = "mysports.league";
    public static final String LEAGUE_CONTEXT_NAME = "mysports.league.name";
    public static final String LEAGUE_CONTEXT_COLOUR = "mysports.league.colour";
    public static final String LEAGUE_CONTEXT_LOGO = "mysports.league.logo";

    /**
     * Persistence unit name. This is the base PU which is used as a template
     * for each tenant's persistence unit.
     * 
     * @see LeagueRepository#setLeagueId(String, java.util.Map) for details of
     *      how the PUs are created from template.
     */
    public static final String PU_NAME = "mysports";

    public static final String ADMIN_CONNECTOR_PROPERTY = "mysports.admin-connector";

    /**
     * TODO
     */
    private Properties properties;

    /**
     * TODO
     */
    private AdminServerConnector adminConnector;

    /**
     * TODO
     */
    private League league;

    public MySportsConfig() {
        properties = new Properties();

        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("my-sports.properties");
        try {
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failure loading my-sports.properties", e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
            }
        }

    }

    protected String getProperty(String name) {
        return (String) this.properties.get(name);
    }

    public AdminServerConnector getAdminConnector() {
        if (this.adminConnector == null) {
            Object value = getProperty(ADMIN_CONNECTOR_PROPERTY);
            if (value != null && value instanceof String) {
                @SuppressWarnings("unchecked")
                Class<AdminServerConnector> connectorClass = ConversionManager.getDefaultManager().convertClassNameToClass((String) value);
                try {
                    this.adminConnector = connectorClass.newInstance();
                    this.adminConnector.setConfig(this);
                } catch (InstantiationException e) {
                    throw new RuntimeException("Could not create AdminServerConnector", e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Could not create AdminServerConnector", e);
                }
            }
        }
        return this.adminConnector;
    }

    public String getSessionName(String leagueId) {
        if (isMultitenant()) {
            if (leagueId == null || leagueId.isEmpty()) {
                throw new IllegalArgumentException("Multitenant instance requires leagueId");
            }
            return PU_NAME + "-" + leagueId;
        }
        return null;
    }

    public boolean isMultitenant() {
        return !this.properties.containsKey(LEAGUE_CONTEXT);
    }
    
    public String getAdminContext() {
        return getProperty(ADMIN_SERVER_CONTEXT_PROPERTY);
    }

    public League getLeague(String leagueId) {
        if (isMultitenant() && leagueId != null) {
            getAdminConnector().getLeague(leagueId);
        }
        
        if (this.league == null && this.properties.containsKey(LEAGUE_CONTEXT)) {
            this.league = new League();
            this.league.setId(getProperty(LEAGUE_CONTEXT));
            this.league.setName(getProperty(LEAGUE_CONTEXT_NAME));
            this.league.setColourScheme(getProperty(LEAGUE_CONTEXT_COLOUR));
            this.league.setLogoUrl(getProperty(LEAGUE_CONTEXT_LOGO));
        }
        return this.league;
    }

}
