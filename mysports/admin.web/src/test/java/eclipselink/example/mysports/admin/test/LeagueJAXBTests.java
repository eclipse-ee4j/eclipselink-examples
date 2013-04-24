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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eclipselink.example.mysports.admin.jaxrs.MySportsContextResolver;
import eclipselink.example.mysports.admin.model.HostedLeague;
import eclipselink.example.mysports.admin.services.test.AdminPersistenceTesting;

/**
 * 
 * @author dclarke
 * @since EclipseLink 2.3.0
 */
public class LeagueJAXBTests {

    private JAXBContext jaxbContext;

    private static EntityManagerFactory emf;

    @Test
    public void verifyCreateContext() throws Exception {
        JAXBContext jc = getJaxbContext();

        Assert.assertNotNull(jc);
    }

    @Test
    public void verifyMarshallHostedLeagues() throws Exception {
        EntityManager em = emf.createEntityManager();

        List<HostedLeague> leagues = em.createNamedQuery("HostedLeague.findAll", HostedLeague.class).getResultList();
        
        Marshaller marshaller =  getJaxbContext().createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        for (HostedLeague hl: leagues) {
            hl.setUri("TEST");
            marshaller.marshal(hl, System.out);
        }
    }

    private JAXBContext getJaxbContext() throws JAXBException {
        if (this.jaxbContext == null) {
            this.jaxbContext = MySportsContextResolver.createContext();
        }
        return this.jaxbContext;
    }

    @BeforeClass
    public static void createEMF() {
        emf = Persistence.createEntityManagerFactory("MySportsAdmin", AdminPersistenceTesting.get());
    }

    @AfterClass
    public static void closeEMF() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
        emf = null;
    }
}
