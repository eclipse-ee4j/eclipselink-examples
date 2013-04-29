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
package eclipselink.example.mysports.admin.examples;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import eclipselink.example.mysports.admin.model.HostedLeague;
import eclipselink.example.mysports.admin.services.HostedLeagueRepository;

/**
 * TODO
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
public abstract class ExampleLeagueDefinition {

    private HostedLeague league;

    public ExampleLeagueDefinition(String id, String name, String scheme, String logoUrl, String dataSource) {
        this.league = new HostedLeague(id, name, scheme);
        this.league.setLogoUrl(logoUrl);
        this.league.setDatasourceName(dataSource);
        addExtensions();
    }

    public HostedLeague getLeague() {
        return league;
    }

    protected abstract void addExtensions();

    public static ExampleLeagueDefinition OSL = new ExampleLeagueDefinition("OSL", "Ottawa Soccer League", "black", "/logos/osl.png", "jdbc/MySports") {
        @Override
        protected void addExtensions() {
            getLeague().addPlayerExtension("allergies", "java.lang.String", "FLEX_1");
        }
    };

    public static ExampleLeagueDefinition HTHL = new ExampleLeagueDefinition("HTHL", "High Tech Hockey League", "red", "/logos/hthl.png", "jdbc/MySports") {
        @Override
        protected void addExtensions() {
            getLeague().addPlayerExtension("position", "java.lang.String", "FLEX_1");
            getLeague().addPlayerExtension("penaltyMinutes", "java.lang.Integer", "FLEX_2");
        }
    };

    public static ExampleLeagueDefinition KFL = new ExampleLeagueDefinition("KFL", "Kid's Football League", "green", "/logos/kfl.png", "jdbc/MySports") {
        @Override
        protected void addExtensions() {
            getLeague().addPlayerExtension("position", "java.lang.String", "FLEX_1");
        }
    };

    public static ExampleLeagueDefinition MHL = new ExampleLeagueDefinition("MHL", "Minor Hockey League", "red", "/logos/mhl.png", "jdbc/MySportsMHL") {
        @Override
        protected void addExtensions() {
            getLeague().addPlayerExtension("position", "java.lang.String", "FLEX_1");
        }
    };

    public static void populateAll(HostedLeagueRepository repository) {
        repository.create(OSL.getLeague());
        repository.create(KFL.getLeague());
        repository.create(MHL.getLeague());
        repository.create(HTHL.getLeague());
        createStyles(repository);
    }

    public static void clearDatabase(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.createNativeQuery("DELETE FROM mys_admin_player_ext").executeUpdate();
            em.createNativeQuery("DELETE FROM mys_admin_team_ext").executeUpdate();
            em.createNativeQuery("DELETE FROM mys_admin_div_ext").executeUpdate();
            em.createQuery("DELETE FROM HostedLeague").executeUpdate();
            em.createQuery("DELETE FROM Style").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public static void createStyles(HostedLeagueRepository repository) {
        repository.createStyle("default", load("blue"));
        repository.createStyle("red", load("red"));
        repository.createStyle("black", load("black"));
        repository.createStyle("blue", load("blue"));
        repository.createStyle("green", load("green"));
    }

    private static String load(String name) {
        String resource = name + ".css";
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);

        if (in == null) {
            throw new IllegalArgumentException("Could not find CSS for '" + name + " @ " + resource);
        }
        try {
            StringWriter writer = new StringWriter(in.available());
            for (int ch = in.read(); ch >= 0; ch = in.read()) {
                writer.write(ch);
            }
            return writer.toString();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
            }
        }

    }

}
