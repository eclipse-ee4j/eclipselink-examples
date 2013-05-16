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
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;

import eclipselink.example.jpa.employee.model.Address;
import eclipselink.example.jpa.employee.model.Employee;
import eclipselink.example.jpa.employee.model.PhoneNumber;
import eclipselink.example.jpa.employee.services.EmployeeRepository;

/**
 * Backing bean to edit or create an {@link Employee}.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
@ManagedBean
@ViewScoped
public class EditEmployee {

    private Employee employee;

    private String type;

    boolean create = false;

    private EmployeeRepository repository;

    public EmployeeRepository getRepository() {
        return repository;
    }

    @EJB
    public void setRepository(EmployeeRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    private void init() {
        Flash flashScope = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        this.employee = (Employee) flashScope.get("employee");

        if (this.employee == null) {
            this.employee = new Employee();
        }
    }

    public Employee getEmployee() {
        return this.employee;
    }

    public String getEmployeeId() {
        if (getEmployee() == null || getEmployee().getId() <= 0) {
            return "None Assigned";
        }
        return Integer.toString(getEmployee().getId());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isCreate() {
        return getEmployee() != null && getEmployee().getId() <= 0;
    }

    /**
     * 
     * @return
     */
    public String save() {
        Employee emp = getRepository().save(getEmployee());
        if (emp == null) {
            FacesContext.getCurrentInstance().addMessage("OptimisticLockException", new FacesMessage("Commit Failed: Lock Exception or Entity Deleted."));
        } else {
            this.employee = emp;
        }

        return null;
    }

    public String delete() {
        Flash flashScope = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        flashScope.put("employee", getEmployee());

        return Navigation.DELETE;
    }

    public String refresh() {
        this.employee = getRepository().refresh(getEmployee());
        if (this.employee == null) {
            return cancel();
        }

        return null;
    }

    public String cancel() {
        return Navigation.INDEX_REDIRECT;
    }

    /**
     * Force the optimistic version field to be updated so that the save
     * operations will fail.
     */
    public String updateVersion() {
        int newVersion = getRepository().updateVersion(getEmployee());

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
        if (getType() != null && !getType().isEmpty()) {
            getEmployee().addPhoneNumber(getType(), "", "");
        } else {
            FacesContext.getCurrentInstance().addMessage("input", new FacesMessage("Invalid type. Phone number could not be added"));
        }
        setType("");
        return null;
    }

    public String remove(PhoneNumber phone) {
        getEmployee().removePhoneNumber(phone);
        return null;
    }
    
}
