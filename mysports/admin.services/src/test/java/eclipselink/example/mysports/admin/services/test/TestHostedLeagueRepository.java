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
package eclipselink.example.mysports.admin.services.test;

import java.lang.reflect.Proxy;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eclipselink.example.mysports.admin.examples.ExampleLeagueDefinition;
import eclipselink.example.mysports.admin.model.HostedLeague;
import eclipselink.example.mysports.admin.services.HostedLeagueRepository;

/**
 * TODO
 * 
 * @since EclipseLink 2.4.2
 * @author dclarke
 */
public class TestHostedLeagueRepository {

    @Test
    public void verifyProxy() {
        Assert.assertTrue(Proxy.isProxyClass(repository.getClass()));
    }

    @Test
    public void verifySchema() {
        //EntityManagerFactory emf = AdminPersistenceTesting.getEMF(repository);

    }

    @Test
    public void verifyAllLeagues() {
        List<HostedLeague> allLeagues = repository.allLeagues();

        Assert.assertNotNull(allLeagues);
    }

    @Test
    public void verifyAllSharedLeagues() {
        List<HostedLeague> allLeagues = repository.allSharedLeagues();

        Assert.assertNotNull(allLeagues);
    }

    //@Test
    public void getValidLogos() {
        Assert.assertNotNull(repository.getLogo(ExampleLeagueDefinition.HTHL.getLeague().getId()));
        Assert.assertNotNull(repository.getLogo(ExampleLeagueDefinition.KFL.getLeague().getId()));
        Assert.assertNotNull(repository.getLogo(ExampleLeagueDefinition.MHL.getLeague().getId()));
        Assert.assertNotNull(repository.getLogo(ExampleLeagueDefinition.OSL.getLeague().getId()));
    }

    //@Test
    public void getInValidLogo() {
        try {
            repository.getLogo("XXX");
        } catch (IllegalArgumentException e) {
            return;
        }
        Assert.fail("IllegalArgumentException expected but not thrown");

    }

    @Test
    public void getValidCSS() {
        Assert.assertNotNull(repository.getCSS(ExampleLeagueDefinition.HTHL.getLeague().getId()));
        Assert.assertNotNull(repository.getCSS(ExampleLeagueDefinition.KFL.getLeague().getId()));
        Assert.assertNotNull(repository.getCSS(ExampleLeagueDefinition.MHL.getLeague().getId()));
        Assert.assertNotNull(repository.getCSS(ExampleLeagueDefinition.OSL.getLeague().getId()));
    }

    @Test
    public void getInValidCSS() {
        try {
            repository.getCSS("XXX");
        } catch (IllegalArgumentException e) {
            return;
        }
        Assert.fail("IllegalArgumentException expected but not thrown");
    }

    @Test
    public void getValidORM() {
        Assert.assertNotNull(repository.getORM(ExampleLeagueDefinition.HTHL.getLeague().getId()));
        Assert.assertNotNull(repository.getORM(ExampleLeagueDefinition.KFL.getLeague().getId()));
        Assert.assertNotNull(repository.getORM(ExampleLeagueDefinition.MHL.getLeague().getId()));
        Assert.assertNotNull(repository.getORM(ExampleLeagueDefinition.OSL.getLeague().getId()));
    }

    @Test
    public void getInvalidORM() {
        try {
            repository.getORM("XXX");
        } catch (IllegalArgumentException e) {
            return;
        }
        Assert.fail("IllegalArgumentException expected but not thrown");
    }

    private static HostedLeagueRepository repository;

    @BeforeClass
    public static void createRespository() {
        repository = AdminPersistenceTesting.createTestRepository(true, false);
        ExampleLeagueDefinition.populateAll(repository);
    }

    @AfterClass
    public static void closeRepository() {
        AdminPersistenceTesting.closeTestingRepository(repository, true);
    }

}
