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
package example;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import eclipselink.example.jpa.employee.model.Employee;
import eclipselink.example.jpa.employee.model.Gender;
import eclipselink.example.jpa.employee.model.SamplePopulation;
import eclipselink.example.jpa.employee.test.PersistenceTesting;

/**
 * Examples illustrating the use of JPA with the employee domain
 * eclipselink.example.jpa.employee.model.
 * 
 * @see eclipselink.example.jpa.employee.test.model.JavaSEExampleTest
 * 
 * @author dclarke
 * @since EclipseLink 2.4
 */
public class JavaSEExample {

    public static void main(String[] args) throws Exception {
        EntityManagerFactory emf = PersistenceTesting.createEMF(true);
        JavaSEExample example = new JavaSEExample();

        try {
            EntityManager em = emf.createEntityManager();

            em.getTransaction().begin();
            new SamplePopulation().createNewEmployees(em, 10);

            // Add employee with 555 area code to satisfy a test query
            Employee e = new Employee();
            e.setFirstName("John");
            e.setLastName("Doe");
            e.setGender(Gender.Male);
            e.addPhoneNumber("HOME", "555", "5552222");
            em.persist(e);

            em.getTransaction().commit();
            em.clear();

            example.queryAllEmployees(em);
            em.clear();

            example.queryEmployeeLikeAreaCode55(em);
            em.clear();

            example.modifyEmployee(em, 1);
            em.clear();

            example.deleteEmployee(em, 1);
            em.clear();

            em.close();

        } finally {
            emf.close();
        }
    }

    public void queryAllEmployees(EntityManager em) {
        List<Employee> results = em.createQuery("SELECT e FROM Employee e", Employee.class).getResultList();

        System.out.println("Query All Results: " + results.size());

        results.forEach(e -> System.out.println("\t>" + e));
    }

    public void queryEmployeeLikeAreaCode55(EntityManager em) {
        System.out.println("\n\n --- Query Employee.phoneNumbers.areaCode LIKE '55%' ---");

        TypedQuery<Employee> query = em.createQuery("SELECT e FROM Employee e JOIN e.phoneNumbers phones WHERE phones.areaCode LIKE '55%'", Employee.class);
        List<Employee> emps = query.getResultList();

        emps.forEach(e -> System.out.println("> " + e));
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
