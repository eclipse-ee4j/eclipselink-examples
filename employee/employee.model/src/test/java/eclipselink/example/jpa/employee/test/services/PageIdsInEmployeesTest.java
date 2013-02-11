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
import org.junit.BeforeClass;
import org.junit.Test;

import eclipselink.example.jpa.employee.model.Employee;
import eclipselink.example.jpa.employee.services.Diagnostics;
import eclipselink.example.jpa.employee.services.FirstMaxPaging;
import eclipselink.example.jpa.employee.services.Diagnostics.SQLTrace;
import example.JavaSEExample;
import example.PersistenceTesting;

/**
 * TODO
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
public class PageIdsInEmployeesTest {

    @Test
    public void streamAll() {
        EntityManager em = getEmf().createEntityManager();
        Diagnostics diagnostics = Diagnostics.getInstance(getEmf());

        SQLTrace start = diagnostics.start();
        Assert.assertTrue(start.getEntries().isEmpty());

        FirstMaxPaging paging = new FirstMaxPaging(emf, 5);
        Assert.assertEquals(25, paging.size());

        SQLTrace end = diagnostics.stop();

        Assert.assertNotNull(end);
        Assert.assertSame(start, end);
        Assert.assertFalse(end.getEntries().isEmpty());
        Assert.assertEquals(1, end.getEntries().size());
                
        Assert.assertEquals(5, paging.getNumPages());

        for (int index = 0; index < 5; index++) {
            List<Employee> emps = paging.get(index + 1);
            Assert.assertEquals(5, emps.size());
            for (Employee e : emps) {
                System.out.println("> " + e);
            }
        }

        
        em.close();
    }

    private static EntityManagerFactory emf;

    public static EntityManagerFactory getEmf() {
        return emf;
    }

    @BeforeClass
    public static void createEMF() {
        emf = PersistenceTesting.createEMF(true);

        EntityManager em = emf.createEntityManager();
        new JavaSEExample().createNewEmployees(em, 25);
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
