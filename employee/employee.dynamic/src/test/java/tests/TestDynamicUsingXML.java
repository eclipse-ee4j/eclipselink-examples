/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dclarke - Dynamic Persistence INCUBATION - Enhancement 200045
 *               http://wiki.eclipse.org/EclipseLink/Development/JPA/Dynamic
 *     
 * This code is being developed under INCUBATION and is not currently included 
 * in the automated EclipseLink build. The API in this code may change, or 
 * may never be included in the product. Please provide feedback through mailing 
 * lists or the bug database.
 ******************************************************************************/
package tests;

import static example.PersistenceHelper.EMPLOYEE_XML_PU;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.dynamic.ReflectiveDynamicClassLoader;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import example.PersistenceHelper;
import example.Queries;
import example.Samples;
import example.Transactions;

public class TestDynamicUsingXML {

    private static EntityManagerFactory emf;

    @Test
    public void queries() throws JAXBException {
        EntityManager em = emf.createEntityManager();

        // Lookup types
        JPADynamicHelper helper = new JPADynamicHelper(emf);
        DynamicType empType = helper.getType("Employee");

        // Run Queries
        Queries queries = new Queries();

        int minEmpId = queries.minimumEmployeeId(em);
        queries.findEmployee(em, empType, minEmpId);
        queries.findEmployeesUsingGenderIn(em);

        em.close();
    }

    @Test
    public void transactions() throws JAXBException {
        EntityManager em = emf.createEntityManager();

        Transactions txn = new Transactions();

        txn.createUsingPersist(em);

        em.close();
    }

    @Test
    public void validation() {
        EntityManager em = emf.createEntityManager();

        // Lookup types
        JPADynamicHelper helper = new JPADynamicHelper(emf);
        DynamicType empType = helper.getType("Employee");

        // Run Queries
        Queries queries = new Queries();

        int minEmpId = queries.minimumEmployeeId(em);
        DynamicEntity emp = queries.findEmployee(em, empType, minEmpId);

        em.getTransaction().begin();
        emp.set("firstName", null);
        emp.set("lastName", null);
        emp.set("gender", null);

        try {
            em.flush();
        } catch (ConstraintViolationException e) {
            System.out.println("ConstraintException: " + e.getMessage());
            for (ConstraintViolation<?> cv : e.getConstraintViolations()) {
                System.out.println("\t>> " + cv.getPropertyPath() + "::" + cv.getMessage());
            }
            return;
        }

        Assert.fail("ConstraintViolationException not thrown");
    }

    @BeforeClass
    public static void setup() throws JAXBException {
        // Create an entity manager factory with a dynamic class loader.
        DynamicClassLoader dcl = new ReflectiveDynamicClassLoader(Thread.currentThread().getContextClassLoader());
        emf = PersistenceHelper.createEntityManagerFactory(dcl, EMPLOYEE_XML_PU, true);

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        new Samples(emf).persistAll(em);
        em.getTransaction().commit();
        em.clear();
    }

    @AfterClass
    public static void close() {
        emf.close();
    }
}
