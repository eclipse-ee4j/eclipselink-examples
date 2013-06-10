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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import eclipselink.example.jpa.employee.model.Employee;

/**
 * Example of paging a collection of {@link Employee} using id IN queries. This
 * approach uses an initial query to retrieve all of the entity identifiers
 * (generally requires single part keys). Each query for a page uses a separate
 * query for the set of entities using an IN with the page's IDs.
 * 
 * @since EclipseLink 2.4.2
 */
public class IdInPaging extends EntityPaging<Employee> {

    /**
     * A named query is used with result caching enabled to minimize retrieving
     * the same page of entities multiple times.
     */
    private static final String QUERY_NAME = "Employee.idsIn";

    private int size;

    private List<List<Number>> idPages = new ArrayList<List<Number>>();

    public IdInPaging(EntityManagerFactory emf, CriteriaQuery<Number> criteria, int pageSize) {
        super(emf, pageSize);

        EntityManager em = emf.createEntityManager();
        List<Number> ids = em.createQuery(criteria).getResultList();
        em.close();
        this.size = ids.size();

        int start = 0;
        while (start < ids.size()) {
            int end = start + pageSize;
            if (end > this.size) {
                end = this.size;
            }
            List<Number> subList = ids.subList(start, end);
            idPages.add(subList);
            start = end;
        }
    }

    public int getNumPages() {
        return this.idPages.size();
    }

    public int size() {
        return this.size;
    }

    /**
     * Retrieve a page of Employee instances.
     */
    public List<Employee> get(int pageNum) {
        List<Number> ids = this.idPages.get(pageNum - 1);
        EntityManager em = getEmf().createEntityManager();

        try {
            TypedQuery<Employee> empsQuery = em.createNamedQuery(QUERY_NAME, Employee.class);
            empsQuery.setParameter("IDS", ids);
            this.currentPage = pageNum;
            return empsQuery.getResultList();
        } finally {
            em.close();
        }
    }

}
