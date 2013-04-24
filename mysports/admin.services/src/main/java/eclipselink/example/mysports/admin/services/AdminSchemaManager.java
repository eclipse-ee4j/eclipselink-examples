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
 *  dclarke - EclipseLink 2.4.2 - MySports Demo Bug 344608
 ******************************************************************************/
package eclipselink.example.mysports.admin.services;

import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.tools.schemaframework.DefaultTableGenerator;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.TableCreator;

public class AdminSchemaManager {

    public static void createTables(EntityManagerFactory emf) {
        Server session = JpaHelper.getServerSession(emf);
        SchemaManager sm = new SchemaManager(session);
        
        sm.replaceDefaultTables();
        sm.createSequences();
    }
    
    public static void dropTables(EntityManagerFactory emf) {
        Server session = JpaHelper.getServerSession(emf);
        SchemaManager sm = new SchemaManager(session);

        TableCreator tc = new DefaultTableGenerator(session.getProject(), true).generateDefaultTableCreator();
        tc.setIgnoreDatabaseException(true);
        tc.dropTables(session, sm, true);
    }
}
