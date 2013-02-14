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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import eclipselink.example.jpa.employee.model.Employee;

/**
 * Example of paging a collection of {@link Employee} using FIRST/MAX results.
 * 
 * @since EclipseLink 2.4.2
 */
public class FirstMaxPaging extends EntityPaging<Employee> {

    private int size = -1;

    public FirstMaxPaging(EntityManagerFactory emf, CriteriaQuery<Employee> criteria, int pageSize) {
        super(emf, pageSize);
    }

    public int getNumPages() {
        return (size() / getPageSize()) + (size() % getPageSize() > 0 ? 1 : 0);
    }

    /**
     * Retrieve a page of Employee instances.
     */
    public List<Employee> get(int page) {
        EntityManager em = getEmf().createEntityManager();

        try {
            TypedQuery<Employee> empsQuery = em.createNamedQuery("Employee.findAll", Employee.class);
            int first = (page - 1) * getPageSize();
            empsQuery.setFirstResult(first);
            empsQuery.setMaxResults(getPageSize());
            this.currentPage = page;
            return empsQuery.getResultList();
        } finally {
            em.close();
        }
    }

    public int size() {
        if (this.size < 0) {
            EntityManager em = getEmf().createEntityManager();

            try {
                TypedQuery<Number> countQuery = em.createNamedQuery("Employee.count", Number.class);
                this.size = countQuery.getSingleResult().intValue();
            } finally {
                em.close();
            }
        }
        return this.size;
    }

}
