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
 *  dclarke - initial
 ******************************************************************************/
package eclipselink.example.jpa.employee.test.services;

import java.lang.reflect.Proxy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eclipselink.example.jpa.employee.model.Employee;
import eclipselink.example.jpa.employee.model.SamplePopulation;
import eclipselink.example.jpa.employee.services.Diagnostics;
import eclipselink.example.jpa.employee.services.Diagnostics.SQLTrace;
import eclipselink.example.jpa.employee.test.PersistenceTesting;

public class DiagnosticsTest {

    @Test
    public void verify() {
        EntityManager em = getEmf().createEntityManager();

        Server session = em.unwrap(Server.class);
        SessionLog log = session.getSessionLog();

        Assert.assertNotNull(log);
        Assert.assertTrue(Proxy.isProxyClass(log.getClass()));
        Assert.assertTrue(Proxy.getInvocationHandler(log) instanceof Diagnostics);

        em.close();
    }

    @Test
    public void singleFind() {
        Diagnostics diagnostics = Diagnostics.getInstance(getEmf());
        EntityManager em = getEmf().createEntityManager();

        SQLTrace start = diagnostics.start();
        Assert.assertEquals(0, start.getEntries().size());

        em.find(Employee.class, 1);
        SQLTrace trace = diagnostics.stop();

        Assert.assertSame(trace, start);
        Assert.assertNotNull(trace);
        Assert.assertEquals(1, trace.getEntries().size());

        em.close();
    }

    private static EntityManagerFactory emf;

    public static EntityManagerFactory getEmf() {
        return emf;
    }

    @BeforeClass
    public static void createEMF() {
        emf = PersistenceTesting.createEMF(true);

        EntityManager em = emf.createEntityManager();
        new SamplePopulation().createNewEmployees(em, 25);
        em.close();

        Diagnostics.getInstance(emf);
        emf.getCache().evictAll();
    }

    @AfterClass
    public static void closeEMF() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
        emf = null;
    }

}
