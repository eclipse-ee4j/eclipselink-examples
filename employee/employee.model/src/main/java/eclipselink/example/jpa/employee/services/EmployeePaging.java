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
 *      dclarke - initial 
 ******************************************************************************/
package eclipselink.example.jpa.employee.services;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;

import eclipselink.example.jpa.employee.model.Employee;

/**
 * Example of paging a collection of {@link Employee} using FIRST/MAX results.
 * 
 * @since EclipseLink 2.4.2
 */
@Stateless
@LocalBean
public class EmployeePaging {

    private EntityManagerFactory emf;

    public EntityManagerFactory getEmf() {
        return emf;
    }

    @PersistenceUnit(unitName = "employee")
    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * Retrieve a page of Employee instances.
     * 
     * @param startPosition position of the first result, 
     *        numbered from 0
     * @param maxResult  maximum number of results to retrieve
     */
    public List<Employee> get(int startPosition, int maxResult) {
        EntityManager em = getEmf().createEntityManager();

        try {
            TypedQuery<Employee> empsQuery = em.createQuery("SELECT e FROM Employee e ORDER BY e.lastName,  e.firstName", Employee.class);
            empsQuery.setFirstResult(startPosition);
            empsQuery.setMaxResults(maxResult);

            return empsQuery.getResultList();
        } finally {
            em.close();
        }
    }

    public int size() {
        EntityManager em = getEmf().createEntityManager();

        try {
            TypedQuery<Number> countQuery = em.createQuery("SELECT COUNT(e) FROM Employee", Number.class);
           return countQuery.getSingleResult().intValue();
        } finally {
            em.close();
        }
    }


}
