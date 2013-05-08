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
import javax.persistence.OptimisticLockException;

import eclipselink.example.jpa.employee.model.Address;
import eclipselink.example.jpa.employee.model.Employee;
import eclipselink.example.jpa.employee.model.PhoneNumber;
import eclipselink.example.jpa.employee.services.Diagnostics;
import eclipselink.example.jpa.employee.services.Diagnostics.SQLTrace;
import eclipselink.example.jpa.employee.services.EmployeeRepository;

/**
 * Backing bean to edit or create an {@link Employee}.
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
@ManagedBean
@ViewScoped
public class EditEmployeeView {

    private EmployeeRepository repository;

    private Employee employee;

    /**
     * Value used to create new unique {@link PhoneNumber}
     */
    private String type;

    protected static final String PAGE = "/employee/edit";
    protected static final String PAGE_REDIRECT = "/employee/edit?faces-redirect=true";

    @PostConstruct
    private void init() {
        Flash flashScope = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        Integer id = (Integer) flashScope.get("employee-id");
        setEmployee(id);
    }

    @EJB
    public EmployeeRepository getRepository() {
        return repository;
    }

    public void setRepository(EmployeeRepository repository) {
        this.repository = repository;
    }

    public Employee getEmployee() {
        return this.employee;
    }

    public void setEmployee(int id) {
        getDiagnostics().start();
        this.employee = getRepository().find(id);

    }

    public String getEmployeeId() {
        if (getEmployee().getId() <= 0) {
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

    public Diagnostics getDiagnostics() {
        return getRepository().getDiagnostics();
    }

    /**
     * 
     * @return
     */
    public String save() {
        try {
            getDiagnostics().start();
            getRepository().save(getEmployee());
        } catch (OptimisticLockException e) {
            FacesContext.getCurrentInstance().addMessage("EclipseLink", new FacesMessage("OptimisticLockException: Could not save changes"));
        } finally {
            stopSqlCapture();
        }
        return null;
    }

    public String delete() {
        getDiagnostics().start();
        getRepository().delete(getEmployee());
        stopSqlCapture();
        return cancel();
    }

    public String refresh() {
        getDiagnostics().start();
        this.employee = getRepository().refresh(getEmployee());
        stopSqlCapture();
        return null;
    }

    public String cancel() {
        return "/index?faces-redirect=true";
    }

    /**
     * Force the optimistic version field to be updated so that the save
     * operations will fail.
     */
    public String updateVersion() {
        getDiagnostics().start();
        getRepository().updateVersion(getEmployee());
        stopSqlCapture();
        return null;
    }

    public Address getAddress() {
        return getEmployee().getAddress();
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
        PhoneNumber newPhone = getRepository().addPhone(getEmployee(), getType());
        if (newPhone == null) {
            FacesContext.getCurrentInstance().addMessage("input", new FacesMessage("Invalid type. Phone number could not be added"));
        }
        setType("");
        return null;
    }

    public String remove(PhoneNumber phone) {
        getEmployee().removePhoneNumber(phone);
        return null;
    }

    protected void stopSqlCapture() {
        addMessages(getRepository().getDiagnostics().stop());
    }

    private void addMessages(SQLTrace sqlTrace) {
        for (String entry : sqlTrace.getEntries()) {
            FacesContext.getCurrentInstance().addMessage("SQL", new FacesMessage(entry));
        }
    }

}
