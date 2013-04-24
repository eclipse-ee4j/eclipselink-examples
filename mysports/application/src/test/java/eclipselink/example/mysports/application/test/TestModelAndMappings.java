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

import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.weaving.PersistenceWeaved;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eclipselink.example.mysports.application.model.Division;
import eclipselink.example.mysports.application.model.Extensible;
import eclipselink.example.mysports.application.model.Player;
import eclipselink.example.mysports.application.model.Team;
import eclipselink.example.mysports.application.model.User;
import eclipselink.example.mysports.examples.ExampleLeague;
import eclipselink.example.mysports.examples.OttawaSoccerLeague;

/**
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
public class TestModelAndMappings {

    private static TestingLeagueRepository repository;
    
    private static ExampleLeague LEAGUE = new OttawaSoccerLeague();

    @Test
    public void verifySession() {
        EntityManagerFactory emf = repository.unwrap(EntityManagerFactory.class);
        Server session = JpaHelper.getServerSession(emf);
        Assert.assertNotNull(session);
        Assert.assertTrue(session.isServerSession());
    }

    @Test
    public void verifyPlayer() {
        EntityManagerFactory emf = repository.unwrap(EntityManagerFactory.class);
        Server session = JpaHelper.getServerSession(emf);
        ClassDescriptor descriptor = session.getClassDescriptor(Player.class);

        Assert.assertNotNull(descriptor);
        Assert.assertEquals(Helper.getShortClassName(descriptor.getJavaClass()), descriptor.getAlias());

        Assert.assertTrue(Extensible.class.isAssignableFrom(descriptor.getJavaClass()));
        Assert.assertTrue("Entity class now woven: " + descriptor, PersistenceWeaved.class.isAssignableFrom(descriptor.getJavaClass()));
    }

    @Test
    public void verifyTeam() {
        EntityManagerFactory emf = repository.unwrap(EntityManagerFactory.class);
        Server session = JpaHelper.getServerSession(emf);
        ClassDescriptor descriptor = session.getClassDescriptor(Team.class);

        Assert.assertNotNull(descriptor);
        Assert.assertEquals(Helper.getShortClassName(descriptor.getJavaClass()), descriptor.getAlias());
        Assert.assertTrue("Entity class now woven: " + descriptor, PersistenceWeaved.class.isAssignableFrom(descriptor.getJavaClass()));
    }

    @Test
    public void verifyDivision() {
        EntityManagerFactory emf = repository.unwrap(EntityManagerFactory.class);
        Server session = JpaHelper.getServerSession(emf);
        ClassDescriptor descriptor = session.getClassDescriptor(Division.class);

        Assert.assertNotNull(descriptor);
        Assert.assertEquals(Helper.getShortClassName(descriptor.getJavaClass()), descriptor.getAlias());
        Assert.assertTrue("Entity class now woven: " + descriptor, PersistenceWeaved.class.isAssignableFrom(descriptor.getJavaClass()));
    }

    @Test
    public void verifyUser() {
        EntityManagerFactory emf = repository.unwrap(EntityManagerFactory.class);
        Server session = JpaHelper.getServerSession(emf);
        ClassDescriptor descriptor = session.getClassDescriptor(User.class);

        Assert.assertNotNull(descriptor);
        Assert.assertEquals(Helper.getShortClassName(descriptor.getJavaClass()), descriptor.getAlias());
        Assert.assertTrue("Entity class now woven: " + descriptor, PersistenceWeaved.class.isAssignableFrom(descriptor.getJavaClass()));
    }

    @BeforeClass
    public static void setup() {
        repository = new TestingLeagueRepository();
        repository.setLeagueId(LEAGUE.getId(), null);
        LEAGUE.populate(repository);
    }

    @AfterClass
    public static void tearDown() {
        repository.close();
    }
}
