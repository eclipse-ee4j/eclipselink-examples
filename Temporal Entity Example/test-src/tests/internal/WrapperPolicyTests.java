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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import junit.framework.Assert;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.sessions.server.Server;
import org.junit.Test;

import temporal.TemporalHelper;
import temporal.persistence.DescriptorHelper;
import temporal.persistence.EditionWrapperPolicy;
import tests.BaseTestCase;

/**
 * Verify the helper methods on {@link TemporalHelper}
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class WrapperPolicyTests extends BaseTestCase {

    @Test
    public void verifyConfig() {
        Server session = JpaHelper.getServerSession(getEMF());

        Assert.assertNotNull(session);

        for (String alias : new String[] { "Person", "Address", "Phone" }) {
            ClassDescriptor cd = session.getClassDescriptorForAlias(alias);
            Assert.assertNotNull(cd);
            Assert.assertTrue(cd.hasWrapperPolicy());
            Assert.assertTrue(cd.getWrapperPolicy() instanceof EditionWrapperPolicy);

            cd = session.getClassDescriptorForAlias(alias + DescriptorHelper.EDITION);
            Assert.assertNotNull(cd);
            Assert.assertTrue(cd.hasWrapperPolicy());
            Assert.assertTrue(cd.getWrapperPolicy() instanceof EditionWrapperPolicy);
        }
    }

    @Test
    public void createEntityAndDescriptorLookupUsingProxy() {
        Server session = JpaHelper.getServerSession(getEMF());
        Assert.assertNotNull(session);

        for (String alias : new String[] { "Person", "Address", "Phone" }) {
            ClassDescriptor cd = session.getClassDescriptorForAlias(alias);
            Assert.assertNotNull(cd);
            
            Object entity = cd.getInstantiationPolicy().buildNewInstance();
            Assert.assertNotNull(entity);
            Assert.assertFalse(cd.getWrapperPolicy().isWrapped(entity));
            
            Object wrappedEnObject = cd.getWrapperPolicy().wrapObject(entity, (AbstractSession) session);
            
            Assert.assertNotNull(wrappedEnObject);
            Assert.assertTrue(cd.getWrapperPolicy().isWrapped(wrappedEnObject));
            Assert.assertTrue(Proxy.isProxyClass(wrappedEnObject.getClass()));
            InvocationHandler handler = Proxy.getInvocationHandler(wrappedEnObject);
            Assert.assertTrue(handler instanceof EditionWrapperPolicy.Handler<?>);
            
            ClassDescriptor lookupCD = session.getClassDescriptor(wrappedEnObject);
            Assert.assertNotNull(lookupCD);
            Assert.assertSame(cd, lookupCD);
        }
    }

    @Test
    public void createEditionAndDescriptorLookupUsingProxy() {
        Server session = JpaHelper.getServerSession(getEMF());
        Assert.assertNotNull(session);

        for (String alias : new String[] { "Person", "Address", "Phone" }) {
            ClassDescriptor cd = session.getClassDescriptorForAlias(alias+ DescriptorHelper.EDITION);
            Assert.assertNotNull(cd);
            
            Object entity = cd.getInstantiationPolicy().buildNewInstance();
            Assert.assertNotNull(entity);
            Assert.assertFalse(cd.getWrapperPolicy().isWrapped(entity));
            
            Object wrappedEnObject = cd.getWrapperPolicy().wrapObject(entity, (AbstractSession) session);
            
            Assert.assertNotNull(wrappedEnObject);
            Assert.assertTrue(Proxy.isProxyClass(wrappedEnObject.getClass()));
            InvocationHandler handler = Proxy.getInvocationHandler(wrappedEnObject);
            Assert.assertTrue(handler instanceof EditionWrapperPolicy.Handler<?>);
            
            ClassDescriptor lookupCD = session.getClassDescriptor(wrappedEnObject);
            Assert.assertNotNull(lookupCD);
            Assert.assertSame(cd, lookupCD);
        }
    }
}
