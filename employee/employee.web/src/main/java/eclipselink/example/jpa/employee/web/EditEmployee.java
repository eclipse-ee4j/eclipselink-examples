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
package eclipselink.example.jpa.employee.web;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceUnit;
import javax.persistence.RollbackException;

import eclipselink.example.jpa.employee.model.Address;
import eclipselink.example.jpa.employee.model.Employee;

/**
 * Return list of available Leagues from JAX-RS call to MySports Admin app.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
@ManagedBean
@ViewScoped
public class EditEmployee extends BaseBean {

    private Employee employee;

    protected static final String PAGE = "/employee/edit";
    protected static final String PAGE_REDIRECT = "/employee/edit?faces-redirect=true";

    @PostConstruct
    private void init() {
        Flash flashScope = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        this.employee = (Employee) flashScope.get("employee");

        refresh();
    }

    public Employee getEmployee() {
        return employee;
    }

    @PersistenceUnit(unitName = "employee")
    public void setEmf(EntityManagerFactory emf) {
        super.setEmf(emf);
    }

    public String save() {
        EntityManager em = createEntityManager();

        try {
            em.getTransaction().begin();
            this.employee = em.merge(getEmployee());
            em.getTransaction().commit();
        } catch (RollbackException e) {
            if (e.getCause() instanceof OptimisticLockException) {
                FacesContext.getCurrentInstance().addMessage("OptimisticLockException", new FacesMessage("Commit Failed: Optimistic Lock Exception."));
            } else {
                throw e;
            }
        } finally {
            close(em);
        }
        return null;
    }

    public String refresh() {
        EntityManager em = createEntityManager();

        try {
            this.employee = em.find(Employee.class, getEmployee().getId());
            em.refresh(getEmployee());
            getEmployee().getAddress();
            getEmployee().getPhoneNumbers().size();
        } finally {
            close(em);
        }
        return null;
    }

    public String cancel() {
        return StreamEmployees.PAGE;
    }

    /**
     * Force the optimistic version field to be updated so that the save
     * operations will fail.
     */
    public String updateVersion() {
        EntityManager em = createEntityManager();
        int newVersion = -1;

        try {
            em.getTransaction().begin();
            em.createNativeQuery("UPDATE EMPLOYEE SET VERSION = VERSION + 1 WHERE EMP_ID = " + getEmployee().getId()).executeUpdate();
            em.getTransaction().commit();

            Number result = (Number) em.createNativeQuery("SELECT VERSION FROM EMPLOYEE WHERE EMP_ID = " + getEmployee().getId()).getSingleResult();
            newVersion = result.intValue();
        } finally {
            close(em);
        }

        FacesContext.getCurrentInstance().addMessage("Update version", new FacesMessage("DATABASE EMPLOYEE ID: " + getEmployee().getId() + " VERSION= " + newVersion));

        return null;
    }

    public String removeAddress() {
        getEmployee().setAddress(null);
        return null;
    }

    public String addAddress() {
        getEmployee().setAddress(new Address());
        return null;
    }

    public String addPhone() {
        getEmployee().addPhoneNumber("", "", "");
        return null;
    }

}
