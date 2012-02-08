/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 and Eclipse Distribution License v. 1.0 which accompanies
 * this distribution. The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html and the Eclipse Distribution
 * License is available at http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors: dclarke - Bug 361016: Future Versions Examples
 ******************************************************************************/
package tests;

import static example.PersonModelExample.*;

import java.util.List;

import javax.persistence.EntityManager;

import junit.framework.Assert;
import model.Person;

import org.junit.Test;

import temporal.TemporalHelper;
import temporal.persistence.TemporalQueryRedirector;
import example.PersonModelExample;

/**
 * Tests that verify the functionality of {@link TemporalQueryRedirector} which
 * redirectors current queries to be temporal queries when an effective time is
 * in the future.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class TemporalQueryRedirectorTests extends BaseTestCase {

    private static PersonModelExample example = new PersonModelExample();

    @Test
    public void allAtPresent() {
        EntityManager em = createEntityManager();

        List<Person> results = em.createQuery("SELECT p FROM Person p", Person.class).getResultList();

        Assert.assertEquals(1, results.size());
    }

    @Test
    public void allAtT2() {
        EntityManager em = createEntityManager();
        TemporalHelper.setEffectiveTime(em, T2);

        List<Person> results = em.createQuery("SELECT p FROM Person p", Person.class).getResultList();

        Assert.assertEquals(1, results.size());
    }

    @Test
    public void exampleFullAtT3UsingID() {
        EntityManager em = createEntityManager();
        TemporalHelper.setEffectiveTime(em, T3);

        List<Person> results = em.createQuery("SELECT p FROM Person p WHERE p.id = " + example.fullPerson.getId(), Person.class).getResultList();

        Assert.assertEquals(1, results.size());
    }

    @Test
    public void exampleFullAtT4() {
        EntityManager em = createEntityManager();
        TemporalHelper.setEffectiveTime(em, T4);

        List<Person> results = em.createQuery("SELECT p FROM Person p WHERE p.id = " + example.fullPerson.getId(), Person.class).getResultList();

        Assert.assertEquals(1, results.size());
    }

    @Override
    public void populate(EntityManager em) {
        em.persist(example.fullPerson);
    }
}
