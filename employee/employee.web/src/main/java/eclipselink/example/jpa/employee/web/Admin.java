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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

import eclipselink.example.jpa.employee.model.SamplePopulation;

/**
 * TODO
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
@ManagedBean
@RequestScoped
public class Admin {

    private EntityManagerFactory emf;

    public EntityManagerFactory getEmf() {
        return emf;
    }

    @PersistenceUnit(unitName = "employee")
    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public String resetDatabase() {
        EntityManager em = getEmf().createEntityManager();

        try {
            SchemaManager sm = new SchemaManager(em.unwrap(Server.class));
            sm.replaceDefaultTables();
            sm.replaceSequences();

            em.unwrap(Server.class).getIdentityMapAccessor().initializeAllIdentityMaps();
        } finally {
            em.close();
        }
        return null;
    }

    public String populateDatabase() {
        EntityManager em = getEmf().createEntityManager();

        try {
            new SamplePopulation().createNewEmployees(em, 25);
        } finally {
            em.close();
        }
        return null;
    }

}
