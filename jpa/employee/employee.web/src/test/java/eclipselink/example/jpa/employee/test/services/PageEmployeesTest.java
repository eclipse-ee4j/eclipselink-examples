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

import org.eclipse.persistence.jpa.JpaHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eclipselink.example.jpa.employee.model.Employee;
import eclipselink.example.jpa.employee.model.SamplePopulation;
import eclipselink.example.jpa.employee.services.EmployeeCriteria;
import eclipselink.example.jpa.employee.services.EmployeeRepository;
import eclipselink.example.jpa.employee.services.paging.EntityPaging;
import eclipselink.example.jpa.employee.services.persistence.SQLCapture;
import eclipselink.example.jpa.employee.services.persistence.SQLCapture.SQLTrace;
import eclipselink.example.jpa.employee.test.PersistenceTesting;

/**
 * TODO
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
public class PageEmployeesTest {

    @Test
    public void page5ByIndex() {

        SQLTrace start = diagnostics.getTrace();
        Assert.assertTrue(start.getEntries().isEmpty());

        EmployeeCriteria criteria = new EmployeeCriteria(10);
        criteria.setFirstName(null);
        criteria.setLastName(null);
        criteria.setPageSize(5);
        criteria.setPagingType(EntityPaging.Type.PAGE.name());

        EntityPaging<Employee> paging = getRepository().getPaging(criteria);

        Assert.assertEquals(25, paging.size());

        SQLTrace end = diagnostics.getTrace(true);

        Assert.assertNotNull(end);
        Assert.assertSame(start, end);
        Assert.assertEquals(1, end.getEntries().size());

        Assert.assertEquals(5, paging.getNumPages());

        for (int index = 0; index < 5; index++) {
            List<Employee> emps = paging.get(index + 1);
            Assert.assertEquals(5, emps.size());
            emps.forEach(e -> System.out.println("> " + e));

        }

        Assert.assertFalse(paging.hasNext());

    }

    @Test
    public void page5ByNext() {

        SQLTrace start = diagnostics.getTrace();
        Assert.assertTrue(start.getEntries().isEmpty());

        EmployeeCriteria criteria = new EmployeeCriteria(10);
        criteria.setFirstName(null);
        criteria.setLastName(null);
        criteria.setPageSize(5);
        criteria.setPagingType(EntityPaging.Type.PAGE.name());

        EntityPaging<Employee> paging = getRepository().getPaging(criteria);

        Assert.assertEquals(25, paging.size());

        SQLTrace end = diagnostics.getTrace(true);

        Assert.assertNotNull(end);
        Assert.assertSame(start, end);
        Assert.assertEquals(1, end.getEntries().size());

        Assert.assertEquals(5, paging.getNumPages());

        for (int index = 0; index < 5; index++) {
            List<Employee> emps = paging.next();
            Assert.assertEquals(5, emps.size());
            emps.forEach(e -> System.out.println("> " + e));
        }

        Assert.assertFalse(paging.hasNext());

    }

    @Test
    public void page10ByIndex() {

        SQLTrace start = diagnostics.getTrace();
        Assert.assertTrue(start.getEntries().isEmpty());

        EmployeeCriteria criteria = new EmployeeCriteria(10);
        criteria.setFirstName(null);
        criteria.setLastName(null);
        criteria.setPageSize(10);
        criteria.setPagingType(EntityPaging.Type.PAGE.name());

        EntityPaging<Employee> paging = getRepository().getPaging(criteria);

        Assert.assertEquals(25, paging.size());

        SQLTrace end = diagnostics.getTrace(true);

        Assert.assertNotNull(end);
        Assert.assertSame(start, end);
        Assert.assertFalse(end.getEntries().isEmpty());
        Assert.assertEquals(1, end.getEntries().size());

        Assert.assertEquals(3, paging.getNumPages());

        for (int index = 0; index < paging.getNumPages(); index++) {
            List<Employee> emps = paging.get(index + 1);
            Assert.assertEquals(index < 2 ? 10 : 5, emps.size());
            emps.forEach(e -> System.out.println("> " + e));
        }

        Assert.assertFalse(paging.hasNext());

    }

    @Test
    public void page10ByNext() {

        SQLTrace start = diagnostics.getTrace();
        Assert.assertTrue(start.getEntries().isEmpty());

        EmployeeCriteria criteria = new EmployeeCriteria(10);
        criteria.setFirstName(null);
        criteria.setLastName(null);
        criteria.setPageSize(10);
        criteria.setPagingType(EntityPaging.Type.PAGE.name());

        EntityPaging<Employee> paging = getRepository().getPaging(criteria);

        Assert.assertEquals(25, paging.size());

        SQLTrace end = diagnostics.getTrace(true);

        Assert.assertNotNull(end);
        Assert.assertSame(start, end);
        Assert.assertFalse(end.getEntries().isEmpty());
        Assert.assertEquals(1, end.getEntries().size());

        Assert.assertEquals(3, paging.getNumPages());

        for (int index = 0; index < paging.getNumPages(); index++) {
            List<Employee> emps = paging.next();
            Assert.assertEquals(index < 2 ? 10 : 5, emps.size());
            emps.forEach(e -> System.out.println("> " + e));
        }

        Assert.assertFalse(paging.hasNext());

    }

    private static EntityManagerFactory emf;

    private static SQLCapture diagnostics;

    public static EntityManagerFactory getEmf() {
        return emf;
    }

    @BeforeClass
    public static void createEMF() {
        emf = PersistenceTesting.createEMF(true);
        diagnostics = new SQLCapture(JpaHelper.getServerSession(getEmf()));

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
        
        diagnostics.clear();
    }

    @After
    public void close() {
        this.repository.getEntityManager().getTransaction().commit();
        this.repository.getEntityManager().close();
    }

    public EmployeeRepository getRepository() {
        return repository;
    }

}
