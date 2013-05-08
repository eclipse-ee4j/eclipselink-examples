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
 *  dclarke - initial
 ******************************************************************************/
package eclipselink.example.jpa.employee.test.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eclipselink.example.jpa.employee.model.SamplePopulation;
import eclipselink.example.jpa.employee.services.Diagnostics;
import eclipselink.example.jpa.employee.services.AdminBean;
import eclipselink.example.jpa.employee.test.PersistenceTesting;

public class AdminBeanTest {

    private AdminBean admin;

    public AdminBean getAdmin() {
        return admin;
    }

    @Test
    public void verifyReset() {
        for (String type : getAdmin().getTypes()) {
            Assert.assertEquals(0, admin.getCacheSize(type));
            Assert.assertEquals(0, admin.getDatabaseCount(type));
        }
    }

    @Test
    public void popuate() {
        verifyReset();

        getAdmin().populateDatabase(20);

        Assert.assertEquals(20, admin.getCacheSize("Employee"));
        Assert.assertEquals(20, admin.getDatabaseCount("Employee"));

        Assert.assertEquals(20, admin.getCacheSize("Address"));
        Assert.assertEquals(20, admin.getDatabaseCount("Address"));

        Assert.assertEquals(40, admin.getCacheSize("PhoneNumber"));
        Assert.assertEquals(40, admin.getDatabaseCount("PhoneNumber"));

        Assert.assertEquals(0, admin.getCacheSize("Project"));
        Assert.assertEquals(0, admin.getDatabaseCount("Project"));
    }

    @Test
    public void verifyTypeNames() {
        List<String> types = getAdmin().getTypes();

        Assert.assertNotNull(types);
        Assert.assertEquals(4, types.size());

        Assert.assertTrue(types.contains("Employee"));
        Assert.assertTrue(types.contains("Address"));
        Assert.assertTrue(types.contains("PhoneNumber"));
        Assert.assertTrue(types.contains("Project"));
    }

    private static EntityManagerFactory emf;

    public static EntityManagerFactory getEmf() {
        return emf;
    }

    @Before
    public void resetDatabase() {
        this.admin = new AdminBean();
        this.admin.setEmf(getEmf());
        this.admin.resetDatabase();
    }

    @BeforeClass
    public static void createEMF() {
        emf = PersistenceTesting.createEMF(true);

        EntityManager em = emf.createEntityManager();
        new SamplePopulation().createNewEmployees(em, 25);
        em.close();

        Diagnostics.getInstance(emf);
        emf.getCache().evictAll();
    }

    @AfterClass
    public static void closeEMF() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
        emf = null;
    }

}
