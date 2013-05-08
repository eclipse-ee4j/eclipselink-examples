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

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceUnit;

import eclipselink.example.jpa.employee.model.Address;
import eclipselink.example.jpa.employee.model.Employee;
import eclipselink.example.jpa.employee.model.PhoneNumber;

/**
 * Edit service for an {@link Employee} instance.
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
@Stateless
@LocalBean
public class EditEmployeeBean {

    private EntityManagerFactory emf;

    private Employee employee;

    private Diagnostics diagnostics;

    public EditEmployeeBean() {
        super();
    }

    public EntityManagerFactory getEmf() {
        return emf;
    }

    @PersistenceUnit(unitName = "employee")
    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
        this.diagnostics = Diagnostics.getInstance(emf);
    }

    public Diagnostics getDiagnostics() {
        return diagnostics;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        if (employee != null) {
            employee.getAddress();
            employee.getPhoneNumbers().size();
        }
    }

    public Employee setEmployee(int id) {
        EntityManager em = getEmf().createEntityManager();

        try {
            this.employee = em.find(Employee.class, id);
            return this.employee;
        } finally {
            em.close();
        }
    }

    public boolean isNew() {
        return getEmployee().getId() <= 0;
    }

    public void save() {
        EntityManager em = getEmf().createEntityManager();

        try {
            this.employee = em.merge(getEmployee());
            em.getTransaction().begin();

            // Ensure the Employee's lock value is incremented
            em.lock(getEmployee(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void delete() {
        EntityManager em = getEmf().createEntityManager();

        try {
            this.employee = em.merge(getEmployee());
            em.getTransaction().begin();
            em.remove(getEmployee());
            em.getTransaction().commit();
        } finally {
            setEmployee(null);
        }
    }

    public void refresh() {
        EntityManager em = getEmf().createEntityManager();

        try {
            if (!em.contains(getEmployee())) {
                setEmployee(em.merge(getEmployee()));
            }
            em.refresh(getEmployee());
        } finally {
            em.close();
        }
    }

    public int updateVersion() {
        EntityManager em = getEmf().createEntityManager();

        try {
            this.employee = em.merge(getEmployee());
            em.getTransaction().begin();
            em.createNativeQuery("UPDATE EMPLOYEE SET VERSION = VERSION + 1 WHERE EMP_ID = " + getEmployee().getId()).executeUpdate();
            em.getTransaction().commit();
            setEmployee(em.merge(getEmployee()));
            Number result = (Number) em.createNativeQuery("SELECT VERSION FROM EMPLOYEE WHERE EMP_ID = " + getEmployee().getId()).getSingleResult();
            return result.intValue();
        } finally {
            em.close();
        }
    }

    public String removeAddress() {
        getEmployee().setAddress(null);
        return null;
    }

    public String addAddress() {
        getEmployee().setAddress(new Address());
        return null;
    }

    public PhoneNumber addPhone(String type) {
        if (type != null && !type.isEmpty()) {
            return getEmployee().addPhoneNumber(type, "", "");
        }
        return null;
    }

    public void remove(PhoneNumber phone) {
        getEmployee().removePhoneNumber(phone);
    }

}
