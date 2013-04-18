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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.criteria.CriteriaQuery;

import eclipselink.example.jpa.employee.model.Employee;
import eclipselink.example.jpa.employee.services.EmployeeCriteria;
import eclipselink.example.jpa.employee.services.EntityPaging;

/**
 * Backing bean to manage search results for an Employee query. The results can
 * either be paged or displayed in a single scrolling list.
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
@ManagedBean
@ViewScoped
public class EmployeeResults extends BaseBean {

    protected static final String PAGE = "/employee/results?faces-redirect=true";

    /**
     * Current employees being shown
     */
    private List<Employee> employees;

    private EntityPaging<Employee> paging;

    private int currentPage = 1;

    @PersistenceUnit(unitName = "employee")
    public void setEmf(EntityManagerFactory emf) {
        super.setEmf(emf);
    }

    public EntityPaging<Employee> getPaging() {
        return this.paging;
    }

    @PostConstruct
    public void initialize() {
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        EmployeeCriteria criteria = (EmployeeCriteria) flash.get(SearchEmployees.CRITERIA);

        this.currentPage = 1;
        this.employees = null;

        this.paging = criteria.getPaging(getEmf());

        if (!hasPaging()) {
            EntityManager em = createEntityManager();
            try {
                startSqlCapture();
                CriteriaQuery<Employee> cq = criteria.createQuery(getEmf());
                this.employees = em.createQuery(cq).getResultList();
            } finally {
                close(em);
            }
        }
    }

    public List<Employee> getEmployees() {
        if (this.employees == null && hasPaging()) {
            startSqlCapture();
            this.employees = getPaging().get(this.currentPage);
            this.stopSqlCapture();
        }
        return this.employees;
    }

    public boolean hasPaging() {
        return this.paging != null;
    }

    public int getSize() {
        if (hasPaging()) {
            return this.paging.size();
        }
        return getEmployees().size();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getNumPages() {
        if (hasPaging()) {
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

        return EditEmployee.PAGE;
    }

}
