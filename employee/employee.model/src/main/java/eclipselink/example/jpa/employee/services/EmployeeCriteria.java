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

import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import eclipselink.example.jpa.employee.model.Employee;
import eclipselink.example.jpa.employee.model.Employee_;
import eclipselink.example.jpa.employee.services.EntityPaging.Type;

/**
 * TODO
 * 
 * @author dclarke
 * @since EclipseLInk 2.4.2
 */
public class EmployeeCriteria {

    private String firstName = "%";

    private String lastName = "%";

    private String pagingType = "None";

    private int pageSize = 10;

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

    public CriteriaQuery<Employee> createQuery(EntityManagerFactory emf) {
        CriteriaBuilder cb = emf.getCriteriaBuilder();
        CriteriaQuery<Employee> query = cb.createQuery(Employee.class);
        Root<Employee> employee = query.from(Employee.class);

        Predicate where = cb.conjunction();

        if (getFirstName() != null && !getFirstName().isEmpty()) {
            where = cb.and(where, cb.like(employee.get(Employee_.firstName), getFirstName()));
        }

        if (getLastName() != null && !getLastName().isEmpty()) {
            where = cb.and(where, cb.like(employee.get(Employee_.lastName), getLastName()));
        }

        query.where(where);
        query.orderBy(cb.asc(employee.get(Employee_.id)));

        return query;
    }

    public CriteriaQuery<Number> createIdQuery(EntityManagerFactory emf) {
        CriteriaBuilder cb = emf.getCriteriaBuilder();
        CriteriaQuery<Number> query = cb.createQuery(Number.class);
        Root<Employee> employee = query.from(Employee.class);
        query.select(employee.get(Employee_.id));
        
        Predicate where = cb.conjunction();

        if (getFirstName() != null && !getFirstName().isEmpty()) {
            where = cb.and(where, cb.like(employee.get(Employee_.firstName), getFirstName()));
        }

        if (getLastName() != null && !getLastName().isEmpty()) {
            where = cb.and(where, cb.like(employee.get(Employee_.lastName), getLastName()));
        }

        query.where(where);
        query.orderBy(cb.asc(employee.get(Employee_.id)));

        return query;
    }

    public EntityPaging<Employee> getPaging(EntityManagerFactory emf) {
        Type type = EntityPaging.Type.valueOf(getPagingType());

        switch (type) {
        case NONE:
            return null;
        case PAGE:
            return new FirstMaxPaging(emf, createQuery(emf), getPageSize());
        case PAGE_IN:
            return new IdInPaging(emf, createIdQuery(emf), getPageSize());
        case CURSOR:
            return new StreamPaging<Employee>(emf, createQuery(emf), getPageSize());
        }

        return null;
    }

}
