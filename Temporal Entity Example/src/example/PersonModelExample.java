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
package example;

import static temporal.TemporalHelper.EFF_TS_PROPERTY;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import model.Address;
import model.Hobby;
import model.Person;
import model.entities.AddressEntity;
import model.entities.PersonEntity;

import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

import temporal.Effectivity;

/**
 * This test case performs current and edition queries on a simple
 * Person-Address-Phones model both illustrating and verifying query operations.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1 
 */
public class PersonModelExample {

    public Person simplePerson;

    public Person fullPerson;

    public Person futurePerson;

    public Map<String, Hobby> hobbies = new HashMap<String, Hobby>();

    public static final long T1 = 100;
    public static final long T2 = 200;
    public static final long T3 = 300;
    public static final long T4 = 400;
    public static final long T5 = 500;
    public static final long T6 = 600;
    public static final long T7 = 700;
    public static final long T8 = 800;
    public static final long T9 = 900;
    public static final long T10 = 1000;

    public static final String GOLF = "Golfing";
    public static final String SKI = "Skiing";
    public static final String SWIM = "Swimming";
    public static final String RUN = "Running";
    public static final String PAINT = "Painting";

    public PersonModelExample() {
        // Create Hobbies
        this.hobbies.put(GOLF, new Hobby(GOLF, null));
        this.hobbies.put(SKI, new Hobby(SKI, null));
        this.hobbies.put(SWIM, new Hobby(SWIM, null));
        this.hobbies.put(RUN, new Hobby(RUN, null));
        this.hobbies.put(PAINT, new Hobby(PAINT, null));

        // Create Simple Person with no relationships
        this.simplePerson = new PersonEntity();
        this.simplePerson.setName("Bob");

        // create fullPerson
        this.fullPerson = new PersonEntity();
        this.fullPerson.setName("Jim");
        this.fullPerson.setAddress(new AddressEntity("123 Anywhere St", "Miami", "Florida"));
        this.fullPerson.addPhone("Home", "111-111-1111");
        this.fullPerson.setDateOfBirth(new Date(70, 8, 13));

        // create futurePerson
        this.futurePerson = new PersonEntity();
        this.futurePerson.setName("Sally");
        this.futurePerson.getEffectivity().setStart(T2);
        Address futureAddress = new AddressEntity("45 O'Connor Street", "Ottawa", "Ontario");
        futureAddress.getEffectivity().setStart(T3);
        this.futurePerson.setAddress(futureAddress);
        this.futurePerson.addPhone("Work", "321-654-0987").getEffectivity().setStart(T4);
        this.futurePerson.addHobby(this.hobbies.get(GOLF), T2);
    }

    public static void main(String[] args) {
        PersonModelExample example = new PersonModelExample();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("example");

        // Replace Schema
        Server session = JpaHelper.getServerSession(emf);
        SchemaManager sm = new SchemaManager(session);
        sm.replaceDefaultTables();
        sm.replaceSequences();

        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();
        example.populateAll(em);
        em.getTransaction().commit();

        em.clear();
        em.getEntityManagerFactory().getCache().evictAll();
        example.queryAllCurrent(em);

        em.close();
        emf.close();
    }

    /**
     * Create a new Person edition based on the current starting at T2.
     */
    public void populateAll(EntityManager em) {
        populateHobbies(em);
        em.persist(this.simplePerson);
        em.persist(this.fullPerson);
        em.persist(this.futurePerson);
    }

    public void populateHobbies(EntityManager em) {
        for (Hobby h : this.hobbies.values()) {
            em.persist(h);
        }
    }

    public List<Person> queryAllCurrent(EntityManager em) {
        List<Person> results = em.createQuery("SELECT p From Person p", Person.class).getResultList();

        System.out.println("\nQUERY ALL CURRENT:");
        for (Person p : results) {
            System.out.println("\t>" + p);
        }

        return results;
    }

    public List<Person> queryAllCurrentJoinAddress(EntityManager em) {
        List<Person> results = em.createQuery("SELECT p From Person p JOIN FETCH p.address", Person.class).getResultList();

        System.out.println("\nQUERY CURRENT (JOIN ADDRESS):");
        for (Person p : results) {
            System.out.println("\t>" + p);
        }

        return results;
    }

