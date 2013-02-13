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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import eclipselink.example.jpa.employee.model.Employee;

/**
 * TODO
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
@ManagedBean
@ViewScoped
public class SearchEmployees extends BaseBean {

    private String firstName = "%";

    private String lastName = "%";
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String search() {
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        flash.put("", "");
        return EmployeeResults.PAGE;
    }

}
