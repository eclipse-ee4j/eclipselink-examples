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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import eclipselink.example.jpa.employee.services.AdminBean;

/**
 * TODO
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
@ManagedBean
@RequestScoped
public class Admin {

    private AdminBean adminBean;

    private List<String> typeNames;

    private boolean displaySql = false;

    public AdminBean getAdminBean() {
        return adminBean;
    }

    @EJB
    public void setAdminBean(AdminBean adminBean) {
        this.adminBean = adminBean;
    }

    public String resetDatabase() {
        return getAdminBean().resetDatabase();
    }

    public String populateDatabase() {
        return getAdminBean().populateDatabase(25);
    }

    public List<String> getTypeNames() {
        if (this.typeNames == null) {
            this.typeNames = getAdminBean().getTypes();
        }
        return typeNames;
    }

    public boolean isDisplaySql() {
        return displaySql;
    }

    public void setDisplaySql(boolean displaySql) {
        this.displaySql = displaySql;
    }

    public void toggleSqlDisplay() {
        this.displaySql = !this.displaySql;
    }

    public String getToggleSqlDisplayButton() {
        return isDisplaySql() ? "Disable SQL Display" : "Enable SQL Display";
    }

    public String getCacheSize(String typeName) {
        int size = getAdminBean().getCacheSize(typeName);
        if (size < 0) {
            return "Error";
        }
        return Integer.toString(size);
    }

}
