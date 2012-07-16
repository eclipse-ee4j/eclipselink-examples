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
 *  dclarke - Employee Demo 2.4
 ******************************************************************************/
package example.advanced;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import model.Address;
import model.Employee;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;

import example.util.ExamplePropertiesLoader;

public class NativeQueries {

    public static void main(String[] args) {
        Map<String, Object> props = new HashMap<String, Object>();

        ExamplePropertiesLoader.loadProperties(props);

        // Enable Schema Gen
        props.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
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
        newEmp.setAddress(new Address("Ottawa", "Canada", "ON", "K1P1A4", "45 O'Connor Street"));

        em.persist(newEmp);

        em.getTransaction().commit();

        em.close();
        emf.getCache().evictAll();

        nativeUnmappedJoin(emf);

        emf.getCache().evictAll();

        nativeSubQuery(emf);

        emf.close();
    }

    private static void nativeUnmappedJoin(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        System.out.println("\n\n --- Native SubSelect Query ---");

        ReadAllQuery raq = new ReadAllQuery(Employee.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();
        Expression fnameExp = eb.get("firstName").equal("Doug");

        ExpressionBuilder addrEB = new ExpressionBuilder(Address.class);
        ReportQuery subQuery = new ReportQuery(Address.class, addrEB);
        subQuery.addAttribute("id");
        Expression joinExp = addrEB.get("id").equal(eb.getField("ADDR_ID"));
        Expression cityExp = addrEB.get("city").equal("Ottawa");
        subQuery.setSelectionCriteria(cityExp.and(joinExp));

        raq.setSelectionCriteria(fnameExp.and(eb.exists(subQuery)));

        List<Employee> emps = JpaHelper.createQuery(raq, em).getResultList();

        for (Employee e : emps) {
            System.out.println("> " + e);
        }

        em.close();
    }

    private static void nativeSubQuery(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        System.out.println("\n\n --- Native Subquery Query ---");

        ReadAllQuery raq = new ReadAllQuery(Employee.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();
        Expression fnameExp = eb.get("firstName").equal("Doug");
        ExpressionBuilder addrEB = new ExpressionBuilder(Address.class);
        Expression joinExp = addrEB.get("id").equal(eb.getField("ADDR_ID"));
        Expression cityExp = addrEB.get("city").equal("Ottawa");
        raq.setSelectionCriteria(fnameExp.and(joinExp.and(cityExp)));

        List<Employee> emps = JpaHelper.createQuery(raq, em).getResultList();

        for (Employee e : emps) {
            System.out.println("> " + e);
        }

        em.close();
    }
}
