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
package eclipselink.example.jpa.employee.services;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.metamodel.EntityType;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.IdentityMapAccessor;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

import eclipselink.example.jpa.employee.model.Employee;
import eclipselink.example.jpa.employee.model.SamplePopulation;

/**
 * Edit service for an {@link Employee} instance.
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
@Stateless
@LocalBean
public class AdminBean {

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

    public String populateDatabase(int quantity) {
        EntityManager em = getEmf().createEntityManager();

        try {
            em.getTransaction().begin();
            new SamplePopulation().createNewEmployees(em, quantity);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return null;
    }

    public int getCacheSize(String typeName) {
        EntityManager em = getEmf().createEntityManager();
        Server session = em.unwrap(Server.class);
        ClassDescriptor descriptor = session.getDescriptorForAlias(typeName);
        if (descriptor != null) {
            return ((IdentityMapAccessor) session.getIdentityMapAccessor()).getIdentityMap(descriptor.getJavaClass()).getSize();
        } else {
            return -1;
        }
    }

    public int getDatabaseCount(String type) {
        EntityManager em = getEmf().createEntityManager();

        try {
            return em.createQuery("SELECT COUNT(o) FROM " + type + " o", Number.class).getSingleResult().intValue();
        } finally {
            em.close();
        }
    }

    /**
     * Construct a list of valid type names based on the JAP metamodel. Only
     * include types that are inheritance roots (no super type).
     */
    public List<String> getTypes() {
        List<String> typeNames = new ArrayList<String>();
        for (EntityType<?> type : getEmf().getMetamodel().getEntities()) {
            if (type.getSupertype() == null) {
                typeNames.add(type.getName());
            }
        }
        return typeNames;
    }

}
