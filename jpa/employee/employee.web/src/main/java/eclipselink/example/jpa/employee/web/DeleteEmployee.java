/*******************************************************************************
 * Copyright (c) 2013 Oracle. All rights reserved.
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

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;

import eclipselink.example.jpa.employee.model.Employee;
import eclipselink.example.jpa.employee.services.EmployeeRepository;

/**
 * Backing bean to confirm deletion of an {@link Employee}.
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
@ManagedBean
@ViewScoped
public class DeleteEmployee {

    private Employee employee;

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
    }

    public Employee getEmployee() {
        return this.employee;
    }

    public String confirm() {
        this.employee = getRepository().delete(getEmployee());
        return null;
    }

    public String cancel() {
        return Navigation.INDEX_REDIRECT;
    }
}
