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

import eclipselink.example.jpa.employee.services.AdminBean;
import eclipselink.example.jpa.employee.services.diagnostics.Diagnostics;
import eclipselink.example.jpa.employee.services.diagnostics.Diagnostics.SQLTrace;

/**
 * TODO
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
@ManagedBean
@ApplicationScoped
public class Admin {

    private AdminBean adminBean;

    private List<String> typeNames;

    boolean sqlTraceEnabled = true;

    private Diagnostics diagnostics;

    public Diagnostics getDiagnostics() {
        return diagnostics;
    }

    @EJB
    public void setDiagnostics(Diagnostics diagnostics) {
        this.diagnostics = diagnostics;
    }

    public AdminBean getAdminBean() {
        return adminBean;
    }

    @EJB
    public void setAdminBean(AdminBean adminBean) {
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
        return getDiagnostics().isEnabled();
    }

    public String getMessages() {
        SQLTrace trace = getDiagnostics().getTrace(true);

        if (isSqlTraceEnabled() && trace != null) {
            // Truncate at 5 messages
            trace.truncate(5, "... SQL trace truncated");

            for (String entry : trace.getEntries()) {
                FacesContext.getCurrentInstance().addMessage("SQL", new FacesMessage(entry));
            }
        }
        return null;
    }

}
