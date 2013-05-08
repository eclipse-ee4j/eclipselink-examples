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
package eclipselink.example.jpa.employee.services.paging;

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

    private CriteriaQuery<Employee> criteria;

    private CriteriaQuery<Number> countCriteria;

    public FirstMaxPaging(EntityManagerFactory emf, CriteriaQuery<Employee> criteria, CriteriaQuery<Number> countCriteria, int pageSize) {
        super(emf, pageSize);
        this.criteria = criteria;
        this.countCriteria = countCriteria;
    }

    public CriteriaQuery<Employee> getCriteria() {
        return criteria;
    }

    public CriteriaQuery<Number> getCountCriteria() {
        return countCriteria;
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
            TypedQuery<Employee> empsQuery = em.createQuery(getCriteria());
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
                TypedQuery<Number> countQuery = em.createQuery(getCountCriteria());
                this.size = countQuery.getSingleResult().intValue();
            } finally {
                em.close();
            }
        }
        return this.size;
    }

}
