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

import static example.PersonModelExample.T2;
import static example.PersonModelExample.T4;

import java.lang.reflect.Proxy;

import junit.framework.Assert;
import model.Address;
import model.Person;
import model.Phone;
import model.entities.AddressEntity;
import model.entities.PersonEntity;
import model.entities.PhoneEntity;

import org.junit.Test;

import temporal.EditionWrapperHelper;
import temporal.TemporalEdition;
import temporal.TemporalEntity;
import temporal.TemporalEntityManager;
import example.PersonModelExample;

/**
 * Tests that verify the update of editions
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class ProxyWrapperUpdateTests extends BaseTestCase {

    private static PersonModelExample example = new PersonModelExample();

    @Test
    public void createWrapperForCurrent() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime( null, false);
        
        try {
            EditionWrapperHelper.wrap(em, new PersonEntity());
        } catch (IllegalArgumentException e) {
            return;
        }
        Assert.fail("IllegalArgumentException not thrown");
    }

    @Test
    public void createWrapperForPersonEditionWithoutEffectiveTS() {
        TemporalEntityManager em = getEntityManager();
        
        try {
            em.getTransaction().begin();
            Person tempPerson = em.newEntity( Person.class);
            em.persist(tempPerson);
            em.flush();
            
            EditionWrapperHelper.wrap(em, tempPerson);

        } catch (IllegalArgumentException e) {
            return;
        }

        Assert.fail("IllegalArgumentException expected");
    }

    @Test
    public void createWrapperForPersonEdition() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime( T4, true);

        em.getTransaction().begin();
        Person tempPerson = em.newEntity( Person.class);
        em.persist(tempPerson);
        em.flush();

        TemporalEntity<Person> wrapper = EditionWrapperHelper.wrap(em, tempPerson);

        Assert.assertNotNull(wrapper);
        Assert.assertTrue(wrapper instanceof Person);
        Assert.assertTrue(wrapper instanceof TemporalEdition);
    }

    @Test
    public void createWrapperForAddressEdition() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime( T4, true);

        em.getTransaction().begin();
        TemporalEntity<Address> wrapper = EditionWrapperHelper.wrap(em, em.newEntity( AddressEntity.class));

        Assert.assertNotNull(wrapper);
        Assert.assertTrue(wrapper instanceof Address);
        Assert.assertTrue(wrapper instanceof TemporalEdition);
        
        em.getTransaction().rollback();
    }

    @Test
    public void createWrapperForPhoneEdition() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime( T4, true);

        em.getTransaction().begin();
        TemporalEntity<Phone> wrapper = EditionWrapperHelper.wrap(em,em.newEntity( PhoneEntity.class));

        Assert.assertNotNull(wrapper);
        Assert.assertTrue(wrapper instanceof Phone);
        Assert.assertTrue(wrapper instanceof TemporalEdition);
        em.getTransaction().rollback();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void create2editionsUsingWrappers() {
        TemporalEntityManager em = getEntityManager();
        em.setEffectiveTime( T2, true);


        Person editionAtT2 = em.find(Person.class, example.fullPerson.getId());

        em.getTransaction().begin();
        Person wrappedPerson = (Person) EditionWrapperHelper.wrap(em, editionAtT2);

        Assert.assertTrue(Proxy.isProxyClass(wrappedPerson.getClass()));
        Assert.assertTrue(Proxy.isProxyClass(wrappedPerson.getAddress().getClass()));
        Assert.assertFalse(((TemporalEdition<Person>) wrappedPerson).hasChanges());

        wrappedPerson.setName(editionAtT2.getName() + "@T2");

        Assert.assertTrue(((TemporalEdition<Person>) wrappedPerson).hasChanges());

        em.getTransaction().commit();
        em.close();

        em = getEntityManager();
        em.setEffectiveTime( T4, true);

        Person editionAtT4 = em.find(Person.class, example.fullPerson.getId());

        em.getTransaction().begin();
        wrappedPerson = (Person) EditionWrapperHelper.wrap(em, editionAtT4);

        Assert.assertFalse(((TemporalEdition<Person>) wrappedPerson).hasChanges());

        wrappedPerson.setName(editionAtT4.getName() + "@T4");

        Assert.assertTrue(((TemporalEdition<Person>) wrappedPerson).hasChanges());

        em.getTransaction().commit();
    }

    @Override
    public void populate(TemporalEntityManager em) {
        em.persist(example.fullPerson);
    }
}
