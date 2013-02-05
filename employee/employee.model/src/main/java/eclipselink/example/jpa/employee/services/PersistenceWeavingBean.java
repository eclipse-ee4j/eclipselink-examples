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

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 * This utility class will cause the container to weave the persistence unit.
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
public class PersistenceWeavingBean {

    @PersistenceUnit(unitName = "employee")
    private EntityManagerFactory emf;

}
