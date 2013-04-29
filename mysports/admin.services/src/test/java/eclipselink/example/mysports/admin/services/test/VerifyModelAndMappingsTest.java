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
package eclipselink.example.mysports.admin.services.test;

import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eclipselink.example.mysports.admin.model.Extension;
import eclipselink.example.mysports.admin.model.HostedLeague;
import eclipselink.example.mysports.admin.model.Style;
import eclipselink.example.mysports.admin.services.HostedLeagueRepository;

/**
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
public class VerifyModelAndMappingsTest {

    @Test
    public void verifySession() {
        EntityManagerFactory emf = AdminPersistenceTesting.getEMF(repository);
        Assert.assertNotNull(emf);

        Server session = JpaHelper.getServerSession(emf);
        Assert.assertNotNull(session);
        Assert.assertTrue(session.isServerSession());
    }

    @Test
    public void verifyLeague() {
        EntityManagerFactory emf = AdminPersistenceTesting.getEMF(repository);
        Server session = JpaHelper.getServerSession(emf);
        ClassDescriptor descriptor = session.getClassDescriptor(HostedLeague.class);

        Assert.assertNotNull(descriptor);
        Assert.assertEquals(Helper.getShortClassName(descriptor.getJavaClass()), descriptor.getAlias());
    }

    @Test
    public void verifyStyle() {
        EntityManagerFactory emf = AdminPersistenceTesting.getEMF(repository);
        Server session = JpaHelper.getServerSession(emf);
        ClassDescriptor descriptor = session.getClassDescriptor(Style.class);

        Assert.assertNotNull(descriptor);
        Assert.assertEquals(Helper.getShortClassName(descriptor.getJavaClass()), descriptor.getAlias());
    }

    @Test
    public void verifyExtension() {
        EntityManagerFactory emf = AdminPersistenceTesting.getEMF(repository);
        Server session = JpaHelper.getServerSession(emf);
        ClassDescriptor descriptor = session.getClassDescriptor(Extension.class);

        Assert.assertNotNull(descriptor);
        //Assert.assertTrue(descriptor.isAggregateDescriptor());
    }

    private static HostedLeagueRepository repository;

    @BeforeClass
    public static void createRespository() {
        repository = AdminPersistenceTesting.createTestRepository(true);
    }

    @AfterClass
    public static void closeRepository() {
        AdminPersistenceTesting.closeTestingRepository(repository, true);
    }

}
