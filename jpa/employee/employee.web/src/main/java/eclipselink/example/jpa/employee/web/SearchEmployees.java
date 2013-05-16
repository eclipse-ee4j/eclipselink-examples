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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.inject.Inject;

import eclipselink.example.jpa.employee.services.EmployeeCriteria;

/**
 * TODO
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
@ManagedBean
@ViewScoped
public class SearchEmployees {

    /**
     * Flash property name used to pass the criteria through to the results
     * rendering page.
     */
    protected final static String CRITERIA = "CRITERIA";

    @Inject
    private EmployeeCriteria criteria = new EmployeeCriteria(10);
    
    public EmployeeCriteria getCriteria() {
        return criteria;
    }

    public void setCriteria(EmployeeCriteria criteria) {
        this.criteria = criteria;
    }

    public String search() {
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        flash.put(CRITERIA, getCriteria());
        return EmployeeResults.PAGE;
    }

}
