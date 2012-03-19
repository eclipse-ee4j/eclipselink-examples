/*******************************************************************************
 * Copyright (c) 2011-2012 Oracle. All rights reserved.
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
package temporal.persistence;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Member;
import java.lang.reflect.Proxy;

import javax.persistence.EntityManager;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.descriptors.InstanceVariableAttributeAccessor;
import org.eclipse.persistence.internal.descriptors.MethodAttributeAccessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Session;

import temporal.BaseEntity;
import temporal.TemporalEntity;
import temporal.TemporalHelper;

/**
 * This helper is used in to configure and access the temporal values of an
 * {@link EntityManager} and its managed entities.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class DescriptorHelper {

    /**
     * Property name used to cache current descriptor on edition and edition
     * view descriptors
     */
    public static final String CURRENT = "Current";

    /**
     * Entity type name prefix prepended to current entity type
     */
    public static final String EDITION = "Edition";

    /**
     * Entity type name prefix prepended to current entity type
     */
    public static final String EDITION_VIEW = "EditionView";

    /**
     * Copy mapped value from source to new edition. This copies the real
     * attribute value.
     */
    protected static void copyValue(AbstractSession session, DatabaseMapping mapping, TemporalEntity<?> source, TemporalEntity<?> target) {
        if (mapping.getAttributeName().equals("effectivity")) {
            return;
        }

        String nonTemporal = (String) mapping.getProperty(TemporalHelper.NON_TEMPORAL);
        if (nonTemporal != null && Boolean.valueOf(nonTemporal)) {
            return;
        }

        Member member = null;

        if (mapping.getAttributeAccessor().isInstanceVariableAttributeAccessor()) {
            member = ((InstanceVariableAttributeAccessor) mapping.getAttributeAccessor()).getAttributeField();
        } else {
            member = ((MethodAttributeAccessor) mapping.getAttributeAccessor()).getGetMethod();
        }
        if (member.getDeclaringClass().equals(BaseEntity.class)) {
            return;
        }

        Object value = mapping.getRealAttributeValueFromObject(source, session);
        mapping.setRealAttributeValueInObject(target, value);
    }

    private static ClassDescriptor getDescriptor(Session session, Class<?> entityClass, String type) {
        ClassDescriptor desc = session.getClassDescriptor(entityClass);
        if (desc == null) {
            throw new IllegalArgumentException("Non-persistent type: " + entityClass);
        }
        return (ClassDescriptor) desc.getProperty(type);
    }

    public static ClassDescriptor getCurrentDescriptor(Session session, Class<?> entityClass) {
        return getDescriptor(session, entityClass, CURRENT);
    }

    public static ClassDescriptor getEditionDescriptor(Session session, Class<?> entityClass) {
        return getDescriptor(session, entityClass, EDITION);
    }
    
    /**
     * TODO
     */
    public static ClassDescriptor getClassDescriptor(Session session, Object entity) {
        Object domainObject = entity;
        
        if (Proxy.isProxyClass(entity.getClass())) {
            InvocationHandler handler = Proxy.getInvocationHandler(entity);
            
            if (handler instanceof EditionWrapperPolicy.Handler<?>) {
                domainObject = ((EditionWrapperPolicy.Handler<?>) handler).getEntity();
            }
            else            if (handler instanceof CurrentWrapperPolicy.CurrentWrapperHandler) {
                domainObject = ((CurrentWrapperPolicy.CurrentWrapperHandler<?>) handler).getEntity();
            }
        }
        return session.getClassDescriptor(domainObject);
    }

}
