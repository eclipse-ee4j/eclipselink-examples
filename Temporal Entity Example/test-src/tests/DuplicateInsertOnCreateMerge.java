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
import static example.PersonModelExample.T2;
import static example.PersonModelExample.T4;

import javax.persistence.EntityManager;

import junit.framework.Assert;
import model.Address;
import model.Person;
import model.Phone;
import model.entities.PersonEntity;

import org.junit.Test;

import temporal.TemporalHelper;
import example.PersonModelExample;

/**
 * Test cases dealing with potential duplicate insert scenarios
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class DuplicateInsertOnCreateMerge extends BaseTestCase {

    private static PersonModelExample example = new PersonModelExample();

    private Person getSample() {
        return example.fullPerson;
    }

    @Override
    public void populate(EntityManager em) {
        System.out.println("\nFullPersonWithEditions.populate:START");

        example.populateHobbies(em);
        em.persist(getSample());
        em.flush();

        System.out.println("\nFullPersonWithEditions.populate::DONE");
    }

    public Person createPersonEditionAtT2(EntityManager em) {
        TemporalHelper.setEffectiveTime(em, T2, true);

        Person fpEdition = em.find(PersonEntity.class, getSample().getId());
        Person personEditionT2 = fpEdition;

        if (personEditionT2.getEffectivity().getStart() != T2) {
            personEditionT2 = TemporalHelper.createEdition(em, fpEdition);
            personEditionT2.setName("Jimmy");
            Address aT2 = TemporalHelper.createEdition(em, fpEdition.getAddress());
            aT2.setCity("Toronto");
            aT2.setState("ON");
            personEditionT2.setAddress(aT2);
            Phone originalPhone = fpEdition.getPhone("Home");

            Phone pT2 = TemporalHelper.createEdition(em, originalPhone);
            personEditionT2.addPhone(pT2);
            pT2.setNumber("222-222-2222");
            em.persist(personEditionT2.addHobby(example.hobbies.get(GOLF), T2));
        } else {
            personEditionT2.getAddress();
            personEditionT2.getPhones().size();
        }
        return personEditionT2;
    }

    @Test
    public void createPersonAtT2AndMerge() {
        EntityManager em = createEntityManager();
        TemporalHelper.setEffectiveTime(em, T2, true);
        em.getTransaction().begin();

        Person personEditionT2 = createPersonEditionAtT2(em);

        Assert.assertNotNull(personEditionT2);
        Assert.assertEquals(T2, personEditionT2.getEffectivity().getStart());
        Assert.assertNotNull(personEditionT2.getPhone("Home"));

        // XXX
        em.merge(personEditionT2);

        em.getTransaction().commit();

        em.clear();
        personEditionT2 = em.find(Person.class, getSample().getId());

        Assert.assertNotNull(personEditionT2);
        Assert.assertEquals(T2, personEditionT2.getEffectivity().getStart());
        Phone pT2 = personEditionT2.getPhone("Home");
        Assert.assertNotNull(pT2);
    }

    @Test
    public void createPersonAtT4AndMerge() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        Person personEditionT2 = createPersonEditionAtT2(em);
        Assert.assertNotNull(personEditionT2);
        Assert.assertEquals(T2, personEditionT2.getEffectivity().getStart());
        Assert.assertNotNull(personEditionT2.getPhone("Home"));

        TemporalHelper.setEffectiveTime(em, T4, true);

        Person personEditionT4 = TemporalHelper.createEdition(em, personEditionT2);
        personEditionT4.setName("James");
        Address aT4 = TemporalHelper.createEdition(em, personEditionT4.getAddress());
        aT4.setCity("San Francisco");
        aT4.setState("CA");
        personEditionT4.setAddress(aT4);
        Phone pT4 = TemporalHelper.createEdition(em, personEditionT4.getPhone("Home"));
        pT4.setNumber("444-444-4444");
        personEditionT4.addPhone(pT4);
        personEditionT4.removeHobby(example.hobbies.get(GOLF), T4, T4);
        em.persist(personEditionT4.addHobby(example.hobbies.get(RUN), T4));
        em.persist(personEditionT4.addHobby(example.hobbies.get(SKI), T4));

        em.merge(personEditionT4);
        em.getTransaction().commit();
    }
}
