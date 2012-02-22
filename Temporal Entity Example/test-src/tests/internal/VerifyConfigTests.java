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
package tests.internal;

import junit.framework.Assert;
import model.Address;
import model.Person;
import model.Phone;
import model.entities.AddressEntity;
import model.entities.PersonEntity;
import model.entities.PhoneEntity;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.weaving.PersistenceWeaved;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedChangeTracking;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedFetchGroups;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedLazy;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.Test;

import temporal.Effectivity;
import temporal.TemporalEdition;
import temporal.TemporalEntity;
import temporal.TemporalHelper;
import tests.BaseTestCase;

/**
 * Tests that verify the descriptors are setup as expected and that the
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class VerifyConfigTests extends BaseTestCase {

    @Test
    public void verifyDescriptorsExist() {
        Server session = JpaHelper.getServerSession(getEMF());

        Assert.assertNotNull(session);
        Assert.assertTrue(session.isConnected());
        Assert.assertEquals(18, session.getDescriptors().size());

        Assert.assertNotNull(session.getClassDescriptorForAlias("Person"));
        Assert.assertNotNull(session.getClassDescriptor(PersonEntity.class));
        Assert.assertNotNull(session.getClassDescriptorForAlias("Address"));
        Assert.assertNotNull(session.getClassDescriptor(AddressEntity.class));
        Assert.assertNotNull(session.getClassDescriptorForAlias("Phone"));
        Assert.assertNotNull(session.getClassDescriptor(PhoneEntity.class));
        Assert.assertNotNull(session.getClassDescriptor(Effectivity.class));

        Assert.assertNotNull(session.getClassDescriptorForAlias("Hobby"));
        Assert.assertNotNull(session.getClassDescriptorForAlias("PersonHobby"));
        Assert.assertNotNull(session.getClassDescriptorForAlias("EditionSet"));
        Assert.assertNotNull(session.getClassDescriptorForAlias("EditionSetEntry"));

        Assert.assertNotNull(session.getClassDescriptorForAlias("PersonEdition"));
        Assert.assertNotNull(session.getClassDescriptorForAlias("AddressEdition"));
        Assert.assertNotNull(session.getClassDescriptorForAlias("PhoneEdition"));

        Assert.assertNotNull(session.getClassDescriptorForAlias("PersonEditionView"));
        Assert.assertNotNull(session.getClassDescriptorForAlias("AddressEditionView"));
        Assert.assertNotNull(session.getClassDescriptorForAlias("PhoneEditionView"));
    }

    @Test
    public void verifyInterface() {
        Server session = JpaHelper.getServerSession(getEMF());

        Assert.assertSame(Person.class, session.getClassDescriptor(PersonEntity.class).getProperty(TemporalHelper.INTERFACE));
        Assert.assertSame(Person.class, session.getClassDescriptor(Person.class).getProperty(TemporalHelper.INTERFACE));
        Assert.assertSame(Address.class, session.getClassDescriptor(AddressEntity.class).getProperty(TemporalHelper.INTERFACE));
        Assert.assertSame(Address.class, session.getClassDescriptor(Address.class).getProperty(TemporalHelper.INTERFACE));
        Assert.assertSame(Phone.class, session.getClassDescriptor(PhoneEntity.class).getProperty(TemporalHelper.INTERFACE));
        Assert.assertSame(Phone.class, session.getClassDescriptor(Phone.class).getProperty(TemporalHelper.INTERFACE));
    }

    @Test
    public void verifyWeavingPerson() {
        Assert.assertTrue(PersistenceWeaved.class.isAssignableFrom(PersonEntity.class));
        Assert.assertTrue(PersistenceWeavedLazy.class.isAssignableFrom(PersonEntity.class));
        Assert.assertTrue(PersistenceWeavedChangeTracking.class.isAssignableFrom(PersonEntity.class));
        Assert.assertTrue(PersistenceWeavedFetchGroups.class.isAssignableFrom(PersonEntity.class));
        Assert.assertFalse(TemporalEdition.class.isAssignableFrom(PersonEntity.class));
    }

    @Test
    public void verifyWeavingAddress() {
        Assert.assertTrue(PersistenceWeaved.class.isAssignableFrom(AddressEntity.class));
        Assert.assertTrue(PersistenceWeavedLazy.class.isAssignableFrom(AddressEntity.class));
        Assert.assertTrue(PersistenceWeavedChangeTracking.class.isAssignableFrom(AddressEntity.class));
        Assert.assertTrue(PersistenceWeavedFetchGroups.class.isAssignableFrom(AddressEntity.class));
        Assert.assertFalse(TemporalEdition.class.isAssignableFrom(AddressEntity.class));
    }

    @Test
    public void verifyWeavingPhone() {
        Assert.assertTrue(PersistenceWeaved.class.isAssignableFrom(PhoneEntity.class));
        Assert.assertTrue(PersistenceWeavedLazy.class.isAssignableFrom(PhoneEntity.class));
        Assert.assertTrue(PersistenceWeavedChangeTracking.class.isAssignableFrom(PhoneEntity.class));
        Assert.assertTrue(PersistenceWeavedFetchGroups.class.isAssignableFrom(PhoneEntity.class));
        Assert.assertFalse(TemporalEdition.class.isAssignableFrom(PhoneEntity.class));
    }

    @Test
    public void verifyAttributeChangeTracking() {
        Server session = JpaHelper.getServerSession(getEMF());

        assertAttributeChangeTracking(session, "Person");
        assertAttributeChangeTracking(session, "Address");
        assertAttributeChangeTracking(session, "Phone");
    }

    @Test
    public void verifyEntityDescriptors() {
        Server session = JpaHelper.getServerSession(getEMF());

        for (String alias : new String[] { "Person", "Address", "Phone" }) {
            ClassDescriptor descriptor = session.getClassDescriptorForAlias(alias);

            Assert.assertNotNull(descriptor);
            Assert.assertNotNull(descriptor.getQueryManager().getAdditionalJoinExpression());
            Assert.assertFalse(descriptor.shouldBeReadOnly());
            Assert.assertFalse(descriptor.isIsolated());
            Assert.assertTrue(descriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy());

            Assert.assertEquals(1, descriptor.getPrimaryKeyFieldNames().size());
            Assert.assertEquals("OID", descriptor.getPrimaryKeyFields().get(0).getName());
        }
    }

    @Test
    public void verifyEditionDescriptors() {
        Server session = JpaHelper.getServerSession(getEMF());

        for (String alias : new String[] { "Person", "Address", "Phone" }) {
            ClassDescriptor descriptor = session.getClassDescriptorForAlias(alias + TemporalHelper.EDITION);

            Assert.assertNotNull(descriptor);
            Assert.assertNotNull(descriptor.getQueryManager().getAdditionalJoinExpression());
            Assert.assertFalse(descriptor.shouldBeReadOnly());
            Assert.assertTrue(descriptor.isIsolated());
            Assert.assertTrue(descriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy());

            Assert.assertEquals(1, descriptor.getPrimaryKeyFieldNames().size());
            Assert.assertEquals("OID", descriptor.getPrimaryKeyFields().get(0).getName());
        }
    }

    @Test
    public void verifyEditionViewDescriptors() {
        Server session = JpaHelper.getServerSession(getEMF());

        for (String alias : new String[] { "Person", "Address", "Phone" }) {
            ClassDescriptor descriptor = session.getClassDescriptorForAlias(alias + TemporalHelper.EDITION_VIEW);

            Assert.assertNotNull(descriptor);
            Assert.assertNull(descriptor.getQueryManager().getAdditionalJoinExpression());
            Assert.assertTrue(descriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy());
            Assert.assertTrue(descriptor.isIsolated());
            Assert.assertEquals(1, descriptor.getPrimaryKeyFieldNames().size());
            Assert.assertEquals("OID", descriptor.getPrimaryKeyFields().get(0).getName());
        }
    }

    private void assertAttributeChangeTracking(Session session, String alias) {
        ClassDescriptor descriptor = session.getClassDescriptorForAlias(alias);
        Assert.assertNotNull(descriptor);
        Assert.assertTrue(descriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy());

        if (TemporalEntity.class.isAssignableFrom(descriptor.getJavaClass()) && !descriptor.getAlias().endsWith(TemporalHelper.EDITION)) {
            assertAttributeChangeTracking(session, alias + TemporalHelper.EDITION);
        }
    }

    @Test
    public void verifyPersonPhonesDescriptor() {
        Server session = JpaHelper.getServerSession(getEMF());
        ClassDescriptor descriptor = session.getClassDescriptorForAlias("Person");

        Assert.assertNotNull(descriptor);
        Assert.assertEquals(PersonEntity.class, descriptor.getJavaClass());
        
        OneToManyMapping phonesMapping = (OneToManyMapping) descriptor.getMappingForAttributeName("phones");
        Assert.assertNotNull(phonesMapping);

        Assert.assertEquals("Phone", phonesMapping.getReferenceDescriptor().getAlias());
        Assert.assertTrue(phonesMapping.isCacheable());
        
        // TODO: Verify FK fields
    }

}
