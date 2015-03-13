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
 *  dclarke - initial
 ******************************************************************************/
package eclipselink.example.jpa.employee.web;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import eclipselink.example.jpa.employee.services.AdminService;
import eclipselink.example.jpa.employee.services.persistence.SQLCapture.SQLTrace;

/**
 * TODO
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
@ManagedBean
@ApplicationScoped
public class Admin {

    private AdminService adminBean;

    private List<String> typeNames;

    boolean sqlTraceEnabled = true;

    public AdminService getAdminBean() {
        return adminBean;
    }

    @EJB
    public void setAdminBean(AdminService adminBean) {
        this.adminBean = adminBean;
    }

    public String resetDatabase() {
        getAdminBean().resetDatabase();
        return null;
    }

    public String populateDatabase() {
        getAdminBean().populateDatabase(25);
        return null;
    }

    public List<String> getTypeNames() {
        if (this.typeNames == null) {
            this.typeNames = getAdminBean().getTypes();
        }
        return typeNames;
    }

    public String getCacheSize(String typeName) {
        int size = getAdminBean().getCacheSize(typeName);
        if (size < 0) {
            return "Error";
        }
        return Integer.toString(size);
    }

    public boolean isSqlTraceEnabled() {
        return getAdminBean().getSqlCapture() != null;
    }

    public String getMessages() {
        SQLTrace trace = getAdminBean().getSqlCapture().getTrace(true);

        if (isSqlTraceEnabled() && trace != null) {
            // Truncate at 5 messages
            trace.truncate(5, "... SQL trace truncated");

            trace.getEntries().forEach(entry -> {
                FacesMessage msg = new FacesMessage(entry);
                FacesContext.getCurrentInstance().addMessage("SQL", msg);
            });
        }
        return null;
    }

}
