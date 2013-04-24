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
package eclipselink.example.mysports.admin.services.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eclipselink.example.mysports.admin.model.Datasource;
import eclipselink.example.mysports.admin.model.GlassFish;
import eclipselink.example.mysports.admin.model.HostEnvironment;
import eclipselink.example.mysports.admin.services.AdminSchemaManager;
import eclipselink.example.mysports.admin.services.HostEnvironmentRepository;
import eclipselink.example.mysports.admin.services.HostEnvironmentRepositoryBean;

/**
 * Create initial {@link Datasource} instances.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
public class TestCreateHostEnvironments {

    private static EntityManagerFactory emf;

    private EntityManager entityManager;

    private HostEnvironmentRepositoryBean repository;

    public HostEnvironmentRepository getRepository() {
        return repository;
    }

    @Test
    public void createEnvironments() {
        HostEnvironment gf = new GlassFish();
        gf.setName("GlassFish@localhost");
        gf.setDescription("GlassFish 3.1.2 instance");

        getRepository().create(gf);
    }

    @Before
    public void setupRespoitory() {
        repository = new HostEnvironmentRepositoryBean();
        entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        repository.setEntityManager(entityManager);
    }

    @After
    public void completeTransaction() {
        if (this.entityManager != null && this.entityManager.isOpen()) {
            if (this.entityManager.getTransaction().isActive()) {
                this.entityManager.getTransaction().commit();
            }
            this.entityManager.close();
        }
        this.entityManager = null;
        this.repository = null;
    }

    @BeforeClass
    public static void createReposirory() {
        emf = Persistence.createEntityManagerFactory("MySportsAdmin", AdminPersistenceTesting.get());
        AdminSchemaManager.createTables(emf);

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM HostEnvironment").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterClass
    public static void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
        emf = null;
    }

}
