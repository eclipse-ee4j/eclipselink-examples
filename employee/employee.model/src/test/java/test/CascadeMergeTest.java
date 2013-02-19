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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eclipselink.example.jpa.employee.model.Employee;
import eclipselink.example.jpa.employee.model.SamplePopulation;
import eclipselink.example.jpa.employee.services.Diagnostics;
import eclipselink.example.jpa.employee.services.Diagnostics.SQLTrace;
import example.PersistenceTesting;

public class CascadeMergeTest {

    @Test
    public void test() {
        EntityManager em = getEmf().createEntityManager();

        TypedQuery<Employee> query = em.createQuery("SELECT e FROM Employee e WHERE e.address IS NOT NULL", Employee.class);

        List<Employee> emps = query.getResultList();

        Employee emp = emps.get(0);

        Assert.assertNotNull(emp);
        Assert.assertNotNull(emp.getAddress());

        em.detach(emp);
        
        em.close();

        em = getEmf().createEntityManager();
        emp.getAddress().setCity(emp.getAddress().getCity() + "+");

        em.getTransaction().begin();
        em.merge(emp);
        
        getDiagnostics().start();
        em.flush();
        SQLTrace trace = getDiagnostics().stop();
        
        Assert.assertEquals(1, trace.getEntries().size());
        Assert.assertTrue(trace.getEntries().get(0).getMessage().startsWith("UPDATE ADDRESS"));
        
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
