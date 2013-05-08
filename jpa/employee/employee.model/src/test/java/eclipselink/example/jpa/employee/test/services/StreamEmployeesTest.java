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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eclipselink.example.jpa.employee.model.Employee;
import eclipselink.example.jpa.employee.model.SamplePopulation;
import eclipselink.example.jpa.employee.services.Diagnostics;
import eclipselink.example.jpa.employee.services.Diagnostics.SQLTrace;
import eclipselink.example.jpa.employee.services.EmployeeCriteria;
import eclipselink.example.jpa.employee.services.EmployeeRepository;
import eclipselink.example.jpa.employee.services.paging.EntityPaging;
import eclipselink.example.jpa.employee.test.PersistenceTesting;

/**
 * TODO
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
public class StreamEmployeesTest {

    @Test
    public void streamAllNext() {
        SQLTrace start = getDiagnostics().start();
        Assert.assertTrue(start.getEntries().isEmpty());

        EmployeeCriteria criteria = new EmployeeCriteria();
        criteria.setFirstName(null);
        criteria.setLastName(null);
        criteria.setPageSize(5);
        criteria.setPagingType(EntityPaging.Type.CURSOR.name());
        EntityPaging<Employee> stream = getRepository().getPaging(criteria);

        Assert.assertEquals(25, stream.size());

        SQLTrace end = getDiagnostics().stop();

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

            Assert.assertEquals(5, emps.size());

            for (Employee e : emps) {
                System.out.println("> " + e);
                Assert.assertEquals(currentId++, e.getId());
            }
        }

        Assert.assertEquals(1, end.getEntries().size());
        Assert.assertFalse(stream.hasNext());

        try {
            stream.next();
        } catch (IllegalStateException e) {
            return;
        } 

        Assert.fail("IllegalStateException not thrown on next()");
    }

    @Test
    public void streamAllNext10() {
        SQLTrace start = getDiagnostics().start();
        Assert.assertTrue(start.getEntries().isEmpty());

        EmployeeCriteria criteria = new EmployeeCriteria();
        criteria.setFirstName(null);
        criteria.setLastName(null);
        criteria.setPageSize(10);
        criteria.setPagingType(EntityPaging.Type.CURSOR.name());
        EntityPaging<Employee> stream = getRepository().getPaging(criteria);

        Assert.assertEquals(25, stream.size());

        SQLTrace end = getDiagnostics().stop();

        Assert.assertNotNull(end);
        Assert.assertSame(start, end);
        Assert.assertFalse(end.getEntries().isEmpty());
        Assert.assertEquals(1, end.getEntries().size());

        Assert.assertEquals(3, stream.getNumPages());

        // Verify the ids assuming they are sequentially assigned starting at 1.
        int currentId = 1;

        for (int index = 0; index < 3; index++) {
            Assert.assertTrue(stream.hasNext());
            List<Employee> emps = stream.next();

            if (index < 2) {
                Assert.assertEquals(10, emps.size());
            } else {
                Assert.assertEquals(5, emps.size());
            }

            for (Employee e : emps) {
                System.out.println("> " + e);
                Assert.assertEquals(currentId++, e.getId());
            }
        }

        Assert.assertEquals(1, end.getEntries().size());
        Assert.assertFalse(stream.hasNext());

        try {
            stream.next();
        } catch (IllegalStateException e) {
            return;
        } 

        Assert.fail("IllegalStateException not thrown on next()");
    }

    @Test
    public void streamAllPrevious() {
        SQLTrace start = getDiagnostics().start();
        Assert.assertTrue(start.getEntries().isEmpty());

        EmployeeCriteria criteria = new EmployeeCriteria();
        criteria.setFirstName(null);
        criteria.setLastName(null);
        criteria.setPageSize(5);
        criteria.setPagingType(EntityPaging.Type.CURSOR.name());
        EntityPaging<Employee> stream = getRepository().getPaging(criteria);

        Assert.assertEquals(25, stream.size());

        SQLTrace end = getDiagnostics().stop();

        Assert.assertNotNull(end);
        Assert.assertSame(start, end);
        Assert.assertFalse(end.getEntries().isEmpty());
        Assert.assertEquals(1, end.getEntries().size());

        Assert.assertEquals(5, stream.getNumPages());

        // skip to end
        List<List<Employee>> pages = new ArrayList<List<Employee>>();
        pages.add(stream.next());
        Assert.assertEquals(1, stream.getCurrentPage());
        pages.add(stream.next());
        Assert.assertEquals(2, stream.getCurrentPage());
        pages.add(stream.next());
        Assert.assertEquals(3, stream.getCurrentPage());
        pages.add(stream.next());
        Assert.assertEquals(4, stream.getCurrentPage());
        pages.add(stream.next());
        Assert.assertEquals(5, stream.getCurrentPage());

        for (int index = 4; index > 0; index--) {
            Assert.assertEquals(index + 1, stream.getCurrentPage());
            Assert.assertTrue("No previous found at page: " + index + " stream at: " + stream.getCurrentPage(), stream.hasPrevious());

            List<Employee> emps = stream.previous();

            if (index == 1) {
                Assert.assertFalse(stream.hasPrevious());
            } else {
                Assert.assertTrue("No previous found at page: " + index + " stream at: " + stream.getCurrentPage(), stream.hasPrevious());
            }

            Assert.assertEquals(5, emps.size());
            Assert.assertEquals(index, stream.getCurrentPage());

            List<Employee> nextPage = pages.get(index - 1);
            for (int pi = 0; pi < 5; pi++) {
                Employee emp = emps.get(pi);
                Employee nextEmp = nextPage.get(pi);
                Assert.assertSame(nextEmp, emp);
                System.out.println(index + "> " + emp);
            }
        }

        Assert.assertEquals(1, end.getEntries().size());
        Assert.assertTrue(stream.hasNext());
        Assert.assertFalse(stream.hasPrevious());

        try {
            stream.previous();
        } catch (IllegalStateException e) {
            return;
        } 

        Assert.fail("IllegalStateException not thrown on previous()");
    }

    @Test
    public void streamAllPreviousGet() {
        SQLTrace start = getDiagnostics().start();
        Assert.assertTrue(start.getEntries().isEmpty());

        EmployeeCriteria criteria = new EmployeeCriteria();
        criteria.setFirstName(null);
        criteria.setLastName(null);
        criteria.setPageSize(5);
        criteria.setPagingType(EntityPaging.Type.CURSOR.name());
        
        EntityPaging<Employee> stream = getRepository().getPaging(criteria);

        Assert.assertEquals(25, stream.size());

        SQLTrace end = getDiagnostics().stop();

        Assert.assertNotNull(end);
        Assert.assertSame(start, end);
        Assert.assertFalse(end.getEntries().isEmpty());
        Assert.assertEquals(1, end.getEntries().size());

        Assert.assertEquals(5, stream.getNumPages());

        // skip to end
        List<List<Employee>> pages = new ArrayList<List<Employee>>();
        pages.add(stream.next());
        Assert.assertEquals(1, stream.getCurrentPage());
        pages.add(stream.next());
        Assert.assertEquals(2, stream.getCurrentPage());
        pages.add(stream.next());
        Assert.assertEquals(3, stream.getCurrentPage());
        pages.add(stream.next());
        Assert.assertEquals(4, stream.getCurrentPage());
        pages.add(stream.next());
        Assert.assertEquals(5, stream.getCurrentPage());

        for (int index = 4; index > 0; index--) {
            Assert.assertEquals(index + 1, stream.getCurrentPage());
            Assert.assertTrue("No previous found at page: " + index + " stream at: " + stream.getCurrentPage(), stream.hasPrevious());

            List<Employee> emps = stream.get(index);

            if (index == 1) {
                Assert.assertFalse(stream.hasPrevious());
            } else {
                Assert.assertTrue("No previous found at page: " + index + " stream at: " + stream.getCurrentPage(), stream.hasPrevious());
            }

            Assert.assertEquals(5, emps.size());
            Assert.assertEquals(index, stream.getCurrentPage());

            List<Employee> nextPage = pages.get(index - 1);
            for (int pi = 0; pi < 5; pi++) {
                Employee emp = emps.get(pi);
                Employee nextEmp = nextPage.get(pi);
                Assert.assertSame(nextEmp, emp);
                System.out.println(index + "> " + emp);
            }
        }

        Assert.assertEquals(1, end.getEntries().size());
        Assert.assertTrue(stream.hasNext());
        Assert.assertFalse(stream.hasPrevious());

        try {
            stream.previous();
        } catch (IllegalStateException e) {
            return;
        } 

        Assert.fail("IllegalStateException not thrown on previous()");
    }

    private static EntityManagerFactory emf;

    public static EntityManagerFactory getEmf() {
        return emf;
    }

    @BeforeClass
    public static void createEMF() {
        emf = PersistenceTesting.createEMF(true);

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        new SamplePopulation().createNewEmployees(em, 25);
        em.getTransaction().commit();
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

    private EmployeeRepository repository;
    
    @Before
    public void setup() {
        this.repository = new EmployeeRepository();
        this.repository.setEntityManager(getEmf().createEntityManager());
        this.repository.getEntityManager().getTransaction().begin();
    }
    
    @After
    public void close() {
        this.repository.getEntityManager().getTransaction().commit();
        this.repository.getEntityManager().close();
    }

    public EmployeeRepository getRepository() {
        return repository;
    }
    
    public Diagnostics getDiagnostics() {
        return getRepository().getDiagnostics();
    }

}
