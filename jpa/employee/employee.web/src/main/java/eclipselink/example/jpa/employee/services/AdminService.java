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

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.metamodel.EntityType;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.IdentityMapAccessor;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

import eclipselink.example.jpa.employee.model.SamplePopulation;
import eclipselink.example.jpa.employee.services.persistence.SQLCapture;
import eclipselink.example.jpa.employee.services.persistence.SQLCapture.SQLTrace;

/**
 * TODO
 * <p>
 * This session bean uses some internals of EclipseLink and is written to be
 * used in both JTA and RESOURCE_LOCAL. Its transaction behavior is determined
 * by {@link #isJTA()}
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
@Singleton
@Startup
public class AdminService {

    private EntityManagerFactory emf;

    private SQLCapture sqlCapture;

    public EntityManagerFactory getEmf() {
        return emf;
    }

    @PersistenceUnit(unitName = "employee")
    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
        this.sqlCapture = new SQLCapture(getServerSession());
    }

    public SQLCapture getSqlCapture() {
        return sqlCapture;
    }

    public void removeSqlCapture() {
        if (this.sqlCapture != null) {
            this.sqlCapture.remove();
            this.sqlCapture = null;
        }
    }

    public void resetDatabase() {
        Server session = getServerSession();

        SchemaManager sm = new SchemaManager(session);
        sm.replaceDefaultTables();
        sm.replaceSequences();

        session.getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void populateDatabase(int quantity) {
        EntityManager em = getEmf().createEntityManager();

        if (isJTA()) {
            em.joinTransaction();
        } else {
            em.getTransaction().begin();
        }

        new SamplePopulation().createNewEmployees(em, quantity);

        if (isJTA()) {
            em.flush();
        } else {
            em.getTransaction().commit();
        }
    }

    public int getCacheSize(String typeName) {
        Server session = getServerSession();

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

    /**
     * TODO
     * 
     * @return
     */
    private Server getServerSession() {
        EntityManager em = getEmf().createEntityManager();
        try {
            return em.unwrap(Server.class);
        } finally {
            em.close();
        }

    }

    private Boolean jta = null;

    private boolean isJTA() {
        if (jta == null) {
            jta = getServerSession().getServerPlatform().isJTAEnabled();
        }
        return jta;

    }

}
