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
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import eclipselink.example.jpa.employee.model.Employee;

/**
 * Return list of available Leagues from JAX-RS call to MySports Admin app.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
@ManagedBean
@SessionScoped
public class DeleteEmployee extends BaseBean {

    private Employee employee;

    protected static final String PAGE = "/employee/delete?faces-redirect=true";

    @PostConstruct
    private void init() {
        Flash flashScope = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        this.employee = (Employee) flashScope.get("employee");
    }

    public Employee getEmployee() {
        return employee;
    }

    @PersistenceUnit(unitName = "employee")
    public void setEmf(EntityManagerFactory emf) {
        super.setEmf(emf);
    }

    public String delete() {
        EntityManager em = createEntityManager();
        try {
            this.employee = em.find(Employee.class, getEmployee().getId());
            // TODO: Handle find failure
            em.getTransaction().begin();
            em.remove(getEmployee());
            em.getTransaction().commit();

        } finally {
            close(em);
            this.employee = null;
        }
        return null;
    }

    public boolean getisDeleted() {
        return this.employee == null;
    }

    public String cancel() {
        return StreamEmployees.PAGE;
    }
}
