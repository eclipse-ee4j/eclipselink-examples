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
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;

import eclipselink.example.jpa.employee.model.Employee;
import eclipselink.example.jpa.employee.services.IdInPaging;

/**
 * Return list of available Leagues from JAX-RS call to MySports Admin app.
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
@ManagedBean
@SessionScoped
public class PageIdInEmployees extends BaseBean {

    private static final int PAGE_SIZE = 10;

    protected static final String PAGE = "/employee/stream?faces-redirect=true";

    /**
     * Current employees being shown
     */
    private List<Employee> employees;

    private IdInPaging paging;

    private int currentPage = 1;

    @PersistenceUnit(unitName = "employee")
    public void setEmf(EntityManagerFactory emf) {
        super.setEmf(emf);
    }

    public IdInPaging getPaging() {
        return paging;
    }

    public String initialize() {
        EntityManager em = getEmf().createEntityManager();

        try {
            startSqlCapture();

            TypedQuery<Number> idQuery = em.createQuery("SELECT e.id FROM Employee e ORDER BY e.id", Number.class);

            this.paging = new IdInPaging(getEmf(), idQuery, PAGE_SIZE);

            this.currentPage = 1;
            this.paging.size();
            stopSqlCapture();

            this.employees = getEmployees();

        } finally {
            em.close();
        }
        return null;
    }

    public List<Employee> getEmployees() {
        if (this.paging == null) {
            initialize();
        }
        if (this.employees == null) {
            startSqlCapture();
            this.employees = getPaging().get(this.currentPage);
            this.stopSqlCapture();
        }
        return this.employees;
    }

    public int getSize() {
        return getPaging().size();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getNumPages() {
        return getPaging().getNumPages();
    }

    public String next() {
        if (getHasNext()) {
            this.currentPage++;
            this.employees = null;
            getEmployees();
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
            getEmployees();
        }
        return null;
    }

    public boolean getHasPrevious() {
        return this.currentPage > 1;
    }

    public String edit(Employee employee) {
        Flash flashScope = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        flashScope.put("employee", employee);

        return EditEmployee.PAGE_REDIRECT;
    }

    public String delete(Employee employee) {
        Flash flashScope = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        flashScope.put("employee", employee);

        return DeleteEmployee.PAGE;
    }

}
