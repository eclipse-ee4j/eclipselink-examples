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

package temporal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.Collection;

import javax.persistence.EntityManager;


import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;


/**
 * Helper class used to wrap and unwrap {@link TemporalEntity} with
 * {@link TemporalEdition} wrappers.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class EditionWrapperHelper {

    @SuppressWarnings("unchecked")
    public static <T> T wrap(EntityManager em, T entities) {

        if (entities instanceof Collection) {
            Collection<TemporalEntity<?>> source = (Collection<TemporalEntity<?>>) entities;
            Collection<TemporalEntity<?>> target = (Collection<TemporalEntity<?>>) newCollection(source);

            for (Object obj : source) {
                target.add((TemporalEntity<?>) wrap(em, obj));
            }
        }

        TemporalEntity<?> entity = (TemporalEntity<?>) entities;

        if (Proxy.isProxyClass(entity.getClass())) {
            return (T) entity;
        }

        if (entity.getId() <= 0) {
            throw new IllegalArgumentException("Cannot wrap new entity or edition (persist first)");
        }

        entity = TemporalHelper.getEdition(em, entity);

        if (entity == null) {
            throw new IllegalArgumentException("Edition does not exist for entity: " + entity);
        }

        RepeatableWriteUnitOfWork uow = em.unwrap(RepeatableWriteUnitOfWork.class);
        ClassLoader classLoader = uow.getPlatform().getConversionManager().getLoader();
        ClassDescriptor descriptor = uow.getClassDescriptor(entity);
        ClassDescriptor currentDesc = TemporalHelper.getCurrentDescriptor(uow, descriptor.getJavaClass());
        Class<?> wrapperInterface = (Class<?>) currentDesc.getProperty(TemporalHelper.INTERFACE);

        EditionWrapperHandler<?> handler = new EditionWrapperHandler<TemporalEntity<?>>(entity, em);
        return (T) Proxy.newProxyInstance(classLoader, new Class[] { wrapperInterface, TemporalEdition.class }, handler);
    }

    /**
     * Create a new collection. Used when wrapping and unwrapping collections to
     * ensure the result matches the source.
     * 
     * @param source
     * @return
     */
    private static Collection<?> newCollection(Collection<?> source) {
        try {
            @SuppressWarnings("unchecked")
            Constructor<Collection<?>> constructor = (Constructor<Collection<?>>) source.getClass().getConstructor(new Class[] { int.class });
            if (constructor == null) {
                return source.getClass().newInstance();
            }
            return constructor.newInstance(source.size());
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not create collection copy to wrap", e);
        }
    }

    /**
     * Unwrap the edition wrappers. If the wrapper contains
     * 
     * @param entities
     * @return unwrapped entity or collection of entities.
     * @throws IllegalArgumentException
     *             if any entity provided is not a {@link TemporalEntity}
     */
    @SuppressWarnings("unchecked")
    public static <T> T unwrap(T entities) {
        if (entities != null || entities instanceof Collection) {
            Collection<TemporalEntity<?>> source = (Collection<TemporalEntity<?>>) entities;
            Collection<TemporalEntity<?>> target = (Collection<TemporalEntity<?>>) newCollection(source);

            for (Object obj : source) {
                target.add((TemporalEntity<?>) unwrap(obj));
            }
        } else if (entities == null || !(entities instanceof TemporalEntity)) {
            throw new IllegalArgumentException("Unsupported: " + entities);
        }
        TemporalEntity<?> entity = (TemporalEntity<?>) entities;
        if (Proxy.isProxyClass(entity.getClass())) {
            EditionWrapperHandler<?> handler = (EditionWrapperHandler<?>) Proxy.getInvocationHandler(entity);
            entity = handler.getEntity();
        }
        return (T) entity;
    }

}
