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

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;

import eclipselink.example.jpa.employee.model.Employee;
import eclipselink.example.jpa.employee.services.EmployeeRepository;
import eclipselink.example.jpa.employee.services.paging.EntityPaging;

/**
 * Backing bean to manage search results for an Employee query. The results can
 * either be paged or displayed in a single scrolling list.
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
@ManagedBean
@ViewScoped
public class EmployeeResults {

    protected static final String PAGE = "/employee/results?faces-redirect=true";

    @ManagedProperty("#{searchEmployees}")
    private SearchEmployees search;
    
    private EmployeeRepository repository;

    /**
     * Current employees being shown
     */
    private List<Employee> employees;

    private EntityPaging<Employee> paging;

    private int currentPage = 1;

    public EmployeeRepository getRepository() {
        return repository;
    }

    @EJB
    public void setRepository(EmployeeRepository repository) {
        this.repository = repository;
    }

    public SearchEmployees getSearch() {
        return search;
    }

    public void setSearch(SearchEmployees search) {
        this.search = search;
    }

    public EntityPaging<Employee> getPaging() {
        return this.paging;
    }

    @PostConstruct
    public void initialize() {
        this.currentPage = 1;
        this.employees = null;

        this.paging = getRepository().getPaging(getSearch().getCriteria());
    }

    public List<Employee> getEmployees() {
        if (this.employees == null) {
            if (getHasPaging()) {
                this.employees = getPaging().get(this.currentPage);
            } else {
                this.employees = getRepository().getEmployees(getSearch().getCriteria());
            }
        }

        // Enforce 10 result max
        if (this.employees.size() > 10) {
            FacesContext.getCurrentInstance().addMessage("Max Results", new FacesMessage("Found " + employees.size() + " Employees. Try Advanced Search with pagination"));
            this.employees = this.employees.subList(0, 10);
        }
        return this.employees;
    }

    public boolean getHasPaging() {
        return this.paging != null;
    }

    public int getSize() {
        if (getHasPaging()) {
            return this.paging.size();
        }
        return getEmployees().size();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getNumPages() {
        if (getHasPaging()) {
            return this.paging.getNumPages();
        }
        return 1;
    }

    public String next() {
        if (getHasNext()) {
            this.currentPage++;
            this.employees = null;
        }
        return null;
    }

    public boolean getHasNext() {
        return this.currentPage < getNumPages();
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

    public String edit(Employee employee) {
        Flash flashScope = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        flashScope.put("employee", employee);

        return Navigation.EDIT;
    }

    public String delete(Employee employee) {
        Flash flashScope = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        flashScope.put("employee", employee);

        return Navigation.DELETE;
    }
}
