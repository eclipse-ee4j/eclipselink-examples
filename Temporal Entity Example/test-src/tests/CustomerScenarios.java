/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      dclarke - Bug 361016: Future Versions Examples
 ******************************************************************************/

package tests;

import static example.PersonModelExample.*;
import static temporal.TemporalHelper.newInstance;

import java.util.List;

import javax.persistence.EntityManager;

import junit.framework.Assert;
import model.Address;
import model.Person;
import model.Phone;
import model.entities.AddressEntity;
import model.entities.PhoneEntity;

import org.junit.Test;

import temporal.TemporalHelper;
/**
 * TODO
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class CustomerScenarios extends BaseTestCase {

    @Test
    public void createPersonAndAddressNow() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();

        Person p = newInstance(em, Person.class);
        p.setName("Now");
        Address a = new AddressEntity("now", "now", "now");
        p.setAddress(a);

        em.persist(p);

        em.getTransaction().commit();
        em.clear();
        em.getEntityManagerFactory().getCache().evictAll();
        System.out.println("\nREAD:\n");

        Person readP = em.find(Person.class, p.getId());

        Assert.assertEquals(p.getName(), readP.getName());
        Assert.assertEquals("now", readP.getAddress().getCity());
    }

    @Test
    public void createPersonAndAddressFuture() {
        EntityManager em = createEntityManager();
        TemporalHelper.setEffectiveTime(em, T2, true);

        em.getTransaction().begin();

        Person p = TemporalHelper.newInstance(em, Person.class);
        p.setName("Future");
        Address a = TemporalHelper.newInstance(em, Address.class);
        a.setCity("t2");
        p.setAddress(a);

        em.persist(p);

        em.getTransaction().commit();
        em.clear();
        em.getEntityManagerFactory().getCache().evictAll();
        System.out.println("\nREAD:\n");

        Person readP = em.createQuery("SELECT p FROM PersonEdition p WHERE p.cid = " + p.getId(), Person.class).getSingleResult();

        Assert.assertEquals(p.getName(), readP.getName());
        Assert.assertEquals("t2", readP.getAddress().getCity());
    }

    @Test
    public void createPersonInFutureWithOldAddress() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        Address a = new AddressEntity("now", "now", "now");
        em.persist(a);
        em.flush();

        TemporalHelper.setEffectiveTime(em, T2, true);

        Person p = TemporalHelper.newInstance(em, Person.class);
        p.setName("Future");
        p.setAddress(a);

        em.persist(p);

        em.getTransaction().commit();
        em.clear();
        em.getEntityManagerFactory().getCache().evictAll();
        System.out.println("\nREAD:\n");

        TemporalHelper.setEffectiveTime(em, T1, false);

        List<Person> results = em.createQuery("SELECT p FROM PersonEdition p WHERE p.address.city = 'now'", Person.class).getResultList();
        Assert.assertTrue(results.isEmpty());

        TemporalHelper.setEffectiveTime(em, T2, false);
        Person readP = em.createQuery("SELECT p FROM PersonEdition p JOIN FETCH p.address WHERE p.address.city = 'now'", Person.class).getSingleResult();

        Assert.assertNotNull(readP);
        Assert.assertNotNull(readP.getAddress());
        Assert.assertEquals(p.getName(), readP.getName());
        Assert.assertEquals("now", readP.getAddress().getCity());
    }

    @Test
    public void createPersonInFutureWithOldPhone() {
        EntityManager em = createEntityManager();

        em.getTransaction().begin();
        Phone phone_BOT = new PhoneEntity("work", "000-000-0000");
        em.persist(phone_BOT);
        em.flush();

        TemporalHelper.setEffectiveTime(em, T2, true);

        Person p = TemporalHelper.newInstance(em, Person.class);
        p.setName("Future");
        em.persist(p);

        TemporalHelper.setEffectiveTime(em, T3, true);

        Phone phone_T3 = TemporalHelper.createEdition(em, phone_BOT);
        phone_T3.setNumber("333-333-3333");
        p.addPhone(phone_T3);
        em.persist(phone_T3);
        em.getTransaction().commit();
        em.clear();
        em.getEntityManagerFactory().getCache().evictAll();

        System.out.println("\nREAD:\n");

        TemporalHelper.setEffectiveTime(em, T1);
        Person readP_T1 = TemporalHelper.find(em, Person.class, p.getId());
        System.out.println("Read Person @ T1: " + readP_T1);

        em.clear();
        em.getEntityManagerFactory().getCache().evictAll();

        TemporalHelper.setEffectiveTime(em, T2);
        Person readP_T2 = TemporalHelper.find(em, Person.class, p.getId());
        System.out.println("Read Person @ T2: " + readP_T2 + " Phone: " + readP_T2.getPhone("work"));
        Phone readPhone_T2 = TemporalHelper.find(em, Phone.class, phone_BOT.getId());
        System.out.println("Read Phone @ T2: " + readPhone_T2);

        em.clear();
        em.getEntityManagerFactory().getCache().evictAll();

        TemporalHelper.setEffectiveTime(em, T3);
        Person readP_T3 = TemporalHelper.find(em, Person.class, p.getId());
        System.out.println("Read Person @ T3: " + readP_T3 + " Phone: " + readP_T3.getPhone("work"));

        em.clear();
        em.getEntityManagerFactory().getCache().evictAll();

        TemporalHelper.setEffectiveTime(em, T4);
        Person readP_T4 = TemporalHelper.find(em, Person.class, p.getId());
        System.out.println("Read Person @ T4: " + readP_T4 + " Phone: " + readP_T4.getPhone("work"));

    }

    @Override
    public void populate(EntityManager em) {
    }

}
