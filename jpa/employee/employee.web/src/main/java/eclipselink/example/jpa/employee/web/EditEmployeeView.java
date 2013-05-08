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
import javax.persistence.RollbackException;

import org.eclipse.persistence.logging.SessionLogEntry;

import eclipselink.example.jpa.employee.model.Address;
import eclipselink.example.jpa.employee.model.Employee;
import eclipselink.example.jpa.employee.model.PhoneNumber;
import eclipselink.example.jpa.employee.services.Diagnostics;
import eclipselink.example.jpa.employee.services.Diagnostics.SQLTrace;
import eclipselink.example.jpa.employee.services.EditEmployeeBean;

/**
 * Backing bean to edit or create an {@link Employee}.
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
@ManagedBean
@ViewScoped
public class EditEmployeeView {

    private EditEmployeeBean edit;

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

    public EditEmployeeBean getEdit() {
        return edit;
    }

    @EJB
    public void setEdit(EditEmployeeBean edit) {
        this.edit = edit;
    }

    public Employee getEmployee() {
        return getEdit().getEmployee();
    }

    public void setEmployee(int id) {
        getDiagnostics().start();
        this.edit.setEmployee(id);
        addMessages(getDiagnostics().stop());
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
        return getEdit().isNew();
    }

    public Diagnostics getDiagnostics() {
        return getEdit().getDiagnostics();
    }

    /**
     * 
     * @return
     */
    public String save() {
        try {
            getDiagnostics().start();
            getEdit().save();
        } catch (RollbackException e) {
            if (e.getCause() instanceof OptimisticLockException) {
                FacesContext.getCurrentInstance().addMessage("EclipseLink", new FacesMessage("OptimisticLockException: Could not save changes"));
            } else {
                throw e;
            }
        } finally {
            addMessages(getDiagnostics().stop());
        }
        return null;
    }

    public String delete() {
        getDiagnostics().start();
        getEdit().delete();
        addMessages(getDiagnostics().stop());
        return cancel();
    }

    public String refresh() {
        getDiagnostics().start();
        getEdit().refresh();
        addMessages(getDiagnostics().stop());
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
        getEdit().updateVersion();
        addMessages(getDiagnostics().stop());
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
        PhoneNumber newPhone = getEdit().addPhone(getType());
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

    /**
     * Add each SQL string to the messages TODO: Allow this to be
     * enabled/disabled
     */
    private void addMessages(SQLTrace sqlTrace) {
        for (SessionLogEntry entry : sqlTrace.getEntries()) {
            FacesContext.getCurrentInstance().addMessage("SQL", new FacesMessage(entry.getMessage()));
        }
    }

}
