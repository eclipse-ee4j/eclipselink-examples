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

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import eclipselink.example.jpa.employee.persistence.SQLCaptureSessionLog;
import eclipselink.example.jpa.employee.persistence.SQLCaptureSessionLog.SessionLogHandler;
import eclipselink.example.jpa.employee.persistence.SQLTrace;

/**
 * This helper bean will cause the container to weave the persistence unit
 * through its injection at application startup. It will also server as an
 * interface to get the SQL trace for a given application operation.
 * <p>
 * Since this example uses the persistence unit through the application
 * bootstrap API the container will not instrument/weave the entity classes.
 * This class is ONLY required in the application to force the weaving to occur
 * and the {@link EntityManagerFactory} within here is never used.
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
@Startup
@Singleton
@LocalBean
public class PersistenceHelper {

    @PersistenceUnit(unitName = "employee")
    private EntityManagerFactory emf;

    private SessionLogHandler handler;
    
    public EntityManagerFactory getEmf() {
        return emf;
    }

    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    protected SessionLogHandler getHandler() {
        if (handler == null) {
            EntityManager em = getEmf().createEntityManager();
            handler = SQLCaptureSessionLog.getHandler(em);
            em.close();
        }
        return handler;
    }

    public SQLTrace startSQLTrace() {
        return getHandler().start();
    }

    public SQLTrace endSQLTrace() {
        return getHandler().stop();
    }

}
