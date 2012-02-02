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

import static example.PersonModelExample.T2;
import static example.PersonModelExample.T4;

import javax.persistence.EntityManager;

import model.Address;
import model.Person;
import model.Phone;

import org.junit.Assert;
import org.junit.Test;

import temporal.TemporalHelper;
import example.PersonModelExample;

/**
 * Make changes to the current (continuity) require that all future editions
 * which had the same value as the current be updated.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class ModifyCurrentTests extends BaseTestCase {

    private static PersonModelExample example = new PersonModelExample();

    private Person getSample() {
        return example.fullPerson;
    }

    @Test
    public void test() {
        getSample();
        Assert.fail("NOT YET IMPLEMENTED");
    }

    @Override
    public void populate(EntityManager em) {
        System.out.println("\nFullPersonWithEditions.populate:START");

        example.populateHobbies(em);
        em.persist(example.fullPerson);
        em.flush();

        System.out.println("\n> Create T2 Edition");
        TemporalHelper.setEffectiveTime(em, T2, true);

        Person fpEdition = TemporalHelper.find(em, Person.class, example.fullPerson.getId());
        Person personEditionT2 = TemporalHelper.createEdition(em, fpEdition);
        personEditionT2.setName("Jimmy");
        Address aT2 = TemporalHelper.createEdition(em, example.fullPerson.getAddress());
        aT2.setCity("Toronto");
        aT2.setState("ON");
        personEditionT2.setAddress(aT2);
        Phone pT2 = TemporalHelper.createEdition(em, example.fullPerson.getPhone("Home"));
        personEditionT2.addPhone(pT2);
        pT2.setNumber("222-222-2222");
        personEditionT2.addHobby(example.hobbies.get("golf")).getEffectivity().setStart(T2);
        em.flush();

        System.out.println("\n> Create T4 Edition");
        TemporalHelper.setEffectiveTime(em, T4, true);

        Person personEditionT4 = TemporalHelper.createEdition(em, personEditionT2);
        personEditionT4.setName("James");
        Address aT4 = TemporalHelper.createEdition(em, aT2);
        aT4.setCity("San Francisco");
        aT4.setState("CA");
        personEditionT4.setAddress(aT4);
        Phone pT4 = TemporalHelper.createEdition(em, pT2);
        pT4.setNumber("444-444-4444");
        personEditionT4.addPhone(pT4);
        personEditionT4.getPersonHobbies().get("Golf").getEffectivity().setEnd(T4);
        personEditionT4.addHobby(example.hobbies.get("running")).getEffectivity().setStart(T4);
        personEditionT4.addHobby(example.hobbies.get("skiing")).getEffectivity().setStart(T4);
        em.flush();

        System.out.println("\nFullPersonWithEditions.populate::DONE");
    }

}