    public Person queryCurrentSimple(EntityManager em) {
        Person person = em.createQuery("SELECT p From Person p WHERE p.id = " + this.simplePerson.getId(), Person.class).getSingleResult();
        person.getAddress();

        System.out.println("FIND CURRENT: " + person);
        return person;
    }

    public Person querySampleCurrentPersonJoinAddress(EntityManager em) {
        Person person = em.createQuery("SELECT p From Person p JOIN FETCH p.address WHERE p.id = " + this.simplePerson.getId(), Person.class).getSingleResult();
        person.getAddress();

        System.out.println("FIND CURRENT: " + person);

        return person;
    }

    public Person findSampleCurrentPerson(EntityManager em) {
        Person person = em.find(Person.class, this.simplePerson.getId());

        System.out.println("FIND CURRENT: " + person);

        return person;
    }

    public Person queryFutureEditionOfCurrentPersonAtBOT(EntityManager em) {
        em.setProperty(EFF_TS_PROPERTY, Effectivity.BOT);

        Person pEdition = em.createQuery("SELECT p From PersonEdition p WHERE p.cid = " + this.simplePerson.getId(), Person.class).getSingleResult();

        System.out.println("QUERY EDITION @ BOT: " + pEdition);

        return pEdition;
    }

    public Person queryFutureEditionOfCurrentPersonAtT1(EntityManager em) {
        em.setProperty(EFF_TS_PROPERTY, T1);

        Person pEdition = em.createQuery("SELECT p From PersonEdition p WHERE p.cid = " + this.simplePerson.getId(), Person.class).getSingleResult();

        System.out.println("QUERY EDITION @ T1: " + pEdition);

        return pEdition;
    }

    public Person queryFutureEditionOfCurrentPersonAtT2(EntityManager em) {
        em.setProperty(EFF_TS_PROPERTY, T2);

        Person pEdition = em.createQuery("SELECT p From PersonEdition p WHERE p.cid = " + this.simplePerson.getId(), Person.class).getSingleResult();

        System.out.println("QUERY EDITION @ T2: " + pEdition);

        return pEdition;
    }

    public Person queryFutureEditionOfCurrentPersonAtT2JoinFetchAddress(EntityManager em) {
        em.setProperty(EFF_TS_PROPERTY, T2);

        Person person = em.createQuery("SELECT p From PersonEdition p JOIN FETCH p.address WHERE p.cid = " + this.simplePerson.getId(), Person.class).getSingleResult();
        Address address = person.getAddress();

        System.out.println("QUERY EDITION @ T2: " + person);
        System.out.println("\t> " + address);

        return person;
    }

    public Person queryFutureEditionOfCurrentPersonAtT3(EntityManager em) {
        em.setProperty(EFF_TS_PROPERTY, T3);

        Person personEdition = em.createQuery("SELECT p From PersonEdition p WHERE p.cid = " + this.simplePerson.getId(), Person.class).getSingleResult();

        System.out.println("QUERY EDITION @ T3: " + personEdition);

        return personEdition;
    }

    public Person queryFutureEditionOfCurrentPersonAtT4(EntityManager em) {
        em.setProperty(EFF_TS_PROPERTY, T4);

        Person pEdition = em.createQuery("SELECT p From PersonEdition p WHERE p.cid = " + this.simplePerson.getId(), Person.class).getSingleResult();

        System.out.println("QUERY EDITION @ T4: " + pEdition);

        return pEdition;
    }

    public Person queryFutureEditionOfCurrentPersonAtT5(EntityManager em) {
        em.setProperty(EFF_TS_PROPERTY, T5);

        Person pEdition = em.createQuery("SELECT p From PersonEdition p WHERE p.cid = " + this.simplePerson.getId(), Person.class).getSingleResult();

        System.out.println("QUERY EDITION @ T5: " + pEdition);

        return pEdition;
    }

    public List<Person> nativeQueryForAllEdition(EntityManager em) {

        @SuppressWarnings("unchecked")
        List<Person> editions = em.createNativeQuery("SELECT * From TPERSON WHERE CID = " + this.simplePerson.getId(), Person.class).getResultList();

        System.out.println("QUERY ALL EDITIONS:");
        for (Person p : editions) {
            System.out.println("\t" + p);
        }

        return editions;
    }

}
