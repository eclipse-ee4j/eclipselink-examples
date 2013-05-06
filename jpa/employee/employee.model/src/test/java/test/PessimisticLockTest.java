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
 *  dclarke - Employee Demo 2.4
 ******************************************************************************/
package test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eclipselink.example.jpa.employee.model.Address;
import eclipselink.example.jpa.employee.model.SamplePopulation;
import eclipselink.example.jpa.employee.services.Diagnostics;
import example.PersistenceTesting;

/**
 * Simple test case illustrating pessimistic lock usage.
 * 
 * @author dclarke
 * @since EclispeLink 2.4.2
 */
public class PessimisticLockTest {

    @Test
    public void test() {
        EntityManager em = getEmf().createEntityManager();

        int id = em.createQuery("SELECT a.id FROM Address a", Number.class).getSingleResult().intValue();

        em.getTransaction().begin();

        Address addr = em.find(Address.class, id, LockModeType.PESSIMISTIC_WRITE);

        Assert.assertNotNull(addr);
        Assert.assertNull(addr.getCity());
        Assert.assertEquals(em.getLockMode(addr), LockModeType.PESSIMISTIC_WRITE);
        
        addr.setCity("Ottawa");

        Assert.assertNotNull(addr.getCity());
        
        em.lock(addr, LockModeType.PESSIMISTIC_WRITE);
        
        Assert.assertEquals(em.getLockMode(addr), LockModeType.PESSIMISTIC_WRITE);
        Assert.assertNotNull(addr.getCity());

        em.getTransaction().rollback();

        em.close();
    }

    private static EntityManagerFactory emf;

    private static Diagnostics diagnostics;

    public static EntityManagerFactory getEmf() {
        return emf;
    }

    public static Diagnostics getDiagnostics() {
        return diagnostics;
    }

    @BeforeClass
    public static void createEMF() {
        emf = PersistenceTesting.createEMF(true);
        diagnostics = Diagnostics.getInstance(emf);

        EntityManager em = emf.createEntityManager();
        new SamplePopulation().createNewEmployees(em, 1);

        Number count = em.createNamedQuery("Employee.count", Number.class).getSingleResult();
        Assert.assertEquals(1, count.intValue());

        em.close();

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
