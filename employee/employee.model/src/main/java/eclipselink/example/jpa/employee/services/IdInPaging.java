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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import eclipselink.example.jpa.employee.model.Employee;

/**
 * Example of paging a collection of {@link Employee} using id IN queries. This
 * approach uses an initial query to retrieve all of the entity identifiers
 * (generally requires single part keys). Each query for a page uses a separate
 * query for the set of entities using an IN with the page's IDs.
 * 
 * @since EclipseLink 2.4.2
 */
public class IdInPaging {

    /**
     * A named query is used with result caching enabled to minimize retrieving
     * the same page of entities multiple times.
     */
    private static final String QUERY_NAME = "Employee.idsIn";

    private EntityManagerFactory emf;

    private int pageSize;

    private int size;

    private List<List<Number>> idPages = new ArrayList<List<Number>>();

    public IdInPaging(EntityManagerFactory emf, TypedQuery<Number> idQuery, int pageSize) {
        super();
        this.emf = emf;
        this.pageSize = pageSize;

        List<Number> ids = idQuery.getResultList();
        this.size = ids.size();

        int start = 0;
        while (start < ids.size()) {
            List<Number> subList = ids.subList(start, start + pageSize);
            idPages.add(subList);
            start += subList.size();
        }
    }

    protected EntityManagerFactory getEmf() {
        return emf;
    }

    public int getPageSize() {
        return pageSize;
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

            return empsQuery.getResultList();
        } finally {
            em.close();
        }
    }

}
