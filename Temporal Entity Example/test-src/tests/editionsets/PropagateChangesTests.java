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
import static example.PersonModelExample.SWIM;
import static example.PersonModelExample.T2;
import static example.PersonModelExample.T4;
import junit.framework.Assert;
import model.Address;
import model.Person;
import model.Phone;
import model.entities.PersonEntity;

import org.junit.Test;

import temporal.EditionSet;
import temporal.TemporalEntityManager;
import temporal.TemporalHelper;
import tests.BaseTestCase;
import example.PersonModelExample;

/**
 * Tests change propagation through future editions.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class PropagateChangesTests extends BaseTestCase {

    private static PersonModelExample example = new PersonModelExample();

    private Person getSample() {
        return example.fullPerson;
    }

    @Test
    public void makeBasicChangesAtT2Changes() {
        TemporalEntityManager em = getEntityManager();

        em.getTransaction().begin();

        EditionSet esT2 = em.setEffectiveTime( T2, true);
        Assert.assertNotNull(esT2);

        Person personT2 = em.find(Person.class, getSample().getId());

        Assert.assertNotNull(personT2);
        Assert.assertTrue(TemporalHelper.isEdition(em, personT2));
        Assert.assertEquals(T2, personT2.getEffectivity().getStart());

        personT2.setName("Jimster");
        personT2.setEmail("a@b.c");
        personT2.getAddress().setState("ONT");
        personT2.getAddress().setStreet(null);

        em.flush();
        em.clear();

        EditionSet esT4 = em.setEffectiveTime( T4, true);
        Assert.assertNotNull(esT4);

        Person personT4 = em.find(Person.class, getSample().getId());

        Assert.assertNotNull(personT4);
        Assert.assertTrue(TemporalHelper.isEdition(em, personT4));
        Assert.assertEquals(T4, personT4.getEffectivity().getStart());

        String nativeName = (String) em.createNativeQuery("SELECT P_NAMES FROM TPERSON WHERE CID = " + getSample().getId() + " AND START_TS = 400").getSingleResult();
        Assert.assertEquals("Jimbo", nativeName);
        Assert.assertEquals("Jimbo", personT4.getName());

        String nativeState = (String) em.createNativeQuery("SELECT STATE FROM TADDRESS WHERE CID = " + getSample().getAddress().getId() + " AND START_TS = 400").getSingleResult();
        Assert.assertEquals("ONT", nativeState);
        Assert.assertEquals("ONT", personT4.getAddress().getState());

        Assert.assertNull(personT4.getAddress().getStreet());
        Assert.assertNotNull(personT4.getEmail());
    }

    @Test
    public void makeBasicCollectionChangesAtT2Changes() {
        TemporalEntityManager em = getEntityManager();

        em.getTransaction().begin();

        EditionSet esT2 = em.setEffectiveTime( T2, true);
        Assert.assertNotNull(esT2);

        Person personT2 = em.find(Person.class, getSample().getId());

        Assert.assertNotNull(personT2);
        Assert.assertTrue(TemporalHelper.isEdition(em, personT2));
        Assert.assertEquals(T2, personT2.getEffectivity().getStart());

        em.persist(personT2.addHobby(example.hobbies.get(SWIM), T2));

        try {
            em.flush();
        } catch (UnsupportedOperationException e) {
            return;
        }
        Assert.fail("UnsupportedOperationException expected");

    }

    /**
     * Populate initial sample entity
     */
    @Override
    public void populate(TemporalEntityManager em) {
        System.out.println("\nEditionSetTests.populate:START");
        example.populateHobbies(em);
        em.persist(getSample());
        populateT2Editions(em);
        populateT4Editions(em);
        System.out.println("\nEditionSetTests.populate::DONE");
    }

    /**
     * Create the edition at T2 if it has not already been created
     */
    public Person populateT2Editions(TemporalEntityManager em) {
        EditionSet editionSet = em.setEffectiveTime( T2, true);
        Assert.assertNotNull(editionSet);

        Person personEditionT2 = em.find(Person.class, getSample().getId());

        if (personEditionT2.getEffectivity().getStart() != T2) {
            System.out.println("\nEditionSetTests.populateT2Edition:START");

            editionSet.setDescription("EditionSetTests::Person@T2");
            personEditionT2 = em.newEdition( personEditionT2);
            personEditionT2.setName("Jimmy");
            Address aT2 = em.newEdition( personEditionT2.getAddress());
            aT2.setCity("Toronto");
            aT2.setState("ON");
            personEditionT2.setAddress(aT2);
            Phone pT2 = em.newEdition( personEditionT2.getPhone("Home"));
            personEditionT2.addPhone(pT2);
            pT2.setNumber("222-222-2222");
            em.persist(personEditionT2.addHobby(example.hobbies.get(GOLF), T2));
            em.flush();

            System.out.println("\nEditionSetTests.populateT2Edition::DONE");
        }

        return personEditionT2;
    }

    /**
     * Create the edition at T2 if it has not already been created
     */
    public Person populateT4Editions(TemporalEntityManager em) {
        EditionSet editionSet = em.setEffectiveTime( T4, true);
        Assert.assertNotNull(editionSet);

        Person personEditionT4 = em.find(Person.class, getSample().getId());

        if (personEditionT4.getEffectivity().getStart() != T4) {
            System.out.println("\nEditionSetTests.populateT4Edition:START");

            editionSet.setDescription("EditionSetTests::Person@T4");
            personEditionT4 = em.newEdition( personEditionT4);
            personEditionT4.setName("Jimbo");
            Address aT4 = em.newEdition( personEditionT4.getAddress());
            aT4.setCity("Ottawa");
            personEditionT4.setAddress(aT4);
            Phone pT4 = em.newEdition( personEditionT4.getPhone("Home"));
            personEditionT4.addPhone(pT4);
            pT4.setNumber("444-444-4444");
            em.persist(personEditionT4.addHobby(example.hobbies.get(RUN), T4));
            em.flush();

            System.out.println("\nEditionSetTests.populateT4Edition::DONE");
        }

        return personEditionT4;
    }
}
