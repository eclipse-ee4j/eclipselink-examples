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

import model.Employee;
import example.util.ExamplePropertiesLoader;

public class QueryExamples {

    public static void main(String[] args) {
        Map<String, Object> props = new HashMap<String, Object>();

        ExamplePropertiesLoader.loadProperties(props);

        // Enable Schema Gen
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("employee", props);

        EntityManager em = emf.createEntityManager();

        System.out.println("\n\n --- Query for all Employee ---");
        List<Employee> emps = em.createQuery("SELECT e FROM Employee e", Employee.class).getResultList();
        
        for (Employee e: emps) {
            System.out.println("> " + e);
        }

        em.close();
        emf.close();
    }
}
