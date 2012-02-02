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
package temporal;

import java.lang.reflect.Member;
import java.util.Collection;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.descriptors.InstanceVariableAttributeAccessor;
import org.eclipse.persistence.internal.descriptors.MethodAttributeAccessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Session;

import temporal.persistence.TemporalQueryRedirector;

/**
 * This helper is used in to configure and access the temporal values of an
 * {@link EntityManager} and its managed entities.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class TemporalHelper {

    /**
     * Entity type name prefix prepended to current entity type
     */
    public static final String EDITION = "Edition";

    /**
     * Entity type name prefix prepended to current entity type
     */
    public static final String EDITION_VIEW = "EditionView";

    /**
     * Property named used to specify the current effective time for all queries
     * run in the current persistence context.
     */
    public static final String EFF_TS_PROPERTY = "EFF_TS";

    /**
     * Property name to store the active {@link EditionSet}within the
     * {@link EntityManager}.
     */
    public static final String EDITION_SET_PROPERTY = EditionSet.class.getName();

    /**
     * Property named used to store {@link EntityManager} within properties so
     * that it is accessible within {@link TemporalQueryRedirector}
     */
    public static final String ENTITY_MANAGER = EntityManager.class.getName();

    /**
     * 
     */
    public static final String INTERFACE = TemporalEntity.class.getName();

    public static EditionSet setEffectiveTime(EntityManager em, Long startTime, boolean initializeEditionSet) {
        em.setProperty(ENTITY_MANAGER, em);
        if (startTime != null) {
            em.setProperty(EFF_TS_PROPERTY, startTime);
        }
        EditionSet editionSet = getEditionSet(em);
        if (editionSet != null && editionSet.getEffective() != startTime) {
            em.setProperty(EDITION_SET_PROPERTY, null);
        }
        if (initializeEditionSet) {
            return initializeEditionSet(em);
        }
        return null;
    }

    public static EditionSet setEffectiveTime(EntityManager em, Long startTime) {
        return setEffectiveTime(em, startTime, false);
    }

    public static void clearEffectiveTime(EntityManager em) {
        RepeatableWriteUnitOfWork uow = em.unwrap(RepeatableWriteUnitOfWork.class);
        uow.getParent().getProperties().remove(EFF_TS_PROPERTY);
    }

    public static Long getEffectiveTime(EntityManager em) {
        AbstractSession session = em.unwrap(RepeatableWriteUnitOfWork.class);
        return (Long) session.getProperty(EFF_TS_PROPERTY);
    }

    public static boolean hasEffectiveTime(EntityManager em) {
        return getEffectiveTime(em) != null;
    }

    /**
     * Create a new edition based on the previous edition (could be current or
     * future continuity as well). All values from the provided
     * {@link BaseEntity} are copied into the new one and if the start time is
     * provided both the edition and previous version have their start and end
     * times set accordingly.
     * <p>
     * Note: This method currently does not validate for date range conflicts.
     * It simply adds the next edition assuming the one passed in was correct an
     * no other future editions exist that will conflict with this addition.
     * 
     * @param em
     *            {@link EntityManager} within a transaction
     * @param continuityOrEdition
     *            the {@link BaseEntity} to base this new edition on.
     * @param start
     *            the time this new edition should start at. This value is set
     *            on the new edition and used as the end on the provided entity.
     *            If null the times are not left as per TemporalEntity's
     *            constructor.
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends TemporalEntity<?>> T createEdition(EntityManager em, T source) {
        AbstractSession session = em.unwrap(RepeatableWriteUnitOfWork.class);
        Long start = (Long) session.getProperty(EFF_TS_PROPERTY);

        if (start == null || start == Effectivity.BOT) {
            throw new IllegalStateException("Cannot create an eddition without an effective time set");
        }

        ClassDescriptor editionDesc = getEditionDescriptor(session, source.getClass());

        if (editionDesc == null) {
            throw new IllegalArgumentException("No edition descriptor for: " + source);
        }

        // Create a task if none exists
        EditionSet editionSet = getEditionSet(em);
        if (editionSet == null) {
            throw new IllegalStateException("No EditionSet associated with this EntityManager");
        }

        TemporalEntity<T> edition = (TemporalEntity<T>) editionDesc.getInstantiationPolicy().buildNewInstance();
        edition.setContinuity((T) source.getContinuity());

        // Copy the mapped values from source to new edition
        for (DatabaseMapping mapping : editionDesc.getMappings()) {
            if (!mapping.getAttributeName().equals("effectivity")) {
                copyValue(session, mapping, source, edition);
            }
        }

        edition.getEffectivity().setStart(start);
        edition.getEffectivity().setEnd(source.getEffectivity().getEnd());
        source.getEffectivity().setEnd(start);
        em.persist(edition);

        editionSet.add(edition);

        return (T) edition;
    }

    /**
     * Create a new entity that is planned to exist at some future time. The
     * entity created will have a start time greater than
     * {@link TemporalEntity#BOT} (beginning of time) and will be the continuity
     * as well. The start team is specified by the effective time of the
     * {@link EntityManager} specified by its {@value #EFF_TS_PROPERTY}
     * property.
     * 
     * @param em
     *            {@link EntityManager} to persist the new edition into
     * @param entityClass
     *            the edition or current class
     * @return the new edition entity
     */
    @SuppressWarnings("unchecked")
    public static <T extends TemporalEntity<?>> T newInstance(EntityManager em, Class<T> entityClass) {
        AbstractSession session = em.unwrap(RepeatableWriteUnitOfWork.class);
        Long start = (Long) session.getProperty(EFF_TS_PROPERTY);
        ClassDescriptor descriptor = session.getClassDescriptor(entityClass);

        if (start != null) {
            descriptor = getEditionDescriptor(em.unwrap(Session.class), (Class<TemporalEntity<T>>) entityClass);
        }
        if (descriptor == null) {
            throw new IllegalArgumentException("No descriptor for: " + entityClass);
        }

        TemporalEntity<T> edition = (TemporalEntity<T>) descriptor.getInstantiationPolicy().buildNewInstance();
        edition.setContinuity((T) edition);
        if (start != null) {
            edition.getEffectivity().setStart(start);
        }
        em.persist(edition);
        return (T) edition;
    }

    /**
     * Copy mapped value from source to new edition. This copies the real
     * attribute value.
     */
    private static void copyValue(AbstractSession session, DatabaseMapping mapping, TemporalEntity<?> source, TemporalEntity<?> target) {
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

    /**
     * TODO
     */
    // TODO: Need better way to link edition descriptor to current
    @SuppressWarnings("unchecked")
    public static ClassDescriptor getCurrentDescriptor(Session session, Class<?> entityClass) {
        ClassDescriptor desc = session.getClassDescriptor(entityClass);
        if (isEditionClass(desc.getJavaClass())) {
            String currentAlias = desc.getAlias().substring(0, desc.getAlias().indexOf(EDITION));
            desc = session.getClassDescriptorForAlias(currentAlias);
        }
        return desc;
    }

    /**
     * TODO
     */
    @SuppressWarnings("unchecked")
    protected static ClassDescriptor getEditionDescriptor(Session session, Class<?> entityClass) {
        ClassDescriptor desc = session.getClassDescriptor(entityClass);
        if (desc == null || !isEditionClass(desc.getJavaClass())) {
            desc = session.getClassDescriptorForAlias(desc.getAlias() + EDITION);
        }
        return desc;
    }

    /**
     * @return <code>true</code> if this is an instance of the edition subclass.
     */
    public static boolean isEdition(EntityManager em, TemporalEntity<?> entity) {
        ClassDescriptor descriptor = getEditionDescriptor(em.unwrap(Session.class), entity.getClass());
        return descriptor != null && descriptor.getJavaClass() == entity.getClass();
    }

    /**
     * Get an edition for a {@link TemporalEntity} handling it being a current
     * or edition.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends TemporalEntity<?>> T getEdition(EntityManager em, T entity) {
        ClassDescriptor descriptor = getEditionDescriptor(em.unwrap(Session.class), entity.getClass());

        if (descriptor == null) {
            throw new IllegalArgumentException("No edition descriptor found for: " + entity);
        }

        if (descriptor.getJavaClass() == entity.getClass()) {
            return entity;
        }
        if (entity.getContinuity() == null) {
            throw new IllegalArgumentException("Entity has no continuity");
        }
        if (entity.getEffectivity() == null) {
            throw new IllegalArgumentException("Entity has no effectivity");
        }

        Object originalValue = em.getProperties().get(EFF_TS_PROPERTY);
        if (originalValue == null) {
            em.setProperty(EFF_TS_PROPERTY, Effectivity.BOT);
        }
        try {
            return (T) find(em, descriptor.getJavaClass(), entity.getContinuity().getId());
        } finally {
            em.setProperty(EFF_TS_PROPERTY, originalValue);
        }
    }

    /**
     * TODO
     * 
     * @param entityClass
     * @return
     */
    public static boolean isEditionClass(Class<BaseEntity> entityClass) {
        // TODO: Change to adding a marker interface on created edition class
        return entityClass.getName().endsWith(EDITION);
    }

    /**
     * TODO: Remove this method when em.find works on either current or edition
     * based on temporal effectivity of EntityManager
     */
    @SuppressWarnings("unchecked")
    public static <T extends TemporalEntity<?>> T find(EntityManager em, Class<T> entityClass, Object id) {
        Long effectiveTS = (Long) em.getProperties().get(EFF_TS_PROPERTY);
        ClassDescriptor descriptor = null;
        if (effectiveTS == null) {
            descriptor = getCurrentDescriptor(em.unwrap(Session.class), (Class<T>) entityClass);
        } else {
            descriptor = getEditionDescriptor(em.unwrap(Session.class), (Class<T>) entityClass);
        }

        Query query = em.createNamedQuery(descriptor.getAlias() + ".find");
        query.setParameter("ID", id);
        return (T) query.getSingleResult();
    }

    /**
     * Helper method used to determine if an object is {@link TemporalEntity}
     * including the case where a collection is provided. In the case of a
     * collection it is assumed that if the first entity is temporal then all
     * elements of the collection are.
     */
    public static boolean isTemporalEntity(Object value) {
        if (value instanceof Class) {
            return isTemporalEntity((Class<?>) value);
        }
        if (value instanceof TemporalEntity) {
            return true;
        }
        if (value instanceof Collection && !((Collection<?>) value).isEmpty()) {
            Collection<?> c = (Collection<?>) value;
            Object first = c.iterator().next();
            return isTemporalEntity(first);
        }
        if (value instanceof Map) {
            throw new IllegalArgumentException("Map not supported");
        }
        return false;
    }

    public static boolean isTemporalEntity(Class<?> value) {
        return value != null && TemporalEntity.class.isAssignableFrom(value);
    }

    /**
     * Helper method used to determine if an object is {@link Temporal}
     * including the case where a collection is provided. In the case of a
     * collection it is assumed that if the first entity is temporal then all
     * elements of the collection are.
     */
    public static boolean isTemporal(Object value) {
        if (value instanceof Class) {
            return isTemporal((Class<?>) value);
        }
        if (value instanceof Temporal) {
            return true;
        }
        if (value instanceof Collection && !((Collection<?>) value).isEmpty()) {
            Collection<?> c = (Collection<?>) value;
            Object first = c.iterator().next();
            return isTemporal(first);
        }
        if (value instanceof Map) {
            throw new IllegalArgumentException("Map not supported");
        }
        return false;
    }

    public static boolean isTemporal(Class<?> value) {
        return value != null && Temporal.class.isAssignableFrom(value);
    }

    /**
     * TODO
     * 
     * @param em
     */
    public static EditionSet getEditionSet(EntityManager em) {
        AbstractSession session = em.unwrap(RepeatableWriteUnitOfWork.class);
        return (EditionSet) session.getProperty(EDITION_SET_PROPERTY);
    }

    /**
     * TODO
     * 
     * @param em
     */
    public static EditionSet initializeEditionSet(EntityManager em) {
        if (!hasEffectiveTime(em)) {
            throw new IllegalStateException("No effective time configured");
        }
        Long effective = getEffectiveTime(em);

        AbstractSession session = em.unwrap(RepeatableWriteUnitOfWork.class);
        EditionSet editionSet = (EditionSet) session.getProperty(EDITION_SET_PROPERTY);
        if (editionSet == null || editionSet.getEffective() != effective) {
            editionSet = em.find(EditionSet.class, effective);
            if (editionSet == null) {
                editionSet = new EditionSet(effective);
                em.persist(editionSet);
            }
            session.setProperty(EDITION_SET_PROPERTY, editionSet);
        }

        return editionSet;
    }
}
