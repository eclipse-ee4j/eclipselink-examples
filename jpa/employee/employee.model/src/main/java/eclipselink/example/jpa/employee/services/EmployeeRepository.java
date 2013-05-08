/*******************************************************************************
 * Copyright (c) 2010-2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  dclarke - EclipseLink 2.3 - MySports Demo Bug 344608
 ******************************************************************************/
package eclipselink.example.jpa.employee.services;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import eclipselink.example.jpa.employee.model.Address;
import eclipselink.example.jpa.employee.model.Employee;
import eclipselink.example.jpa.employee.model.PhoneNumber;
import eclipselink.example.jpa.employee.services.paging.EntityPaging;

/**
 * Edit service for an {@link Employee} instance.
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
@Stateless
@LocalBean
public class EmployeeRepository {

    private EntityManager entityManager;

    private Diagnostics diagnostics;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    @PersistenceContext(unitName = "employee")
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.diagnostics = Diagnostics.getInstance(entityManager);
    }

    public Diagnostics getDiagnostics() {
        return diagnostics;
    }

    public Employee find(int id) {
        return getEntityManager().find(Employee.class, id);
    }

    public Employee save(Employee employee) {
        Employee emp = getEntityManager().merge(employee);

        // Ensure the Employee's lock value is incremented
        getEntityManager().lock(emp, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        getEntityManager().flush();
        return emp;
    }

    public void delete(Employee employee) {
        Employee emp = getEntityManager().merge(employee);
        getEntityManager().remove(emp);
    }

    public Employee refresh(Employee employee) {
        Employee emp = employee;
        if (!getEntityManager().contains(employee)) {
            emp = getEntityManager().merge(employee);
        }
        getEntityManager().refresh(emp);
        return emp;
    }

    public int updateVersion(Employee employee) {
        Employee emp = getEntityManager().merge(employee);
        getEntityManager().createNativeQuery("UPDATE EMPLOYEE SET VERSION = VERSION + 1 WHERE EMP_ID = " + emp.getId()).executeUpdate();
        Number result = (Number) getEntityManager().createNativeQuery("SELECT VERSION FROM EMPLOYEE WHERE EMP_ID = " + emp.getId()).getSingleResult();
        return result.intValue();
    }

    public void removeAddress(Employee employee) {
        Employee emp = getEntityManager().merge(employee);
        emp.setAddress(null);
    }

    public Address addAddress(Employee employee) {
        Employee emp = getEntityManager().merge(employee);
        emp.setAddress(new Address());
        return emp.getAddress();
    }

    public PhoneNumber addPhone(Employee employee, String type) {
        if (type != null && !type.isEmpty()) {
            Employee emp = getEntityManager().merge(employee);
            return emp.addPhoneNumber(type, "", "");
        }
        return null;
    }

    public void remove(Employee employee, PhoneNumber phone) {
        Employee emp = getEntityManager().merge(employee);
        PhoneNumber p = getEntityManager().merge(phone);
        emp.removePhoneNumber(p);
    }

    public EntityPaging<Employee> getPaging(EmployeeCriteria criteria) {
        return criteria.getPaging(getEntityManager().getEntityManagerFactory());
    }

    public List<Employee> getEmployees(EmployeeCriteria criteria) {
        CriteriaQuery<Employee> cq = criteria.createQuery(getEntityManager());
        return getEntityManager().createQuery(cq).getResultList();
    }
}
