/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      dclarke - initial 
 ******************************************************************************/
package eclipselink.example.jpa.employee.services;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.queries.CursoredStream;

import eclipselink.example.jpa.employee.model.Employee;

/**
 * Example of using an EclipseLink {@link CursoredStream} with a
 * {@link Stateful} session bean.
 * 
 * @since EclipseLink 2.4.2
 */
@Stateful
@LocalBean
public class EmployeeStream {

    private transient CursoredStream stream;

    /**
     * Last position remembered in case the bean is passivated and the
     * CursoredStream needs to be created again and reset to where it left off.
     */
    private int position = 0;

    private EntityManagerFactory emf;

    public EmployeeStream() {
    }

    public EntityManagerFactory getEmf() {
        return emf;
    }

    @PersistenceUnit(unitName = "employee")
    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @PostConstruct
    @PostActivate
    public void initialize() {
        EntityManager em = getEmf().createEntityManager();

        try {
            Query allEmpsQuery = em.createQuery("SELECT e FROM Employee e ORDER BY e.lastName,  e.firstName");
            allEmpsQuery.setHint(QueryHints.CURSOR, HintValues.TRUE);
            allEmpsQuery.setHint(QueryHints.CURSOR_INITIAL_SIZE, 10);
            allEmpsQuery.setHint(QueryHints.CURSOR_PAGE_SIZE, 10);
            // read ahead the position if this bean is being activated
            allEmpsQuery.setFirstResult(getPosition());

            this.stream = (CursoredStream) allEmpsQuery.getSingleResult();
        } finally {
            em.close();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Employee> next(int size) {
        if (this.stream == null) {
            throw new IllegalStateException("EmployeeStream not initialized");
        }

        List<Employee> results = this.stream.nextElements(size);
        position += results.size();
        return results;
    }

    public boolean hasNext() {
        if (this.stream == null) {
            throw new IllegalStateException("EmployeeStream not initialized");
        }
        return this.stream.hasNext();
    }

    public int size() {
        if (this.stream == null) {
            throw new IllegalStateException("EmployeeStream not initialized");
        }
        return this.stream.size();
    }

    public int getPosition() {
        return position;
    }

    @PrePassivate
    @Remove
    public void close() {
        if (this.stream != null) {
            this.stream.close();
            this.stream = null;
        }
    }

}
