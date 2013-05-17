/*******************************************************************************
 * Copyright (c) 2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  dclarke - initial
 ******************************************************************************/
package eclipselink.example.jpa.employee.services;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import eclipselink.example.jpa.employee.model.Employee;
import eclipselink.example.jpa.employee.model.Employee_;
import eclipselink.example.jpa.employee.services.paging.EntityPaging;
import eclipselink.example.jpa.employee.services.paging.EntityPaging.Type;
import eclipselink.example.jpa.employee.services.paging.FirstMaxPaging;
import eclipselink.example.jpa.employee.services.paging.IdInPaging;

/**
 * Search criteria definition. View layer populates this criteria and passes it
 * to the services layer for execution.
 * 
 * @author dclarke
 * @since EclipseLInk 2.4.2
 */
public class EmployeeCriteria {

    private String firstName = "%";

    private String lastName = "%";

    private String pagingType = "NONE";

    private int pageSize = 10;

    public EmployeeCriteria(int pageSize) {
        super();
        this.pageSize = pageSize;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPagingType() {
        return pagingType;
    }

    public void setPagingType(String pagingType) {
        this.pagingType = pagingType;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @SuppressWarnings("unchecked")
    public CriteriaQuery<Employee> createQuery(EntityManagerFactory emf) {
        CriteriaBuilder cb = emf.getCriteriaBuilder();
        CriteriaQuery<Employee> query = cb.createQuery(Employee.class);
        Root<Employee> employee = query.from(Employee.class);
        return (CriteriaQuery<Employee>) addWhereOrder(cb, query, employee, true);
    }

    @SuppressWarnings("unchecked")
    public CriteriaQuery<Employee> createQuery(EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> query = cb.createQuery(Employee.class);
        Root<Employee> employee = query.from(Employee.class);
        return (CriteriaQuery<Employee>) addWhereOrder(cb, query, employee, true);
    }

    @SuppressWarnings("unchecked")
    public CriteriaQuery<Number> createIdQuery(EntityManagerFactory emf) {
        CriteriaBuilder cb = emf.getCriteriaBuilder();
        CriteriaQuery<Number> query = cb.createQuery(Number.class);
        Root<Employee> employee = query.from(Employee.class);
        query.select(employee.get(Employee_.id));
        return (CriteriaQuery<Number>) addWhereOrder(cb, query, employee, true);
    }

    @SuppressWarnings("unchecked")
    public CriteriaQuery<Number> createCountQuery(EntityManagerFactory emf) {
        CriteriaBuilder cb = emf.getCriteriaBuilder();
        CriteriaQuery<Number> query = cb.createQuery(Number.class);
        Root<Employee> employee = query.from(Employee.class);
        query.select(cb.count(employee.get(Employee_.id)));
        return (CriteriaQuery<Number>) addWhereOrder(cb, query, employee, false);
    }

    private CriteriaQuery<?> addWhereOrder(CriteriaBuilder cb, CriteriaQuery<?> query, Root<Employee> employee, boolean order) {
        Predicate where = cb.conjunction();

        if (getFirstName() != null && !getFirstName().isEmpty()) {
            where = cb.and(where, cb.like(employee.get(Employee_.firstName), getFirstName()));
        }

        if (getLastName() != null && !getLastName().isEmpty()) {
            where = cb.and(where, cb.like(employee.get(Employee_.lastName), getLastName()));
        }

        query.where(where);

        if (order) {
            query.orderBy(cb.asc(employee.get(Employee_.id)));
        }

        return query;
    }

    protected EntityPaging<Employee> getPaging(EntityManagerFactory emf) {
        if (getPagingType() != null) {
            Type type = EntityPaging.Type.valueOf(getPagingType());

            switch (type) {
            case NONE:
                return null;
            case PAGE:
                return new FirstMaxPaging(emf, createQuery(emf), createCountQuery(emf), getPageSize());
            case PAGE_IN:
                return new IdInPaging(emf, createIdQuery(emf), getPageSize());
            }
        }
        return null;
    }

    public void reset() {
        this.firstName = "%";
        this.lastName = "%";
        this.pagingType = "NONE";
    }

}
