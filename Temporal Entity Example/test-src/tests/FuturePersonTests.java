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
package tests;

import static example.PersonModelExample.T1;
import static example.PersonModelExample.T2;
import static example.PersonModelExample.T3;
import static example.PersonModelExample.T4;
import static example.PersonModelExample.*;
import static temporal.Effectivity.BOT;

import java.util.List;

import model.Person;
import model.PersonHobby;

import org.junit.Assert;
import org.junit.Test;

import temporal.TemporalEntityManager;
import example.PersonModelExample;

/**
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class FuturePersonTests extends BaseTestCase {

    private static PersonModelExample example = new PersonModelExample();

    private Person getSample() {
        return example.futurePerson;
    }

    @Override
    public void populate(TemporalEntityManager em) {
        example.populateHobbies(em);
        for (PersonHobby ph : example.futurePerson.getPersonHobbies().values()) {
            em.persist(ph);
        }
        em.persist(example.futurePerson);
    }

    @Test
    public void queryAllCurrent() {
        TemporalEntityManager em = getEntityManager();

        List<Person> results = example.queryAllCurrent(em);

        Assert.assertTrue(results.isEmpty());
    }

    @Test
    public void queryPersonEditionAtBOT() {
        TemporalEntityManager em = getEntityManager();
        em.setProperty("EFF_TS", BOT);

        List<Person> results = em.createQuery("SELECT p FROM Person p WHERE p.cid = " + getSample().getId(), Person.class).getResultList();

        Assert.assertTrue("Editions found", results.isEmpty());
    }

    @Test
    public void queryFutureEditionOfCurrentPersonAtT1() {
        TemporalEntityManager em = getEntityManager();
        em.setProperty("EFF_TS", T1);

        List<Person> results = em.createQuery("SELECT p FROM Person p WHERE p.cid = " + getSample().getId(), Person.class).getResultList();

        Assert.assertTrue("Editions found", results.isEmpty());
    }

    @Test
    public void queryFutureEditionOfCurrentPersonAtT2() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime(T2);

        List<Person> results = em.createQuery("SELECT p FROM Person p WHERE p.cid = " + getSample().getId(), Person.class).getResultList();

        System.out.println("QUERY EFFECTIVE @ T2:");
        for (Person p : results) {
            System.out.println("\t>" + p);
            System.out.println("\t\t>" + p.getAddress());
            System.out.println("\t\t>" + p.getPhones().values());
        }

        Assert.assertFalse("No Editions found", results.isEmpty());
        Assert.assertEquals(1, results.size());

        Person person = results.get(0);

        Assert.assertSame(person, person.getContinuity());
        Assert.assertFalse(person.getEffectivity().isCurrent());
        Assert.assertTrue(person.getEffectivity().isFutureEdition());
        Assert.assertEquals(getSample().getId(), person.getId());
        Assert.assertEquals(getSample().getName(), person.getName());
        Assert.assertNull(person.getAddress());
        Assert.assertTrue(person.getPhones().isEmpty());
    }

    @Test
    public void queryFutureEditionOfCurrentPersonAtT3() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime(T3);

        List<Person> results = em.createQuery("SELECT p FROM Person p WHERE p.cid = " + getSample().getId(), Person.class).getResultList();

        System.out.println("QUERY EFFECTIVE @ T3:");
        for (Person p : results) {
            System.out.println("\t>" + p);
            System.out.println("\t\t>" + p.getAddress());
            System.out.println("\t\t>" + p.getPhones().values());
        }

        Assert.assertFalse("No Editions found", results.isEmpty());
        Assert.assertEquals(1, results.size());

        Person person = results.get(0);

        Assert.assertSame(person, person.getContinuity());
        Assert.assertFalse(person.getEffectivity().isCurrent());
        Assert.assertTrue(person.getEffectivity().isFutureEdition());
        Assert.assertEquals(getSample().getId(), person.getId());
        Assert.assertEquals(getSample().getName(), person.getName());
        Assert.assertNotNull(person.getAddress());
        Assert.assertTrue(person.getPhones().isEmpty());
    }

    @Test
    public void queryFutureEditionOfCurrentPersonAtT4() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime(T4);

        List<Person> results = em.createQuery("SELECT p FROM Person p WHERE p.cid = " + getSample().getId(), Person.class).getResultList();

        Assert.assertFalse("No PersonEdition Found", results.isEmpty());

        System.out.println("QUERY EFFECTIVE @ T4:");
        for (Person p : results) {
            System.out.println("\t>" + p);
            System.out.println("\t\t>" + p.getAddress());
            System.out.println("\t\t>" + p.getPhones().values());
        }

        Assert.assertFalse("No Editions found", results.isEmpty());
        Assert.assertEquals(1, results.size());

        Person person = results.get(0);

        Assert.assertSame(person, person.getContinuity());
        Assert.assertFalse(person.getEffectivity().isCurrent());
        Assert.assertTrue(person.getEffectivity().isFutureEdition());
        Assert.assertEquals(getSample().getId(), person.getId());
        Assert.assertEquals(getSample().getName(), person.getName());
        Assert.assertNotNull(person.getAddress());
        Assert.assertFalse(person.getPhones().isEmpty());
    }

    @Test
    public void queryFutureEditionOfCurrentPersonAtT5() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime(T5);

        List<Person> results = em.createQuery("SELECT p FROM Person p WHERE p.cid = " + getSample().getId(), Person.class).getResultList();

        System.out.println("QUERY EFFECTIVE @ T5:");
        for (Person p : results) {
            System.out.println("\t>" + p);
            System.out.println("\t\t>" + p.getAddress());
            System.out.println("\t\t>" + p.getPhones().values());
        }

        Assert.assertFalse("No Editions found", results.isEmpty());
        Assert.assertEquals(1, results.size());

        Person person = results.get(0);

        Assert.assertSame(person, person.getContinuity());
        Assert.assertFalse(person.getEffectivity().isCurrent());
        Assert.assertTrue(person.getEffectivity().isFutureEdition());
        Assert.assertEquals(getSample().getId(), person.getId());
        Assert.assertEquals(getSample().getName(), person.getName());
        Assert.assertNotNull(person.getAddress());
        Assert.assertFalse(person.getPhones().isEmpty());
    }

    @Test
    public void verifyCreateNewEntityInFuture() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime(T6, true);

        em.getTransaction().begin();

        Person p = em.newEntity(Person.class);

        Assert.assertNotNull(p);
        Assert.assertNotNull(p.getEffectivity());

        em.getTransaction().rollback();
    }

}
