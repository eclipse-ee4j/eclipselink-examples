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

import static example.PersonModelExample.GOLF;
import static example.PersonModelExample.RUN;
import static example.PersonModelExample.SKI;
import static example.PersonModelExample.T1;
import static example.PersonModelExample.T2;
import static example.PersonModelExample.T3;
import static example.PersonModelExample.T4;
import static example.PersonModelExample.T5;
import static temporal.Effectivity.BOT;
import static temporal.Effectivity.EOT;

import java.sql.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import model.Address;
import model.Person;
import model.Phone;
import model.entities.PhoneEntity;

import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.sessions.CopyGroup;
import org.junit.Assert;
import org.junit.Test;

import temporal.BaseEntity;
import temporal.EditionSet;
import temporal.TemporalEntityManager;
import temporal.TemporalHelper;
import example.PersonModelExample;

/**
 * This test case performs current and edition queries on a simple
 * Person-Address-Phones model both illustrating and verifying query operations.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class FullPersonWithEditions extends BaseTestCase {

    private static PersonModelExample example = new PersonModelExample();

    private Person getSample() {
        return example.fullPerson;
    }

    @Override
    public void populate(TemporalEntityManager em) {
        System.out.println("\nFullPersonWithEditions.populate:START");

        example.populateHobbies(em);
        em.persist(example.fullPerson);
        em.flush();

        System.out.println("\n> Create T2 Edition");
        em.setEffectiveTime(T2, true);

        Person fpEdition = em.find(Person.class, example.fullPerson.getId());
        Person personEditionT2 = em.newEdition(fpEdition);

        personEditionT2.setName("Jimmy");
        Address aT2 = em.newEdition(example.fullPerson.getAddress());
        aT2.setCity("Toronto");
        aT2.setState("ON");
        personEditionT2.setDateOfBirth(new Date(75, 1, 5));

        personEditionT2.setAddress(aT2);
        Phone pT2 = em.newEdition(example.fullPerson.getPhone("Home"));
        personEditionT2.addPhone(pT2);
        pT2.setNumber("222-222-2222");
        Phone pWT2 = em.newEntity(PhoneEntity.class);
        pWT2.setType("Work");
        pWT2.setNumber("333-333-3333");
        personEditionT2.addPhone(pWT2);

        em.persist(personEditionT2.addHobby(example.hobbies.get(GOLF), T2));

        // Assert.assertEquals(personEditionT2.getPhones().size() - 1,
        // fpEdition.getPhones().size());
        // Assert.assertEquals(personEditionT2.getPersonHobbies().size() - 1,
        // fpEdition.getPersonHobbies().size());

        em.flush();

        System.out.println("\n> Create T4 Edition");
        em.setEffectiveTime(T4, true);

        Person personEditionT4 = em.newEdition(personEditionT2);
        personEditionT4.setName("James");
        Address aT4 = em.newEdition(aT2);
        aT4.setCity("San Francisco");
        aT4.setState("CA");
        personEditionT4.setAddress(aT4);

        Phone pT4 = em.newEdition(pT2);
        pT4.setNumber("444-444-4444");
        personEditionT4.addPhone(pT4);
        pWT2.getEffectivity().setEnd(T4);
        Phone pCT4 = em.newEntity(PhoneEntity.class);
        pCT4.setType("Cell");
        pCT4.setNumber("555-555-55555");
        personEditionT4.addPhone(pCT4);

        personEditionT4.getPersonHobbies().get(GOLF).getEffectivity().setEnd(T4);

        em.persist(personEditionT4.addHobby(example.hobbies.get(RUN), T4));
        em.persist(personEditionT4.addHobby(example.hobbies.get(SKI), T4));

        em.flush();

        System.out.println("\nFullPersonWithEditions.populate::DONE");
    }

    @Test
    public void queryAllCurrent() {
        TemporalEntityManager em = getEntityManager();
        List<Person> results = em.createQuery("SELECT p From Person p", Person.class).getResultList();

        System.out.println("QUERY CURRENT:");
        for (Person p : results) {
            System.out.println("\t>" + p);
        }

        Assert.assertEquals(1, results.size());

        Person currentperson = results.get(0);
        Assert.assertSame(currentperson, currentperson.getContinuity());
        Assert.assertEquals(getSample().getId(), currentperson.getId());
    }

    @Test
    public void findCurrent() {
        TemporalEntityManager em = getEntityManager();

        Person current = em.find(Person.class, getSample().getId());

        System.out.println("VERIFY CURRENT: " + current);

        // Verify person
        Assert.assertNotNull(current);
        Assert.assertEquals(current, current.getContinuity());
        Assert.assertEquals(getSample().getId(), current.getId());
        Assert.assertEquals(getSample().getName(), current.getName());
        Assert.assertTrue(current.getEffectivity().isCurrent());
        Assert.assertFalse(current.getEffectivity().isFutureEdition());
        Assert.assertEquals(current.getEffectivity().getStart(), BOT);
        Assert.assertEquals(current.getEffectivity().getEnd(), T2);

        // Address
        Assert.assertNotNull(current.getAddress());
        Assert.assertEquals(getSample().getAddress().getStreet(), current.getAddress().getStreet());
        Assert.assertEquals(getSample().getAddress().getCity(), current.getAddress().getCity());
        Assert.assertEquals(getSample().getAddress().getState(), current.getAddress().getState());
        Assert.assertTrue(current.getAddress().getEffectivity().isCurrent());
        Assert.assertFalse(current.getAddress().getEffectivity().isFutureEdition());
        Assert.assertEquals(current.getAddress().getEffectivity().getStart(), BOT);
        Assert.assertEquals(current.getAddress().getEffectivity().getEnd(), T2);

        // Phone
        Assert.assertEquals(1, current.getPhones().size());
        Phone currentHome = current.getPhone("Home");
        Assert.assertNotNull(currentHome);
        Assert.assertEquals("111-111-1111", currentHome.getNumber());
        Assert.assertSame(current, currentHome.getPerson());
    }

    @Test
    public void queryAllCurrentJoinAddress() {
        TemporalEntityManager em = getEntityManager();
        List<Person> results = em.createQuery("SELECT p From Person p JOIN FETCH p.address", Person.class).getResultList();

        System.out.println("QUERY CURRENT:");
        for (Person p : results) {
            System.out.println("\t>" + p);
        }

        Assert.assertEquals(1, results.size());

        Person currentperson = results.get(0);
        Assert.assertSame(currentperson, currentperson.getContinuity());
        Assert.assertEquals(getSample().getId(), currentperson.getId());
    }

    @Test
    public void querySampleCurrentPerson() {
        TemporalEntityManager em = getEntityManager();

        Person person = em.createQuery("SELECT p From Person p WHERE p.id = " + getSample().getId(), Person.class).getSingleResult();
        Address address = person.getAddress();

        Assert.assertNotNull(person);

        System.out.println("FIND CURRENT: " + person);

        Assert.assertEquals(getSample().getId(), person.getId());
        Assert.assertSame(person, person.getContinuity());
        Assert.assertNotNull(address);
        Assert.assertEquals(getSample().getAddress().getCity(), address.getCity());
    }

    @Test
    public void querySampleCurrentPersonJoinAddress() {
        TemporalEntityManager em = getEntityManager();

        Person person = em.createQuery("SELECT p From Person p JOIN FETCH p.address WHERE p.id = " + getSample().getId(), Person.class).getSingleResult();
        Address address = person.getAddress();

        Assert.assertNotNull(person);

        System.out.println("FIND CURRENT: " + person);

        Assert.assertEquals(getSample().getId(), person.getId());
        Assert.assertEquals(person, person.getContinuity());
        Assert.assertNotNull(address);
        Assert.assertEquals(getSample().getAddress().getCity(), address.getCity());
    }

    @Test
    public void findSampleCurrentPerson() {
        TemporalEntityManager em = getEntityManager();
        Person person = em.find(Person.class, getSample().getId());

        Assert.assertNotNull(person);

        System.out.println("FIND CURRENT: " + person);

        Assert.assertEquals(getSample().getId(), person.getId());
        Assert.assertSame(person, person.getContinuity());
        Assert.assertTrue(person.getEffectivity().isCurrent());
        Assert.assertFalse(person.getEffectivity().isFutureEdition());
        Assert.assertEquals(person.getEffectivity().getStart(), BOT);
        Assert.assertEquals(person.getEffectivity().getEnd(), T2);

        Assert.assertEquals(0, person.getPersonHobbies().size());

        Assert.assertEquals(1, person.getPhones().size());

    }

    @Test
    public void findFuturePersonEntityEditionT2() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime(T2);

        Person person = em.find(Person.class, getSample().getId());
        Assert.assertNotNull(person);
        System.out.println("FIND Future Edition: " + person);
        Assert.assertEquals(1, person.getPersonHobbies().size());

        Person continuity = person.getContinuity();

        Assert.assertNotNull(continuity);
        System.out.println("\tContinuity: " + continuity);
        Assert.assertTrue("Not an edition entity", TemporalHelper.isEdition(em, person));
        Assert.assertEquals(getSample().getId(), person.getContinuity().getId());

        Assert.assertFalse(person.getEffectivity().isCurrent());
        Assert.assertTrue(person.getEffectivity().isFutureEdition());
        Assert.assertEquals(person.getEffectivity().getStart(), T2);
        Assert.assertEquals(person.getEffectivity().getEnd(), T4);
        Assert.assertNotSame(person, person.getContinuity());
    }

    @Test
    public void queryFutureEditionOfCurrentPersonAtBOT() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime(BOT);

        Person pEdition = em.createQuery("SELECT p From Person p WHERE p.id = " + getSample().getId(), Person.class).getSingleResult();

        System.out.println("QUERY EDITION @ BOT: " + pEdition);

        Assert.assertNotNull("No edition found at BOT", pEdition);
        Assert.assertTrue(pEdition.getEffectivity().isCurrent());
        Assert.assertFalse(pEdition.getEffectivity().isFutureEdition());
        Assert.assertEquals(BOT, pEdition.getEffectivity().getStart());
        Assert.assertEquals(T2, pEdition.getEffectivity().getEnd());
        Assert.assertNotNull("No Continuity found", pEdition.getContinuity());
        Assert.assertEquals(0, pEdition.getPersonHobbies().size());

        Address address = pEdition.getAddress();

        Assert.assertNotNull(address);
        Assert.assertEquals(getSample().getAddress().getCity(), address.getCity());

        Assert.assertEquals(1, pEdition.getPhones().size());
    }

    @Test
    public void queryFutureEditionOfCurrentPersonAtT1() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime(T1);

        Person pEdition = em.createQuery("SELECT p From Person p WHERE p.id = " + getSample().getId(), Person.class).getSingleResult();

        System.out.println("QUERY EDITION @ T1: " + pEdition);

        Assert.assertNotNull("No edition found at T1", pEdition);
        Assert.assertTrue(pEdition.getEffectivity().isCurrent());
        Assert.assertFalse(pEdition.getEffectivity().isFutureEdition());
        Assert.assertEquals(BOT, pEdition.getEffectivity().getStart());
        Assert.assertEquals(T2, pEdition.getEffectivity().getEnd());
        Assert.assertNotNull("No Continuity found", pEdition.getContinuity());

        Assert.assertEquals(0, pEdition.getPersonHobbies().size());
        Assert.assertFalse(pEdition.getPersonHobbies().containsKey(SKI));
        Assert.assertFalse(pEdition.getPersonHobbies().containsKey(RUN));
        Assert.assertFalse(pEdition.getPersonHobbies().containsKey(GOLF));

        Address address = pEdition.getAddress();

        Assert.assertNotNull(address);
        Assert.assertEquals(getSample().getAddress().getCity(), address.getCity());

        Assert.assertEquals(1, pEdition.getPhones().size());
    }

    @Test
    public void queryFutureEditionOfCurrentPersonAtT2() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime(T2);

        Person pEdition = em.createQuery("SELECT p From Person p WHERE p.id = " + getSample().getId(), Person.class).getSingleResult();

        System.out.println("QUERY EDITION @ T2: " + pEdition);

        Assert.assertNotNull("No edition found at T2", pEdition);
        Assert.assertFalse(pEdition.getEffectivity().isCurrent());
        Assert.assertTrue(pEdition.getEffectivity().isFutureEdition());
        Assert.assertEquals(T2, pEdition.getEffectivity().getStart());
        Assert.assertEquals(T4, pEdition.getEffectivity().getEnd());
        Assert.assertNotSame(pEdition, pEdition.getContinuity());

        Assert.assertEquals(1, pEdition.getPersonHobbies().size());
        Assert.assertFalse(pEdition.getPersonHobbies().containsKey(SKI));
        Assert.assertFalse(pEdition.getPersonHobbies().containsKey(RUN));
        Assert.assertTrue(pEdition.getPersonHobbies().containsKey(GOLF));

        Address address = pEdition.getAddress();

        Assert.assertNotNull(address);
        Assert.assertEquals("Toronto", address.getCity());

        Assert.assertEquals(2, pEdition.getPhones().size());
    }

    @Test
    public void queryFutureEditionOfCurrentPersonAtT2JoinFetchAddress() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime(T2);

        Person pEdition = null;
        try {
            pEdition = em.createQuery("SELECT p From Person p JOIN FETCH p.address WHERE p.id = " + getSample().getId(), Person.class).getSingleResult();
        } catch (NoResultException e) {
            Assert.fail("Join returned no result");
        }
        Address address = pEdition.getAddress();

        System.out.println("QUERY EDITION @ T2: " + pEdition);
        System.out.println("\t> " + address);

        Assert.assertNotNull("No edition found", pEdition);
        Assert.assertFalse(pEdition.getEffectivity().isCurrent());
        Assert.assertTrue(pEdition.getEffectivity().isFutureEdition());
        Assert.assertEquals(T2, pEdition.getEffectivity().getStart());
        Assert.assertEquals(T4, pEdition.getEffectivity().getEnd());
        Assert.assertNotNull("No Continuity found", pEdition.getContinuity());
        Assert.assertNotNull(address);
        Assert.assertEquals("Toronto", address.getCity());

        Assert.assertEquals(1, pEdition.getPersonHobbies().size());
        Assert.assertFalse(pEdition.getPersonHobbies().containsKey(SKI));
        Assert.assertFalse(pEdition.getPersonHobbies().containsKey(RUN));
        Assert.assertTrue(pEdition.getPersonHobbies().containsKey(GOLF));

        Assert.assertEquals(2, pEdition.getPhones().size());
    }

    @Test
    public void queryFutureEditionOfCurrentPersonAtT3() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime(T3);

        Person pEdition = em.createQuery("SELECT p From Person p WHERE p.id = " + getSample().getId(), Person.class).getSingleResult();

        System.out.println("QUERY EDITION @ T3: " + pEdition);

        Assert.assertNotNull("No edition found ", pEdition);
        Assert.assertFalse(pEdition.getEffectivity().isCurrent());
        Assert.assertTrue(pEdition.getEffectivity().isFutureEdition());
        Assert.assertEquals(T2, pEdition.getEffectivity().getStart());
        Assert.assertEquals(T4, pEdition.getEffectivity().getEnd());
        Assert.assertNotSame(pEdition, pEdition.getContinuity());

        Assert.assertEquals(1, pEdition.getPersonHobbies().size());
        Assert.assertFalse(pEdition.getPersonHobbies().containsKey(SKI));
        Assert.assertFalse(pEdition.getPersonHobbies().containsKey(RUN));
        Assert.assertTrue(pEdition.getPersonHobbies().containsKey(GOLF));

        Address address = pEdition.getAddress();

        Assert.assertNotNull(address);
        Assert.assertEquals("Toronto", address.getCity());

        Assert.assertEquals(2, pEdition.getPhones().size());
    }

    @Test
    public void queryFutureEditionOfCurrentPersonAtT4() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime(T4);

        Person pEdition = em.createQuery("SELECT p From Person p WHERE p.id = " + getSample().getId(), Person.class).getSingleResult();

        System.out.println("QUERY EDITION @ T4: " + pEdition);

        Assert.assertNotNull("No Person Edition Found", pEdition);
        Assert.assertFalse(pEdition.getEffectivity().isCurrent());
        Assert.assertTrue(pEdition.getEffectivity().isFutureEdition());
        Assert.assertEquals(T4, pEdition.getEffectivity().getStart());
        Assert.assertEquals(EOT, pEdition.getEffectivity().getEnd());
        Assert.assertNotSame(pEdition, pEdition.getContinuity());

        Assert.assertEquals(2, pEdition.getPersonHobbies().size());
        Assert.assertTrue(pEdition.getPersonHobbies().containsKey(SKI));
        Assert.assertTrue(pEdition.getPersonHobbies().containsKey(RUN));
        Assert.assertFalse(pEdition.getPersonHobbies().containsKey(GOLF));

        Address address = pEdition.getAddress();

        Assert.assertNotNull(address);

        Assert.assertEquals(2, pEdition.getPhones().size());
    }

    @Test
    public void queryFutureEditionOfCurrentPersonAtT5() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime(T5);

        Person pEdition = em.createQuery("SELECT p From Person p WHERE p.id = " + getSample().getId(), Person.class).getSingleResult();

        System.out.println("QUERY EDITION @ T5: " + pEdition);

        Assert.assertNotNull("No edition found at T5", pEdition);
        Assert.assertFalse(pEdition.getEffectivity().isCurrent());
        Assert.assertTrue(pEdition.getEffectivity().isFutureEdition());
        Assert.assertEquals(T4, pEdition.getEffectivity().getStart());
        Assert.assertEquals(EOT, pEdition.getEffectivity().getEnd());
        Assert.assertNotSame(pEdition, pEdition.getContinuity());
        Assert.assertEquals(2, pEdition.getPersonHobbies().size());

        Assert.assertTrue(pEdition.getPersonHobbies().containsKey(SKI));
        Assert.assertTrue(pEdition.getPersonHobbies().containsKey(RUN));
        Assert.assertFalse(pEdition.getPersonHobbies().containsKey(GOLF));

        Assert.assertEquals(2, pEdition.getPhones().size());
    }

    @Test
    public void nativeQueryForAllEdition() {
        TemporalEntityManager em = getEntityManager();

        TypedQuery<Person> query = em.createNamedQuery("PersonEdition.all", Person.class);
        query.setParameter("CID", getSample().getId());
        List<Person> editions = query.getResultList();

        Assert.assertFalse("No edition found", editions.isEmpty());

        System.out.println("QUERY ALL EDITIONS:");
        for (Person p : editions) {
            System.out.println("\t" + p);
            Assert.assertNotNull("No Continuity found", p.getContinuity());
        }

        Assert.assertEquals(3, editions.size());
    }

    // @Test
    public void deleteAllAtT5() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime(T5);

        Person p = em.find(Person.class, getSample().getId());

        em.getTransaction().begin();

        p.getEffectivity().setEnd(T5);
        p.getAddress().getEffectivity().setEnd(T5);
        for (Phone phone : p.getPhones().values()) {
            phone.getEffectivity().setEnd(T5);
        }

        em.flush();

        // TODO - validation
    }

    @Test
    public void detachResultUsingCopyPolicy() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime(T2);

        TypedQuery<Person> query = em.createNamedQuery("PersonEdition.find", Person.class);
        query.setParameter("ID", getSample().getId());

        Person p = query.getSingleResult();

        System.out.println("ORIGINAL: " + p + " HASHCODE: " + System.identityHashCode(p));
        System.out.println("\t" + p.getAddress());

        CopyGroup cg = new CopyGroup();
        cg.cascadeAllParts();

        Person pCopy = (Person) JpaHelper.getEntityManager(em).copy(p, cg);
        System.out.println("COPY: " + pCopy + " HASHSCODE: " + System.identityHashCode(pCopy));
        System.out.println("\t" + pCopy.getAddress());
    }

    @Test
    public void modifyFutureEditionOfCurrentPersonAtT4() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime(T4);

        Person pEdition = em.createQuery("SELECT p From Person p WHERE p.id = " + getSample().getId(), Person.class).getSingleResult();

        System.out.println("QUERY EDITION @ T4: " + pEdition);

        Assert.assertNotNull("No Person Edition Found", pEdition);
        Assert.assertFalse(pEdition.getEffectivity().isCurrent());
        Assert.assertTrue(pEdition.getEffectivity().isFutureEdition());
        Assert.assertEquals(T4, pEdition.getEffectivity().getStart());
        Assert.assertNotSame(pEdition, pEdition.getContinuity());

        Assert.assertEquals(2, pEdition.getPersonHobbies().size());
        Assert.assertTrue(pEdition.getPersonHobbies().containsKey(SKI));
        Assert.assertTrue(pEdition.getPersonHobbies().containsKey(RUN));
        Assert.assertFalse(pEdition.getPersonHobbies().containsKey(GOLF));

        long currentVersion = pEdition.getVersion();

        em.getTransaction().begin();
        pEdition.setName(pEdition.getName().toUpperCase());
        em.flush();

        Assert.assertEquals(currentVersion + 1, pEdition.getVersion());
    }

    @Test
    public void modifyFutureEditionOfCurrentPersonAtT4UsingMerge() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime(T4);

        Person pEdition = em.createQuery("SELECT p From Person p WHERE p.id = " + getSample().getId(), Person.class).getSingleResult();

        System.out.println("QUERY EDITION @ T4: " + pEdition);

        // Create new unregistered hobby and add.

        Assert.assertNotNull("No Person Edition Found", pEdition);
        Assert.assertFalse(pEdition.getEffectivity().isCurrent());
        Assert.assertTrue(pEdition.getEffectivity().isFutureEdition());
        Assert.assertEquals(T4, pEdition.getEffectivity().getStart());
        Assert.assertNotSame(pEdition, pEdition.getContinuity());

        Assert.assertEquals(2, pEdition.getPersonHobbies().size());
        Assert.assertTrue(pEdition.getPersonHobbies().containsKey(SKI));
        Assert.assertTrue(pEdition.getPersonHobbies().containsKey(RUN));
        Assert.assertFalse(pEdition.getPersonHobbies().containsKey(GOLF));

        long currentVersion = pEdition.getVersion();

        em.getTransaction().begin();
        pEdition.setName(pEdition.getName().toUpperCase());
        em.flush();

        Assert.assertEquals(currentVersion + 1, pEdition.getVersion());
    }

    /**
     * Verify that the edition creation operation correctly copies values
     * including mutable values and collections.
     */
    @Test
    public void verifyCreateEditionCopying() {
        TemporalEntityManager em = getEntityManager();
        EditionSet es = em.setEffectiveTime(T5, true);

        Person pEdition = em.find(Person.class, getSample().getId());

        Assert.assertNotNull(pEdition);
        Assert.assertTrue(TemporalHelper.isEdition(em, pEdition));
        Assert.assertEquals(T4, pEdition.getEffectivity().getStart());
        Assert.assertNotNull(es);
        Assert.assertTrue(es.getEntries().isEmpty());

        Person pAtT5 = em.newEdition(pEdition);

        Assert.assertNotNull(pAtT5);
        Assert.assertTrue(TemporalHelper.isEdition(em, pEdition));
        Assert.assertEquals(T5, pAtT5.getEffectivity().getStart());
        Assert.assertFalse(es.getEntries().isEmpty());
        Assert.assertEquals(1, es.getEntries().size());

        // Verify collection/map cloning
        Assert.assertNotSame(pEdition.getPhones(), pAtT5.getPhones());
        Assert.assertNotSame(pEdition.getPersonHobbies(), pAtT5.getPersonHobbies());
        Assert.assertNotSame(pEdition.getNicknames(), pAtT5.getNicknames());

        // Mutable non-temporal values
        Assert.assertSame(pEdition.getDateOfBirth(), pAtT5.getDateOfBirth());

        // TODO: Validate mutable basic copying

    }

    @Test
    public void testDateOfBirthNonTemporalStorage() {
        TemporalEntityManager em = getEntityManager();

        List<?> results = em.createNativeQuery("SELECT DATEOFBIRTH FROM TPERSON WHERE CID = 1 ORDER BY OID").getResultList();

        Assert.assertNotNull(results);
        Assert.assertEquals(3, results.size());
        Assert.assertEquals(new Date(75, 1, 5), results.get(0));
        Assert.assertNull(results.get(1));
        Assert.assertNull(results.get(2));
    }

    /**
     * Verify the query result and relationship to person
     */
    @SuppressWarnings("unchecked")
    @Test
    public void queryCurrentHomePhone() {
        TemporalEntityManager em = getEntityManager();

        TypedQuery<Phone> query = em.createQuery("SELECT p FROM Phone p WHERE p.type = 'Home'", Phone.class);
        Phone phone = query.getSingleResult();

        Assert.assertNotNull(phone);
        Assert.assertFalse(TemporalHelper.isEditionClass((Class<BaseEntity>) phone.getClass()));
        Assert.assertNotNull(phone.getContinuity());
        Assert.assertEquals(phone, phone.getContinuity());
        Assert.assertEquals(BOT, phone.getEffectivity().getStart());
        Assert.assertEquals(T2, phone.getEffectivity().getEnd());

        Assert.assertNotNull(phone.getPerson());
        Assert.assertEquals(phone.getEffectivity().getStart(), phone.getPerson().getEffectivity().getStart());
    }

    /**
     * Verify the query result and relationship to person
     */
    @SuppressWarnings("unchecked")
    @Test
    public void queryHomePhoneAtBOT() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime(BOT);

        TypedQuery<Phone> query = em.createQuery("SELECT p FROM Phone p WHERE p.type = 'Home'", Phone.class);
        Phone phone = query.getSingleResult();

        Assert.assertNotNull(phone);
        Assert.assertTrue(TemporalHelper.isEditionClass((Class<BaseEntity>) phone.getClass()));
        Assert.assertNotNull(phone.getContinuity());
        Assert.assertEquals(BOT, phone.getEffectivity().getStart());
        Assert.assertEquals(T2, phone.getEffectivity().getEnd());

        Assert.assertNotNull(phone.getPerson());
        Assert.assertEquals(phone.getEffectivity().getStart(), phone.getPerson().getEffectivity().getStart());
    }

    /**
     * Verify the query result and relationship to person
     */
    @SuppressWarnings("unchecked")
    @Test
    public void queryHomePhoneAtT1() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime(T1);

        TypedQuery<Phone> query = em.createQuery("SELECT p FROM Phone p WHERE p.type = 'Home'", Phone.class);
        Phone phone = query.getSingleResult();

        Assert.assertNotNull(phone);
        Assert.assertTrue(TemporalHelper.isEditionClass((Class<BaseEntity>) phone.getClass()));
        Assert.assertNotNull(phone.getContinuity());
        Assert.assertEquals(BOT, phone.getEffectivity().getStart());
        Assert.assertEquals(T2, phone.getEffectivity().getEnd());

        Assert.assertNotNull(phone.getPerson());
        Assert.assertEquals(phone.getEffectivity().getStart(), phone.getPerson().getEffectivity().getStart());
    }

    /**
     * Verify the query result and relationship to person
     */
    @SuppressWarnings("unchecked")
    @Test
    public void queryHomePhoneAtT2() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime(T2);

        TypedQuery<Phone> query = em.createQuery("SELECT p FROM Phone p WHERE p.type = 'Home'", Phone.class);
        Phone phone = query.getSingleResult();

        Assert.assertNotNull(phone);
        Assert.assertTrue(TemporalHelper.isEditionClass((Class<BaseEntity>) phone.getClass()));
        Assert.assertNotNull(phone.getContinuity());
        Assert.assertEquals(T2, phone.getEffectivity().getStart());
        Assert.assertEquals(T4, phone.getEffectivity().getEnd());

        Assert.assertNotNull(phone.getPerson());
        Assert.assertEquals(phone.getEffectivity().getStart(), phone.getPerson().getEffectivity().getStart());
    }

    /**
     * Verify the query result and relationship to person
     */
    @SuppressWarnings("unchecked")
    @Test
    public void queryHomePhoneAtT3() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime(T3);

        TypedQuery<Phone> query = em.createQuery("SELECT p FROM Phone p WHERE p.type = 'Home'", Phone.class);
        Phone phone = query.getSingleResult();

        Assert.assertNotNull(phone);
        Assert.assertTrue(TemporalHelper.isEditionClass((Class<BaseEntity>) phone.getClass()));
        Assert.assertNotNull(phone.getContinuity());
        Assert.assertEquals(T2, phone.getEffectivity().getStart());
        Assert.assertEquals(T4, phone.getEffectivity().getEnd());

        Assert.assertNotNull(phone.getPerson());
        Assert.assertEquals(phone.getEffectivity().getStart(), phone.getPerson().getEffectivity().getStart());
    }

    /**
     * Verify the query result and relationship to person
     */
    @SuppressWarnings("unchecked")
    @Test
    public void queryHomePhoneAtT4() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime(T4);

        TypedQuery<Phone> query = em.createQuery("SELECT p FROM Phone p WHERE p.type = 'Home'", Phone.class);
        Phone phone = query.getSingleResult();

        Assert.assertNotNull(phone);
        Assert.assertTrue(TemporalHelper.isEditionClass((Class<BaseEntity>) phone.getClass()));
        Assert.assertNotNull(phone.getContinuity());
        Assert.assertEquals(T4, phone.getEffectivity().getStart());
        Assert.assertEquals(EOT, phone.getEffectivity().getEnd());

        Assert.assertNotNull(phone.getPerson());
        Assert.assertEquals(phone.getEffectivity().getStart(), phone.getPerson().getEffectivity().getStart());
    }

    /**
     * Verify the query result and relationship to person
     */
    @SuppressWarnings("unchecked")
    @Test
    public void queryHomePhoneAtT5() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime(T5);

        TypedQuery<Phone> query = em.createQuery("SELECT p FROM Phone p WHERE p.type = 'Home'", Phone.class);
        Phone phone = query.getSingleResult();

        Assert.assertNotNull(phone);
        Assert.assertTrue(TemporalHelper.isEditionClass((Class<BaseEntity>) phone.getClass()));
        Assert.assertNotNull(phone.getContinuity());
        Assert.assertEquals(T4, phone.getEffectivity().getStart());
        Assert.assertEquals(EOT, phone.getEffectivity().getEnd());

        Assert.assertNotNull(phone.getPerson());
        Assert.assertEquals(phone.getEffectivity().getStart(), phone.getPerson().getEffectivity().getStart());
    }
}
