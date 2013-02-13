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
import javax.persistence.TypedQuery;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eclipselink.example.jpa.employee.model.Employee;
import eclipselink.example.jpa.employee.services.Diagnostics;
import eclipselink.example.jpa.employee.services.StreamPaging;
import eclipselink.example.jpa.employee.services.Diagnostics.SQLTrace;
import example.JavaSEExample;
import example.PersistenceTesting;

/**
 * TODO
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
public class StreamEmployeesTest {

    @Test
    public void streamAll() {
        EntityManager em = getEmf().createEntityManager();
        Diagnostics diagnostics = Diagnostics.getInstance(getEmf());

        SQLTrace start = diagnostics.start();
        Assert.assertTrue(start.getEntries().isEmpty());

        TypedQuery<Employee> query = em.createQuery("SELECT e FROM Employee e ORDER BY e.id", Employee.class);
        StreamPaging<Employee> stream = new StreamPaging<Employee>(query, 5);
        Assert.assertEquals(25, stream.size());

        SQLTrace end = diagnostics.stop();

        Assert.assertNotNull(end);
        Assert.assertSame(start, end);
        Assert.assertFalse(end.getEntries().isEmpty());
        Assert.assertEquals(1, end.getEntries().size());

        Assert.assertEquals(5, stream.getNumPages());

        // Verify the ids assuming they are sequentially assigned starting at 1.
        int currentId = 1;

        for (int index = 0; index < 5; index++) {
            Assert.assertTrue(stream.hasNext());
            List<Employee> emps = stream.next();

            //Assert.assertEquals(5, emps.size());

            for (Employee e : emps) {
                Assert.assertEquals(currentId++, e.getId());
                System.out.println("> " + e);
            }
        }

        Assert.assertEquals(1, end.getEntries().size());
        Assert.assertFalse(stream.hasNext());

        List<Employee> next = stream.next();
        Assert.assertTrue(next.isEmpty());

        em.close();
    }

    @Test
    public void streamPartial() {
        EntityManager em = getEmf().createEntityManager();
        Diagnostics diagnostics = Diagnostics.getInstance(getEmf());

        SQLTrace start = diagnostics.start();
        Assert.assertTrue(start.getEntries().isEmpty());

        TypedQuery<Object[]> query = em.createQuery("SELECT e.id, e.firstName, e.lastName FROM Employee e ORDER BY e.id", Object[].class);
        StreamPaging<Object[]> stream = new StreamPaging<Object[]>(query, 5);

        Assert.assertEquals(25, stream.size());

        SQLTrace end = diagnostics.stop();

        Assert.assertNotNull(end);
        Assert.assertSame(start, end);
        Assert.assertFalse(end.getEntries().isEmpty());
        Assert.assertEquals(1, end.getEntries().size());

        Assert.assertEquals(5, stream.getNumPages());

        // Verify the ids assuming they are sequentially assigned starting at 1.
        int currentId = 1;

        for (int index = 0; index < 5; index++) {
            Assert.assertTrue(stream.hasNext());
            List<Object[]> emps = stream.next();

            Assert.assertEquals(5, emps.size());

            for (Object[] e : emps) {
                Assert.assertEquals(currentId++, ((Number) e[0]).intValue());
                System.out.println("> " + e[0] + ":: " + e[2] + ", " + e[1]);
            }
        }

        Assert.assertEquals(1, end.getEntries().size());
        Assert.assertFalse(stream.hasNext());

        List<Object[]> next = stream.next();
        Assert.assertTrue(next.isEmpty());

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
