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
package eclipselink.example.mysports.admin.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappingsWriter;
import org.junit.Test;

import eclipselink.example.mysports.admin.services.MappingsLoader;
import eclipselink.example.mysports.admin.services.test.AdminPersistenceTesting;

public class CreateOrmXML {

    @Test
    public void createEmpty() {
        XMLEntityMappings mappings = new XMLEntityMappings();
        mappings.setVersion("2.3");

        XMLEntityMappingsWriter.write(mappings, System.out);
    }

    @Test
    public void createFromDB() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("MySportsAdmin", AdminPersistenceTesting.get());
        EntityManager em = emf.createEntityManager();

        try {
            String xml = MappingsLoader.getORMapping(em, "OSL");
            System.out.println(xml);

            xml = MappingsLoader.getORMapping(em, "HTHL");
            System.out.println(xml);
        } finally {
            em.close();
            emf.close();
        }
    }

}
