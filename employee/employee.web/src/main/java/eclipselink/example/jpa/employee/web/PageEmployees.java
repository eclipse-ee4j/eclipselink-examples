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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import eclipselink.example.jpa.employee.model.Employee;
import eclipselink.example.jpa.employee.services.EmployeePaging;

/**
 * Return list of available Leagues from JAX-RS call to MySports Admin app.
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
@ManagedBean
@SessionScoped
public class PageEmployees {

    private static final int PAGE_SIZE = 15;

    protected static final String PAGE = "/employee/stream?faces-redirect=true";

    /**
     * Current employees being shown
     */
    private List<Employee> employees;

    private EmployeePaging paging;

    private int size;

    private int currentPage = 1;

    private int numPages = 1;

    private EntityManagerFactory emf;

    public EntityManagerFactory getEmf() {
        return emf;
    }

    @PersistenceUnit(unitName = "employee")
    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EmployeePaging getPaging() {
        return paging;
    }

    public String initialize() {
        this.paging = new EmployeePaging(getEmf());

        this.currentPage = 1;
        this.employees = null;
        this.size = getPaging().size();
        this.numPages = this.size / PAGE_SIZE;
        if ((this.numPages * PAGE_SIZE) < this.size) {
            this.numPages++;
        }

        return null;
    }

    public List<Employee> getEmployees() {
        if (this.paging == null) {
            initialize();
        }
        if (this.employees == null) {
            int max = this.currentPage * PAGE_SIZE;
            int first = max - PAGE_SIZE;
            this.employees = getPaging().get(first, max);
        }
        return this.employees;
    }

    public int getSize() {
        return this.size;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getNumPages() {
        return numPages;
    }

    public String next() {
        if (getHasNext()) {
            this.currentPage++;
            this.employees = null;
        }
        return null;
    }

    public boolean getHasNext() {
        return this.currentPage < this.numPages;
    }

    public String previous() {
        if (getHasPrevious()) {
            this.currentPage--;
            this.employees = null;
        }
        return null;
    }

    public boolean getHasPrevious() {
        return this.currentPage > 1;
    }
}
