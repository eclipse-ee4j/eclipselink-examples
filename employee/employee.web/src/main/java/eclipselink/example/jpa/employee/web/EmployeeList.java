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

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import model.Employee;

/**
 * Return list of available Leagues from JAX-RS call to MySports Admin app.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
@ManagedBean
public class EmployeeList {
    
    private EntityManagerFactory emf;

    protected static final String PAGE = "/employee/search-results?faces-redirect=true";

    public EntityManagerFactory getEmf() {
        return emf;
    }

    @PersistenceUnit(unitName="employee")
    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public List<Employee> getEmployees() {
        EntityManager em = getEmf().createEntityManager();
        try {
            return em.createNamedQuery("Employee.findAll", Employee.class).getResultList();
        } finally {
            em.close();
        }
    }
    
    public String create() {
        return CreateEmployee.PAGE;
    }
}
