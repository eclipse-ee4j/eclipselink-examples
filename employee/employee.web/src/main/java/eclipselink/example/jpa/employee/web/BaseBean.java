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

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.logging.SessionLogEntry;

import eclipselink.example.jpa.employee.services.Diagnostics;
import eclipselink.example.jpa.employee.services.Diagnostics.SQLTrace;

/**
 * Return list of available Leagues from JAX-RS call to MySports Admin app.
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
public abstract class BaseBean {

    private EntityManagerFactory emf;

    private Diagnostics diagnostics;
    
    public EntityManagerFactory getEmf() {
        return emf;
    }

    protected void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
        this.diagnostics = Diagnostics.getInstance(emf);
    }
    
    protected EntityManager createEntityManager() {
        EntityManager em = getEmf().createEntityManager();
        startSqlCapture();
        return em;
    }
    
    protected void close(EntityManager em) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        em.close();
        stopSqlCapture();
    }

    protected void startSqlCapture() {
        this.diagnostics.start();
    }

    protected void stopSqlCapture() {
        addMessages(this.diagnostics.stop());
    }

    /**
     * Add each SQL string to the messages TODO: Allow this to be
     * enabled/disabled
     */
    private void addMessages(SQLTrace sqlTrace) {
        for (SessionLogEntry entry : sqlTrace.getEntries()) {
            FacesContext.getCurrentInstance().addMessage("SQL", new FacesMessage(entry.getMessage()));
        }
    }

}
