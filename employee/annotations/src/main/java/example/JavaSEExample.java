/*******************************************************************************
 * Copyright (c) 2010-2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  dclarke - Employee Demo 2.3
 ******************************************************************************/
package example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import model.Employee;

import org.eclipse.persistence.config.PersistenceUnitProperties;

import example.util.ExamplePropertiesLoader;

public class JavaSEExample {

    public static void main(String[] args) {
        Map<String, Object> props = new HashMap<String, Object>();
        
        ExamplePropertiesLoader.loadProperties(props);
        
        // Enable Schema Gen
        props.put(PersistenceUnitProperties.DDL_GENERATION  , PersistenceUnitProperties.DROP_AND_CREATE);
        props.put(PersistenceUnitProperties.DDL_GENERATION_MODE, PersistenceUnitProperties.DDL_DATABASE_GENERATION);
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("employee", props);

        EntityManager em = emf.createEntityManager();

        System.out.println("\n\n --- Query for all Employee ---");
        em.createQuery("SELECT e FROM Employee e").getResultList();

        System.out.println("\n\n --- Create New Employee ---");
        em.getTransaction().begin();
        
        Employee newEmp = new Employee();
        newEmp.setFirstName("Doug");
        newEmp.setLastName("Clarke");
        newEmp.addPhoneNumber("Work", "555", "5555555");
        newEmp.addPhoneNumber("Home", "555", "1111111");
        
        em.persist(newEmp);
        
        em.getTransaction().commit();
        
        em.clear();
        em.getEntityManagerFactory().getCache().evictAll();

        System.out.println("\n\n --- Query Employee ---");
        
        TypedQuery<Employee> query = em.createQuery("SELECT e FROM Employee e JOIN e.phoneNumbers phones WHERE phones.areaCode LIKE '55%'", Employee.class);
        List<Employee> emps = query.getResultList();
        
        for (Employee e: emps) {
            System.out.println("> " + e);
        }
        
        em.clear();
        em.getEntityManagerFactory().getCache().evictAll();

        System.out.println("\n\n --- Modify Employee ---");
        em.getTransaction().begin();
        
        Employee emp = em.find(Employee.class, newEmp.getId());
        emp.setSalary(1);

        query = em.createQuery("SELECT e FROM Employee e WHERE e.id = :ID AND e.firstName = :FNAME", Employee.class);
        query.setParameter("ID", newEmp.getId());
        query.setParameter("FNAME", newEmp.getFirstName());
        emp = query.getSingleResult();
        
        em.getTransaction().commit();
        
        
        
        System.out.println("\n\n --- Delete Employee ---");
        em.getTransaction().begin();
        
        em.remove(em.find(Employee.class, newEmp.getId()));
        
        em.getTransaction().commit();
        
        
        
        em.close();
        emf.close();
    }
}
