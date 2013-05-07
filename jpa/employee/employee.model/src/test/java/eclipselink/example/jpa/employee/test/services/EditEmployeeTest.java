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
import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eclipselink.example.jpa.employee.model.SamplePopulation;
import eclipselink.example.jpa.employee.services.EditEmployee;
import eclipselink.example.jpa.employee.services.EditEmployeeBean;
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
        EditEmployee edit = new EditEmployeeBean(getEmf(), sampleId);

        try {
            edit.save();
        } finally {
            edit.close();
        }
    }

    @Test
    public void incrementSalary() {
        EditEmployee edit = new EditEmployeeBean(getEmf(), sampleId);

        try {
            edit.getEmployee().setSalary(edit.getEmployee().getSalary() + 1);
            edit.save();
        } finally {
            edit.close();
        }
    }

    @Test
    public void optimisticLockFailure() {
        EditEmployee edit = new EditEmployeeBean(getEmf(), sampleId);

        try {
            edit.updateVersion();
            edit.getEmployee().setSalary(edit.getEmployee().getSalary() + 1);
            edit.save();
        } catch (RollbackException e) {
            if (e.getCause() instanceof OptimisticLockException) {
                return;
            }
            throw e;
        } finally {
            edit.close();
        }

        Assert.fail("OptimisticLockException not thrown");
    }

    @Test
    public void refreshUpdateAddress() {
        EditEmployee edit = new EditEmployeeBean(getEmf(), sampleId);

        try {
            edit.refresh();
            edit.getEmployee().getAddress().setCity("Ottawa");
            edit.save();
        } finally {
            edit.close();
        }

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
        new SamplePopulation().createNewEmployees(em, 1);

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
