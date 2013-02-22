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

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;

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
@Local(EditEmployee.class)
public class EditEmployeeBean implements EditEmployee {

    private EntityManager entityManager;

    private Employee employee;

    public EditEmployeeBean() {
        super();
    }

    public EditEmployeeBean(EntityManagerFactory emf, Integer id) {
        this.entityManager = emf.createEntityManager();
        if (id != null) {
            setEmployee(getEntityManager().find(Employee.class, id));
        } else {
            this.employee = new Employee();
        }
    }

    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see eclipselink.example.jpa.employee.services.EditEmployee#getEmployee()
     */
    @Override
    public Employee getEmployee() {
        return employee;
    }

    /*
     * (non-Javadoc)
     * 
     * @see eclipselink.example.jpa.employee.services.EditEmployee#setEmployee(
     * eclipselink.example.jpa.employee.model.Employee)
     */
    @Override
    public void setEmployee(Employee employee) {
        this.employee = employee;
        if (employee != null) {
            employee.getAddress();
            employee.getPhoneNumbers().size();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see eclipselink.example.jpa.employee.services.EditEmployee#isNew()
     */
    @Override
    public boolean isNew() {
        return getEmployee().getId() <= 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see eclipselink.example.jpa.employee.services.EditEmployee#save()
     */
    @Override
    public void save() {
        getEntityManager().getTransaction().begin();

        // Ensure the Employee's lock value is incremented
        getEntityManager().lock(getEmployee(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);

        getEntityManager().getTransaction().commit();
    }

    /*
     * (non-Javadoc)
     * 
     * @see eclipselink.example.jpa.employee.services.EditEmployee#delete()
     */
    @Override
    public void delete() {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().remove(getEmployee());
            getEntityManager().getTransaction().commit();
        } finally {
            setEmployee(null);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see eclipselink.example.jpa.employee.services.EditEmployee#refresh()
     */
    @Override
    public void refresh() {
        if (!getEntityManager().contains(getEmployee())) {
            setEmployee(getEntityManager().merge(getEmployee()));
        }
        getEntityManager().refresh(getEmployee());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eclipselink.example.jpa.employee.services.EditEmployee#updateVersion()
     */
    @Override
    public int updateVersion() {
        getEntityManager().detach(getEmployee());
        getEntityManager().getTransaction().begin();
        getEntityManager().createNativeQuery("UPDATE EMPLOYEE SET VERSION = VERSION + 1 WHERE EMP_ID = " + getEmployee().getId()).executeUpdate();
        getEntityManager().getTransaction().commit();
        setEmployee(getEntityManager().merge(getEmployee()));
        Number result = (Number) getEntityManager().createNativeQuery("SELECT VERSION FROM EMPLOYEE WHERE EMP_ID = " + getEmployee().getId()).getSingleResult();
        return result.intValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eclipselink.example.jpa.employee.services.EditEmployee#removeAddress()
     */
    @Override
    public String removeAddress() {
        getEmployee().setAddress(null);
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see eclipselink.example.jpa.employee.services.EditEmployee#addAddress()
     */
    @Override
    public String addAddress() {
        getEmployee().setAddress(new Address());
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eclipselink.example.jpa.employee.services.EditEmployee#addPhone(java.
     * lang.String)
     */
    @Override
    public PhoneNumber addPhone(String type) {
        if (type != null && !type.isEmpty()) {
            return getEmployee().addPhoneNumber(type, "", "");
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eclipselink.example.jpa.employee.services.EditEmployee#remove(eclipselink
     * .example.jpa.employee.model.PhoneNumber)
     */
    @Override
    public void remove(PhoneNumber phone) {
        getEmployee().removePhoneNumber(phone);
    }

    /*
     * (non-Javadoc)
     * 
     * @see eclipselink.example.jpa.employee.services.EditEmployee#close()
     */
    @Override
    public void close() {
        getEntityManager().close();
    }

}
