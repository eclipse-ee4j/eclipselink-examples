/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      ssmith - Bug 371099: Native ORM Byte Code Weaving Example
 ******************************************************************************/
package example;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

public class CreateDatabase {

    public static void main(String[] args) {
        DatabaseSession session = (DatabaseSession) SessionManager.getManager().getSession("employee");
        SchemaManager schemaManager = new SchemaManager(session);
        schemaManager.replaceDefaultTables();
        schemaManager.createSequences();
        SessionManager.getManager().destroyAllSessions();
    }

}
