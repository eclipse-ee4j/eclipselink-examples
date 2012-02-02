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
import model.Address;
import model.Person;
import model.Phone;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.VariableOneToOneMapping;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.Test;

import temporal.EditionSet;
import temporal.EditionSetEntry;
import temporal.TemporalEntity;
import temporal.TemporalHelper;
import example.PersonModelExample;

/**
 * Tests verifying the {@link EditionSet} capabilities.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class EditionSetTests extends BaseTestCase {

    private static PersonModelExample example = new PersonModelExample();

    private Person getSample() {
        return example.fullPerson;
    }

    @Test
    public void verifyEditionSetMapping() {
        EntityManager em = createEntityManager();

        ClassDescriptor desc = em.unwrap(Server.class).getClassDescriptorForAlias("EditionSet");
        Assert.assertNotNull(desc);

        OneToManyMapping otmMapping = (OneToManyMapping) desc.getMappingForAttributeName("entries");
        Assert.assertNotNull(otmMapping);
    }

    @Test
    public void verifyEditionSetEntryMapping() {
        EntityManager em = createEntityManager();

        ClassDescriptor desc = em.unwrap(Server.class).getClassDescriptorForAlias("EditionSetEntry");
        Assert.assertNotNull(desc);

        VariableOneToOneMapping votoMapping = (VariableOneToOneMapping) desc.getMappingForAttributeName("edition");
        Assert.assertNotNull(votoMapping);
    }

    @Test
    public void verifySetEffectiveInitialize() {
        EntityManager em = createEntityManager();

        TemporalHelper.setEffectiveTime(em, T6, true);

        EditionSet es = TemporalHelper.getEditionSet(em); 

        Assert.assertNotNull(es);
        Assert.assertEquals(T6, es.getEffective());
    }

    @Test
    public void verifyInitialize() {
        EntityManager em = createEntityManager();

        TemporalHelper.setEffectiveTime(em, T6);
        TemporalHelper.initializeEditionSet(em);

        EditionSet es = TemporalHelper.getEditionSet(em); 

        Assert.assertNotNull(es);
        Assert.assertEquals(T6, es.getEffective());
    }

    @Test
    public void verifyNotInitialized() {
        EntityManager em = createEntityManager();

        TemporalHelper.setEffectiveTime(em, T6);

        EditionSet es = TemporalHelper.getEditionSet(em); 

        Assert.assertNull(es);
    }

    @Test
    public void verifyPopulateCreatedTasks() {
        EntityManager em = createEntityManager();

        List<EditionSet> editionSets = em.createQuery("SELECT e FROM EditionSet e ORDER BY e.effective", EditionSet.class).getResultList();

        Assert.assertNotNull(editionSets);
        Assert.assertEquals("Incorrect number of tasks found.", 2, editionSets.size());

        EditionSet t1 = editionSets.get(0);
        Assert.assertNotNull(t1);
        Assert.assertEquals(T2, t1.getEffective());

        EditionSet t2 = editionSets.get(1);
        Assert.assertNotNull(t2);
        Assert.assertEquals(T4, t2.getEffective());
    }

    @Test
    public void verifyEditionSetAtT2() {
        EntityManager em = createEntityManager();
        EditionSet es = TemporalHelper.setEffectiveTime(em, T2, true);
        
        em.getTransaction().begin();

        Assert.assertNotNull(es);
        Assert.assertEquals(T2, es.getEffective());
        Assert.assertEquals(3, es.getEntries().size());
        
        for (EditionSetEntry entry: es.getEntries()) {
            System.out.println("> " + entry.getEdition());
            for (String attrName: entry.getAttributes()) {
                System.out.println("\t>> " + attrName);
            }
        }
    }

    @Test
    public void verifyEditionSetAtT4() {
        EntityManager em = createEntityManager();
        TemporalHelper.setEffectiveTime(em, T2, true);

        List<EditionSet> editionSets = em.createQuery("SELECT e FROM EditionSet e ORDER BY e.effective", EditionSet.class).getResultList();

        Assert.assertNotNull(editionSets);
        Assert.assertEquals("Incorrect number of tasks found.", 2, editionSets.size());

        EditionSet t1 = editionSets.get(0);
        Assert.assertNotNull(t1);
        Assert.assertEquals(T2, t1.getEffective());

        EditionSet t2 = editionSets.get(1);
        Assert.assertNotNull(t2);
        Assert.assertEquals(T4, t2.getEffective());
    }

    @Override
    public void populate(EntityManager em) {
        System.out.println("\nFullPersonWithEditions.populate:START");

        example.populateHobbies(em);
        em.persist(getSample());
        em.flush();

        System.out.println("\n> Create T2 Edition");
        TemporalHelper.setEffectiveTime(em, T2);
        TemporalHelper.initializeEditionSet(em).setDescription("EditionSetTests::Person@T2");
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
        personEditionT2.addHobby(example.hobbies.get(GOLF)).getEffectivity().setStart(T2);
        em.flush();

        System.out.println("\n> Create T4 Edition");
        TemporalHelper.setEffectiveTime(em, T4);
        TemporalHelper.initializeEditionSet(em).setDescription("EditionSetTests::Person@T4");
        Person personEditionT4 = TemporalHelper.createEdition(em, personEditionT2);
        personEditionT4.setName("James");
        Address aT4 = TemporalHelper.createEdition(em, aT2);
        aT4.setCity("San Francisco");
        aT4.setState("CA");
        personEditionT4.setAddress(aT4);
        Phone pT4 = TemporalHelper.createEdition(em, pT2);
        pT4.setNumber("444-444-4444");
        personEditionT4.addPhone(pT4);
        personEditionT4.getPersonHobbies().get(GOLF).getEffectivity().setEnd(T4);
        personEditionT4.addHobby(example.hobbies.get(RUN)).getEffectivity().setStart(T4);
        personEditionT4.addHobby(example.hobbies.get(SKI)).getEffectivity().setStart(T4);
        em.flush();

        System.out.println("\nFullPersonWithEditions.populate::DONE");
    }

}
