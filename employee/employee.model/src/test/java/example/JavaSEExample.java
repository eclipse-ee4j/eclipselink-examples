/*******************************************************************************
 * Copyright (c) 2010-2012 Oracle. All rights reserved.
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
package example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.config.PersistenceUnitProperties;


import model.Employee;

/**
 * Examples illustrating the use of JPA with the employee domain model.
 * 
 * @see test.JavaSEExampleTest
 * 
 * @author dclarke
 * @since EclipseLink 2.4
 */
public class JavaSEExample {

    public static void main(String[] args) throws Exception {
        EntityManagerFactory emf = createEMF(true);
        JavaSEExample example = new JavaSEExample();

        try {
            EntityManager em = emf.createEntityManager();

            example.createNewEmployees(em);
            example.createNewEmployees(em);
            example.createNewEmployees(em);
            example.createNewEmployees(em);
            example.createNewEmployees(em);
            example.createNewEmployees(em);
                       em.clear();

            example.queryAllEmployees(em);
            em.clear();

            example.queryEmployeeLikeAreaCode55(em);
            em.clear();

            example.modifyEmployee(em, 1);
            em.clear();

            example.deleteEmployee(em, 1);
            em.close();

        } finally {
            emf.close();
        }
    }

    /**
     * 
     */
    public static EntityManagerFactory createEMF(boolean replaceTables) {
        Map<String, Object> props = new HashMap<String, Object>();

        ExamplePropertiesLoader.loadProperties(props);

        if (replaceTables) {
            props.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
            props.put(PersistenceUnitProperties.DDL_GENERATION_MODE, PersistenceUnitProperties.DDL_DATABASE_GENERATION);
        }

        return Persistence.createEntityManagerFactory("employee", props);
    }

    public void queryAllEmployees(EntityManager em) {
        List<Employee> results = em.createQuery("SELECT e FROM Employee e", Employee.class).getResultList();

        System.out.println("Query All Results: " + results.size());
        for (Employee emp : results) {
            System.out.println("\t> " + emp);
        }
    }

    public Employee createNewEmployees(EntityManager em) {
        System.out.println("\n\n --- Create New Employee ---");
        em.getTransaction().begin();

        Employee newEmp = new Employee();
        newEmp.setFirstName("John");
        newEmp.setLastName("Doe");
        newEmp.addPhoneNumber("Work", "555", "5555555");
        newEmp.addPhoneNumber("Home", "555", "1111111");

        em.persist(newEmp);

        em.getTransaction().commit();

        return newEmp;
    }

    public void queryEmployeeLikeAreaCode55(EntityManager em) {
        System.out.println("\n\n --- Query Employee.phoneNumbers.areaCode LIKE '55%' ---");

        TypedQuery<Employee> query = em.createQuery("SELECT e FROM Employee e JOIN e.phoneNumbers phones WHERE phones.areaCode LIKE '55%'", Employee.class);
        List<Employee> emps = query.getResultList();

        for (Employee e : emps) {
            System.out.println("> " + e);
        }
    }

    public void modifyEmployee(EntityManager em, int id) {
        System.out.println("\n\n --- Modify Employee ---");
        em.getTransaction().begin();

        Employee emp = em.find(Employee.class, id);
        emp.setSalary(1);

        TypedQuery<Employee> query = em.createQuery("SELECT e FROM Employee e WHERE e.id = :ID AND e.firstName = :FNAME", Employee.class);
        query.setParameter("ID", id);
        query.setParameter("FNAME", emp.getFirstName());
        emp = query.getSingleResult();

        em.getTransaction().commit();

    }

    public void deleteEmployee(EntityManager em, int id) {
        em.getTransaction().begin();

        em.remove(em.find(Employee.class, id));
        em.flush();
        
        em.getTransaction().rollback();

    }
}
