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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.Assert;

import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import example.mysports.admin.model.Extension;
import example.mysports.admin.model.HostedLeague;
import example.mysports.tests.TestingLeagueRepository;

/**
 * Create initial league entities.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
public class CreateLeagues {

    private static EntityManagerFactory emf;

    @Test
    public void verifySession() {
        Assert.assertNotNull(emf);

        Server session = JpaHelper.getServerSession(emf);
        Assert.assertNotNull(session);
        Assert.assertTrue(session.isServerSession());
    }

    @Test
    public void createLeagues() {
        EntityManager em = emf.createEntityManager();
        
        try {
            em.getTransaction().begin();
            
            HostedLeague osl = new HostedLeague("OSL", "Ottawa Soccer League", "black");
            osl.setLogoUrl("/logos/osl.png");
            
            em.persist(osl);
            em.persist(new Extension(osl, "Player", "allergies", "java.lang.String", "flex_1", "allergies/text()"));
            
            HostedLeague hthl = new HostedLeague("HTHL", "High Tech Hockey League", "red");
            hthl.setLogoUrl("/logos/hthl.png");

            em.persist(hthl);
            em.persist(new Extension(hthl, "Player", "penaltyMinutes", "java.lang.Integer", "flex_1", "penalty-minutes/text()"));
            em.persist(new Extension(hthl, "Player", "position", "java.lang.String", "flex_2", "position/text()"));
            
            HostedLeague kfl = new HostedLeague("KFL", "Kid's Football League", "green");
            kfl.setLogoUrl("/logos/kfl.png");

            em.persist(kfl);
            em.persist(new Extension(kfl, "Player", "position", "java.lang.String", "flex_1", "penalty-minutes/text()"));

            HostedLeague mhl = new HostedLeague("MHL", "Minor Hockey League", "red");
            mhl.setMultitenant(false);
            mhl.addTableName("Player", "mys_player_mhl");
            mhl.addTableName("Division", "mys_div_mhl");
            mhl.addTableName("Team", "mys_team_mhl");
            mhl.setLogoUrl("/logos/hthl.png");

            em.persist(mhl);
            em.persist(new Extension(mhl, "Player", "position", "java.lang.String", "flex_1", "position/text()"));

            // Create default shared league for purposes of schema gen
            HostedLeague defaultLeague = new HostedLeague("ALL", "All League", "red");
            defaultLeague.setVisible(false);
            em.persist(new Extension(defaultLeague, "Player", "flex1", "java.lang.String", "flex_1", "flex1/text()"));
            em.persist(new Extension(defaultLeague, "Player", "flex2", "java.lang.String", "flex_2", "flex1/text()"));
            em.persist(new Extension(defaultLeague, "Player", "flex3", "java.lang.String", "flex_3", "flex1/text()"));
            em.persist(new Extension(defaultLeague, "Player", "flex4", "java.lang.String", "flex_4", "flex1/text()"));
            em.persist(new Extension(defaultLeague, "Player", "flex5", "java.lang.String", "flex_5", "flex1/text()"));
            em.persist(new Extension(defaultLeague, "Team", "flex1", "java.lang.String", "flex_1", "flex1/text()"));
            em.persist(new Extension(defaultLeague, "Team", "flex2", "java.lang.String", "flex_2", "flex1/text()"));
            em.persist(new Extension(defaultLeague, "Team", "flex3", "java.lang.String", "flex_3", "flex1/text()"));
            em.persist(new Extension(defaultLeague, "Team", "flex4", "java.lang.String", "flex_4", "flex1/text()"));
            em.persist(new Extension(defaultLeague, "Team", "flex5", "java.lang.String", "flex_5", "flex1/text()"));
            em.persist(new Extension(defaultLeague, "Division", "flex1", "java.lang.String", "flex_1", "flex1/text()"));
            em.persist(new Extension(defaultLeague, "Division", "flex2", "java.lang.String", "flex_2", "flex1/text()"));
            em.persist(new Extension(defaultLeague, "Division", "flex3", "java.lang.String", "flex_3", "flex1/text()"));
            em.persist(new Extension(defaultLeague, "Division", "flex4", "java.lang.String", "flex_4", "flex1/text()"));
            em.persist(new Extension(defaultLeague, "Division", "flex5", "java.lang.String", "flex_5", "flex1/text()"));
            em.persist(defaultLeague);
            
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    @BeforeClass
    public static void createEMF() {
        emf = Persistence.createEntityManagerFactory("mysports-admin", TestingLeagueRepository.get());
        
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Extension").executeUpdate();
            em.createQuery("DELETE FROM HostedLeague").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterClass
    public static void closeEMF() {
        emf.close();
    }

}
