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
package tests.editionsets;

import static example.PersonModelExample.GOLF;
import static example.PersonModelExample.RUN;
import static example.PersonModelExample.SKI;
import static example.PersonModelExample.T2;
import static example.PersonModelExample.T4;
import static example.PersonModelExample.T5;

import java.util.List;

import javax.persistence.Temporal;

import junit.framework.Assert;
import model.Address;
import model.Person;
import model.PersonHobby;
import model.Phone;
import model.entities.PersonEntity;

import org.junit.Test;

import temporal.EditionSet;
import temporal.EditionSetEntry;
import temporal.TemporalEntityManager;
import temporal.TemporalHelper;
import tests.BaseTestCase;
import example.PersonModelExample;

/**
 * Tests verifying the {@link EditionSet} capabilities.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class DeleteEditionSetTests extends BaseTestCase {

    private static PersonModelExample example = new PersonModelExample();

    private Person getSample() {
        return example.fullPerson;
    }

    @Test
    public void verifyEditionSetAtT2() {
        TemporalEntityManager em = getEntityManager();

        em.getTransaction().begin();
        populateT2Editions(em);

        EditionSet es = em.setEffectiveTime(T2, true);

        Assert.assertNotNull(es);
        Assert.assertEquals(T2, es.getEffective());
        Assert.assertEquals(4, es.getEntries().size());

        Assert.assertTrue(es.getEntries().get(0).getTemporal() instanceof Person);
        Person p = (Person) es.getEntries().get(0).getTemporal();
        Assert.assertEquals(T2, p.getEffectivity().getStart());

        Assert.assertTrue(es.getEntries().get(1).getTemporal() instanceof Address);
        Address a = (Address) es.getEntries().get(1).getTemporal();
        Assert.assertEquals(T2, a.getEffectivity().getStart());

        Assert.assertTrue(es.getEntries().get(2).getTemporal() instanceof Phone);
        Phone phone = (Phone) es.getEntries().get(2).getTemporal();
        Assert.assertEquals(T2, phone.getEffectivity().getStart());

        Assert.assertTrue(es.getEntries().get(3).getTemporal() instanceof PersonHobby);
        PersonHobby ph = (PersonHobby) es.getEntries().get(3).getTemporal();
        Assert.assertEquals(T2, ph.getEffectivity().getStart());
        Assert.assertSame(p, ph.getPerson());
        Assert.assertEquals(PersonModelExample.GOLF, ph.getName());
        Assert.assertEquals(PersonModelExample.GOLF, ph.getHobby().getName());

        Assert.assertEquals(1, p.getPersonHobbies().size());
        
    }

    @Test
    public void verifyEditionSetAtT4() {
        TemporalEntityManager em = getEntityManager();
        em.getTransaction().begin();

        populateT4Editions(em);

        List<EditionSet> editionSets = em.createQuery("SELECT e FROM EditionSet e ORDER BY e.effective", EditionSet.class).getResultList();

        Assert.assertNotNull(editionSets);
        Assert.assertEquals("Incorrect number of EditionSets found.", 2, editionSets.size());

        EditionSet t1 = editionSets.get(0);
        Assert.assertNotNull(t1);
        Assert.assertEquals(T2, t1.getEffective());

        EditionSet t2 = editionSets.get(1);
        Assert.assertNotNull(t2);
        Assert.assertEquals(T4, t2.getEffective());
    }

    /**
     * Verify that the addition of a {@link Temporal} value in a 1:M collection
     * causes an EditionSetEntry to be created.
     */
    @Test
    public void addHobbyAtT5WithInitializedEditionSet() {
        TemporalEntityManager em = getEntityManager();
        EditionSet es = em.setEffectiveTime(T5, true);

        Assert.assertNotNull(es);

        Person person = em.find(Person.class, getSample().getId());
        Assert.assertNotNull(person);
        Assert.assertTrue(TemporalHelper.isTemporalEntity(person));
        Assert.assertEquals(T5, es.getEffective());

        PersonHobby runHobby = em.newTemporal(PersonHobby.class);
        runHobby.setHobby(example.hobbies.get(RUN));
        person.addHobby(runHobby);

        Assert.assertEquals(1, es.getEntries().size());

        EditionSetEntry entry = es.getEntries().get(0);

        Assert.assertTrue(entry.getTemporal() instanceof PersonHobby);
    }

    /**
     * Populate initial sample entity
     */
    @Override
    public void populate(TemporalEntityManager em) {
        System.out.println("\nEditionSetTests.populate:START");
        example.populateHobbies(em);
        em.persist(getSample());
        System.out.println("\nEditionSetTests.populate::DONE");
    }

    /**
     * Create the edition at T2 if it has not already been created
     */
    public Person populateT2Editions(TemporalEntityManager em) {
        EditionSet editionSet = em.setEffectiveTime(T2, true);
        Assert.assertNotNull(editionSet);

        Person personEditionT2 = em.find(Person.class, getSample().getId());

        if (personEditionT2.getEffectivity().getStart() != T2) {
            System.out.println("\nEditionSetTests.populateT2Edition:START");

            editionSet.setDescription("EditionSetTests::Person@T2");
            personEditionT2 = em.newEdition(personEditionT2);
            personEditionT2.setName("Jimmy");
            Address aT2 = em.newEdition(personEditionT2.getAddress());
            aT2.setCity("Toronto");
            aT2.setState("ON");
            personEditionT2.setAddress(aT2);
            Phone pT2 = em.newEdition(personEditionT2.getPhone("Home"));
            personEditionT2.addPhone(pT2);
            pT2.setNumber("222-222-2222");

            PersonHobby golfHobby = em.newTemporal(PersonHobby.class);
            golfHobby.setHobby(example.hobbies.get(GOLF));
            personEditionT2.addHobby(golfHobby);

            em.flush();

            System.out.println("\nEditionSetTests.populateT2Edition::DONE");
        }

        return personEditionT2;
    }

    public Person populateT4Editions(TemporalEntityManager em) {
        populateT2Editions(em);
        
        EditionSet editionSet = em.setEffectiveTime(T4, true);
        Assert.assertNotNull(editionSet);

        Person personEditionT4 = em.find(Person.class, getSample().getId());

        if (personEditionT4 == null) {
            System.out.println("\nEditionSetTests.populateT4Edition:START");
            em.initializeEditionSet().setDescription("EditionSetTests::Person@T4");
            personEditionT4 = em.newEdition(personEditionT4);
            personEditionT4.setName("James");
            Address aT4 = em.newEdition(personEditionT4.getAddress());
            aT4.setCity("San Francisco");
            aT4.setState("CA");
            personEditionT4.setAddress(aT4);
            Phone pT4 = em.newEdition(personEditionT4.getPhone("Home"));
            pT4.setNumber("444-444-4444");
            personEditionT4.addPhone(pT4);
            personEditionT4.getPersonHobbies().get(GOLF).getEffectivity().setEnd(T4);

            PersonHobby runHobby = em.newTemporal(PersonHobby.class);
            runHobby.setHobby(example.hobbies.get(RUN));
            personEditionT4.addHobby(runHobby);

            PersonHobby skiHobby = em.newTemporal(PersonHobby.class);
            skiHobby.setHobby(example.hobbies.get(SKI));
            personEditionT4.addHobby(skiHobby);

            em.flush();

            System.out.println("\nEditionSetTests.populateT4Edition:DONE");
        }

        return personEditionT4;
    }
}
