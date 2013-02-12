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
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;

import eclipselink.example.jpa.employee.model.Employee;
import eclipselink.example.jpa.employee.services.StreamPaging;

/**
 * Return list of available Leagues from JAX-RS call to MySports Admin app.
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
@ManagedBean
@ViewScoped
public class StreamEmployees extends BaseBean {

    private static final int PAGE_SIZE = 10;

    protected static final String PAGE = "/employee/stream?faces-redirect=true";

    private List<Employee> employees;

    private StreamPaging<Employee> stream;

    @PersistenceUnit(unitName = "employee")
    public void setEmf(EntityManagerFactory emf) {
        super.setEmf(emf);
    }

    public StreamPaging<Employee> getStream() {
        return stream;
    }

    protected void initialize() {
        EntityManager em = getEmf().createEntityManager();

        try {
            TypedQuery<Employee> query = em.createQuery("SELECT e FROM Employee e ORDER BY e.id", Employee.class);
            this.stream = new StreamPaging<Employee>(query, PAGE_SIZE);
            this.stream.size();
        } finally {
            em.close();
        }
        this.employees = null;
    }

    public String reset() {
        startSqlCapture();
        initialize();
        stopSqlCapture();
        return null;
    }

    public List<Employee> getEmployees() {
        startSqlCapture();
        if (this.stream == null) {
            initialize();
        }
        if (this.employees == null) {
            this.employees = getStream().next();
        }
        stopSqlCapture();
        return this.employees;
    }

    public int getSize() {
        if (this.stream == null) {
            initialize();
        }
        return getStream().size();
    }

    public int getCurrentPage() {
        return 1;
    }

    public int getNumPages() {
        if (this.stream == null) {
            initialize();
        }
        return getStream().getNumPages();
    }

    public String next() {
        if (getHasNext()) {
            this.employees = getStream().next();
        }
        return null;
    }

    public boolean getHasNext() {
        if (this.stream == null) {
            initialize();
        }
        return getStream().hasNext();
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
