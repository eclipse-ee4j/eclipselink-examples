/*******************************************************************************
 * Copyright (c) 2011-2012 Oracle. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 and Eclipse Distribution License v. 1.0 which accompanies
 * this distribution. The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html and the Eclipse Distribution
 * License is available at http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors: dclarke - Bug 361016: Future Versions Examples
 ******************************************************************************/
package tests.internal;

import static example.PersonModelExample.T1;
import static temporal.Effectivity.*;
import static example.PersonModelExample.T3;
import static example.PersonModelExample.T4;
import static example.PersonModelExample.T5;
import static example.PersonModelExample.T6;
import static example.PersonModelExample.T7;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import model.PersonHobby;

import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.Test;

import temporal.TemporalEntityManager;
import temporal.TemporalHelper;
import tests.BaseTestCase;

/**
 * Verify the helper methods on {@link TemporalHelper}
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class TemporalHelperTests extends BaseTestCase {

    private void verifySetStartTime(TemporalEntityManager em, Long value) {
        assertTrue(em.hasEffectiveTime());
        assertNotNull(em.getEffectiveTime());
        assertEquals(value, em.getEffectiveTime());

        RepeatableWriteUnitOfWork uow = em.unwrap(RepeatableWriteUnitOfWork.class);
        assertNotNull(uow);

        ClientSession clientSession = (ClientSession) uow.getParent();

        DatabaseSession session = em.unwrap(DatabaseSession.class);
        assertNotSame(clientSession, session);

        Server serverSession = em.unwrap(Server.class);
        assertNotSame(clientSession, serverSession);
        assertSame(session, serverSession);
    }

    @Test
    public void verifySetStartTime() {
        TemporalEntityManager em = getEntityManager();

        assertFalse(em.hasEffectiveTime());
        assertNull(em.getEffectiveTime());

        em.setEffectiveTime(T1);

        verifySetStartTime(em, T1);
    }

    @Test
    public void verifyClearStartTime() {
        TemporalEntityManager em = getEntityManager();
        assertFalse(em.hasEffectiveTime());
        assertNull(em.getEffectiveTime());

        em.setEffectiveTime(T3);

        verifySetStartTime(em, T3);

        em.clearEffectiveTime();

        assertFalse(em.hasEffectiveTime());
        assertNull(em.getEffectiveTime());
    }

    @Test
    public void verifyConcurrentSetStartTime() {
        TemporalEntityManager em1 = getEntityManager();
        TemporalEntityManager em2 = TemporalEntityManager.getInstance(getEMF().createEntityManager());

        assertNotSame(em1, em2);

        em1.setEffectiveTime(T4);

        em2.setEffectiveTime(T5);

        verifySetStartTime(em2, T5);
        verifySetStartTime(em1, T4);
    }

    @Test
    public void verifyConcurrentClearStartTime() {
        TemporalEntityManager em1 = getEntityManager();
        TemporalEntityManager em2 = TemporalEntityManager.getInstance(getEMF().createEntityManager());

        assertNotSame(em1, em2);

        em1.setEffectiveTime(T6);
        em2.setEffectiveTime(T7);

        verifySetStartTime(em2, T7);
        verifySetStartTime(em1, T6);

        em1.clearEffectiveTime();
        verifySetStartTime(em2, T7);
        assertFalse(em1.hasEffectiveTime());
        assertNull(em1.getEffectiveTime());

        em2.clearEffectiveTime();
        assertFalse(em2.hasEffectiveTime());
        assertNull(em2.getEffectiveTime());
    }

    @Test
    public void verifyCurrentCreateTemporal() {
        TemporalEntityManager em = getEntityManager();
        em.getTransaction().begin();

        PersonHobby ph = em.newTemporal(PersonHobby.class);

        Assert.assertNotNull(ph);
        Assert.assertNotNull(ph.getEffectivity());
        Assert.assertEquals(BOT, ph.getEffectivity().getStart());
        Assert.assertEquals(EOT, ph.getEffectivity().getEnd());
    }

    @Test
    public void verifyFutureCreateTemporal() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime(T3, true);
        em.getTransaction().begin();

        PersonHobby ph = em.newTemporal(PersonHobby.class);

        Assert.assertNotNull(ph);
        Assert.assertNotNull(ph.getEffectivity());
        Assert.assertEquals(T3, ph.getEffectivity().getStart());
        Assert.assertEquals(EOT, ph.getEffectivity().getEnd());
    }
}
