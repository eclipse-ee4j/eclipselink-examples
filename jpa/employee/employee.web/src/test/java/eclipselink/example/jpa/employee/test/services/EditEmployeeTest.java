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
import eclipselink.example.jpa.employee.services.EmployeeRepository;
import eclipselink.example.jpa.employee.test.PersistenceTesting;

/**
 * TODO
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
public class EditEmployeeTest {

    @Test
    public void saveWithoutChanges() {
        Employee emp = this.repository.find(sampleId, true);

        Assert.assertNotNull(emp);

        repository.save(emp);
    }

    @Test
    public void incrementSalary() {
        Employee emp = this.repository.find(sampleId, true);

        emp.setSalary(emp.getSalary() + 1);

        repository.save(emp);
    }

    @Test
    public void optimisticLockFailure() {
        Employee emp = this.repository.find(sampleId, true);

        repository.updateVersion(emp);
        emp.setSalary(emp.getSalary() + 1);

        Employee result = repository.save(emp);

        Assert.assertNull(result);
        repository.getEntityManager().getTransaction().rollback();
    }

    //@Test
    public void refreshUpdateAddress() {
        Employee emp = this.repository.find(sampleId, true);

        emp = repository.refresh(emp);
        emp.getAddress().setCity("Ottawa");
        repository.save(emp);

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
        if (this.repository.getEntityManager().getTransaction().isActive()) {
            this.repository.getEntityManager().getTransaction().commit();
        }
        this.repository.getEntityManager().close();
    }

    public EmployeeRepository getRepository() {
        return repository;
    }

    private static EntityManagerFactory emf;

    private static int sampleId;

    public static EntityManagerFactory getEmf() {
        return emf;
    }

    @Before
    public void clearCache() {
        getEmf().getCache().evictAll();
    }

    @BeforeClass
    public static void createEMF() {
        emf = PersistenceTesting.createEMF(true);

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        new SamplePopulation().createNewEmployees(em, 1);
        em.getTransaction().commit();

        sampleId = em.createQuery("SELECT e.id FROM Employee e", Integer.class).getSingleResult();
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
