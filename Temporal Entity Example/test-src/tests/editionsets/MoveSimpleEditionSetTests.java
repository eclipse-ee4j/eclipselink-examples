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
import static example.PersonModelExample.T2;
import junit.framework.Assert;
import model.Address;
import model.Person;
import model.Phone;
import model.entities.PersonEntity;

import org.junit.Test;

import temporal.EditionSet;
import temporal.EditionSetEntry;
import temporal.TemporalEntityManager;
import tests.BaseTestCase;
import example.PersonModelExample;

/**
 * Tests applying a simple (no conflict) {@link EditionSet}
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class MoveSimpleEditionSetTests extends BaseTestCase {

    private static PersonModelExample example = new PersonModelExample();

    private Person getSample() {
        return example.fullPerson;
    }

    @Test
    public void verifyEditionSetAtT2() {
        TemporalEntityManager em = getEntityManager();

        em.getTransaction().begin();
        populateT2Editions(em);

        EditionSet es = em.setEffectiveTime( T2, true);

        Assert.assertNotNull(es);
        Assert.assertEquals(T2, es.getEffective());
        Assert.assertEquals(3, es.getEntries().size());

        for (EditionSetEntry entry : es.getEntries()) {
            System.out.println("> " + entry.getTemporal());
            for (String attrName : entry.getAttributes()) {
                System.out.println("\t>> " + attrName);
            }
        }
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
        EditionSet editionSet = em.setEffectiveTime( T2, true);
        Assert.assertNotNull(editionSet);

        Person personEditionT2 = em.find(PersonEntity.class, getSample().getId());

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

}
