/*******************************************************************************
 * Copyright (c) 2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      gonural - JPARS Student Example 
 ******************************************************************************/
package eclipselink.example.jpars.student.web;

import java.util.ServiceLoader;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import javax.annotation.PreDestroy;
import org.eclipse.persistence.jpa.rs.PersistenceContextFactory;
import org.eclipse.persistence.jpa.rs.PersistenceContextFactoryProvider;

/**
 * Forces weaving of persistence units
 *
 */
@Startup
@Singleton
public class WeaverBean {
    @PersistenceUnit(unitName = "jpars_example_student")
    private EntityManagerFactory emf;

    /**
     * Clean up.
     */
    @PreDestroy
    public void preDestroy() {
        ServiceLoader<PersistenceContextFactoryProvider> persistenceContextFactoryProviderLoader =
                ServiceLoader.load(PersistenceContextFactoryProvider.class, Thread.currentThread().getContextClassLoader());

        for (PersistenceContextFactoryProvider persistenceContextFactoryProvider : persistenceContextFactoryProviderLoader) {
            PersistenceContextFactory persistenceContextFactory = persistenceContextFactoryProvider.getPersistenceContextFactory(null);
            if (persistenceContextFactory != null) {
                persistenceContextFactory.close();
            }
        }
    }
}
