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

import static example.PersonModelExample.T6;

import javax.persistence.EntityManager;

import junit.framework.Assert;
import model.Person;
import model.entities.PersonEntity;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.VariableOneToOneMapping;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.Test;

import temporal.EditionSet;
import temporal.TemporalHelper;
import tests.BaseTestCase;
import example.PersonModelExample;

/**
 * Tests verifying the {@link EditionSet} capabilities.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class EditionSetConfigTests extends BaseTestCase {

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

    /**
     * Verify that newInstance fails if the EditionSet has not been initialized
     */
    @Test
    public void verifySetEffectiveProperty_newInstance() {
        EntityManager em = createEntityManager();
        em.setProperty(TemporalHelper.EFF_TS_PROPERTY, T6);

        EditionSet es = TemporalHelper.getEditionSet(em);

        Assert.assertNull("EditionSet initialized when it should not have been", es);
        Assert.assertEquals(T6, (long) TemporalHelper.getEffectiveTime(em));

        try {
            TemporalHelper.newInstance(em, PersonEntity.class);
        } catch (IllegalStateException e) {
            return;
        }
        Assert.fail("IllegalStateException expected");
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


    /**
     * Populate initial sample entity
     */
    @Override
    public void populate(EntityManager em) {
        System.out.println("\nEditionSetTests.populate:START");
        example.populateHobbies(em);
        em.persist(getSample());
        System.out.println("\nEditionSetTests.populate::DONE");
    }

}
