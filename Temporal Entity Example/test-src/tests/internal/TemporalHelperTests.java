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

import static example.PersonModelExample.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.Test;

import temporal.TemporalHelper;
import tests.BaseTestCase;

/**
 * Verify the helper methods on {@link TemporalHelper}
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class TemporalHelperTests extends BaseTestCase {

    private void verifySetStartTime(EntityManager em, Long value) {
        assertTrue(em.getProperties().containsKey(TemporalHelper.EFF_TS_PROPERTY));
        assertTrue(TemporalHelper.hasEffectiveTime(em));
        assertNotNull(TemporalHelper.getEffectiveTime(em));
        assertEquals(value, TemporalHelper.getEffectiveTime(em));

        RepeatableWriteUnitOfWork uow = em.unwrap(RepeatableWriteUnitOfWork.class);
        assertNotNull(uow);
        assertFalse(uow.getProperties().containsKey(TemporalHelper.EFF_TS_PROPERTY));

        ClientSession clientSession = (ClientSession) uow.getParent();
        assertTrue(clientSession.getProperties().containsKey(TemporalHelper.EFF_TS_PROPERTY));

        DatabaseSession session = em.unwrap(DatabaseSession.class);
        assertTrue(clientSession.getProperties().containsKey(TemporalHelper.EFF_TS_PROPERTY));
        assertNotSame(clientSession, session);

        Server serverSession = em.unwrap(Server.class);
        assertFalse(serverSession.getProperties().containsKey(TemporalHelper.EFF_TS_PROPERTY));
        assertNotSame(clientSession, serverSession);
        assertSame(session, serverSession);
    }

    @Test
    public void verifySetStartTime() {
        EntityManager em = createEntityManager();

        assertFalse(TemporalHelper.hasEffectiveTime(em));
        assertNull(TemporalHelper.getEffectiveTime(em));

        TemporalHelper.setEffectiveTime(em, T1);

        verifySetStartTime(em, T1);
    }

    @Test
    public void verifyCreateEMWithStartTime() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(TemporalHelper.EFF_TS_PROPERTY, T2);
        EntityManager em = createEntityManager(properties);

        verifySetStartTime(em, T2);
    }

    @Test
    public void verifyClearStartTime() {
        EntityManager em = createEntityManager();
        assertFalse(TemporalHelper.hasEffectiveTime(em));
        assertNull(TemporalHelper.getEffectiveTime(em));

        TemporalHelper.setEffectiveTime(em, T3);

        verifySetStartTime(em, T3);

        TemporalHelper.clearEffectiveTime(em);

        assertFalse(em.getProperties().containsKey(TemporalHelper.EFF_TS_PROPERTY));
        assertFalse(TemporalHelper.hasEffectiveTime(em));
        assertNull(TemporalHelper.getEffectiveTime(em));
    }

    @Test
    public void verifyConcurrentSetStartTime() {
        EntityManager em1 = createEntityManager();
        EntityManager em2 = getEMF().createEntityManager();
        
        assertNotSame(em1, em2);
        
        TemporalHelper.setEffectiveTime(em1, T4);
        
        TemporalHelper.setEffectiveTime(em2, T5);

        verifySetStartTime(em2, T5);
        verifySetStartTime(em1, T4);
    }

    @Test
    public void verifyConcurrentClearStartTime() {
        EntityManager em1 = createEntityManager();
        EntityManager em2 = getEMF().createEntityManager();
        
        assertNotSame(em1, em2);
        
        TemporalHelper.setEffectiveTime(em1, T6);
        TemporalHelper.setEffectiveTime(em2, T7);

        verifySetStartTime(em2, T7);
        verifySetStartTime(em1, T6);

        TemporalHelper.clearEffectiveTime(em1);
        verifySetStartTime(em2, T7);
        assertFalse(em1.getProperties().containsKey(TemporalHelper.EFF_TS_PROPERTY));
        assertFalse(TemporalHelper.hasEffectiveTime(em1));
        assertNull(TemporalHelper.getEffectiveTime(em1));
        
        TemporalHelper.clearEffectiveTime(em2);
        assertFalse(em2.getProperties().containsKey(TemporalHelper.EFF_TS_PROPERTY));
        assertFalse(TemporalHelper.hasEffectiveTime(em2));
        assertNull(TemporalHelper.getEffectiveTime(em2));
    }

}
