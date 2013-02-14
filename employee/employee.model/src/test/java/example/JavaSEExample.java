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
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import eclipselink.example.jpa.employee.model.Employee;
import eclipselink.example.jpa.employee.model.Gender;
import eclipselink.example.jpa.employee.services.EmployeeCriteria;
import eclipselink.example.jpa.employee.services.EntityPaging;


/**
 * Examples illustrating the use of JPA with the employee domain eclipselink.example.jpa.employee.model.
 * 
 * @see test.JavaSEExampleTest
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

            example.createNewEmployees(em, 10);
            em.clear();

            example.queryAllEmployees(em);
            em.clear();

            example.queryEmployeeLikeAreaCode55(em);
            em.clear();

            example.modifyEmployee(em, 1);
            em.clear();

            example.deleteEmployee(em, 1);
            em.clear();
            
            example.pagingIdIn(emf);
            
            em.close();

        } finally {
            emf.close();
        }
    }


    public void queryAllEmployees(EntityManager em) {
        List<Employee> results = em.createQuery("SELECT e FROM Employee e", Employee.class).getResultList();

        System.out.println("Query All Results: " + results.size());
        for (Employee emp : results) {
            System.out.println("\t> " + emp);
        }
    }

    public void createNewEmployees(EntityManager em, int quantity) {
        System.out.println("\n\n --- Create New Employees + " + quantity + " ---");
        em.getTransaction().begin();

        for (int index = 0; index < quantity; index++) {
             em.persist(createRandomEmployee());
        }
        em.getTransaction().commit();
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
    
    public void pagingIdIn(EntityManagerFactory emf) {
        EmployeeCriteria criteria = new EmployeeCriteria();
        criteria.setFirstName(null);
        criteria.setLastName(null);
        criteria.setPagingType(EntityPaging.Type.PAGE.name());
        
        EntityPaging<Employee> paging = criteria.getPaging(emf);
        
        paging.get(1);
        paging.get(2);
    }

    private static final String[] MALE_FIRST_NAMES = { "Jacob", "Et", "Michael", "Alexander", "William", "Joshua", "Daniel", "Jayden", "Noah", "Anthony" };
    private static final String[] FEMALE_FIRST_NAMES = { "Isabella", "Emma", "Olivia", "Sophia", "Ava", "Emily", "Madison", "Abigail", "Chloe", "Mia" };
    private static final String[] LAST_NAMES = { "Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Garcia", "Rodriguez", "Wilson" };

    public Employee createRandomEmployee() {
        Random r = new Random();

        Employee emp = new Employee();
        emp.setGender(Gender.values()[r.nextInt(2)]);
        if (Gender.Male.equals(emp.getGender())) {
            emp.setFirstName(MALE_FIRST_NAMES[r.nextInt(MALE_FIRST_NAMES.length)]);
        } else {
            emp.setFirstName(FEMALE_FIRST_NAMES[r.nextInt(FEMALE_FIRST_NAMES.length)]);
        }
        emp.setLastName(LAST_NAMES[r.nextInt(LAST_NAMES.length)]);
        emp.addPhoneNumber("555", "111", "5552222");
        
        return emp;
    }
}
