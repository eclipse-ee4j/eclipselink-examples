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

import static temporal.TemporalHelper.NON_TEMPORAL;
import static temporal.persistence.DescriptorHelper.getEditionDescriptor;

import java.lang.reflect.Member;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.descriptors.InstanceVariableAttributeAccessor;
import org.eclipse.persistence.internal.descriptors.MethodAttributeAccessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.sessions.Session;

import temporal.persistence.AbstractEntityManagerWrapper;
import temporal.persistence.DescriptorHelper;

/**
 * TODO
 * 
 * @author dclarke
 * @since EclipseLink 2.3.2
 */
public class TemporalEntityManager extends AbstractEntityManagerWrapper {

    /**
     * Property named used to specify the current effective time for all queries
     * run in the current persistence context.
     */
    public static final String EFF_TS_PROPERTY = "EFF_TS";

    /**
     * TODO
     */
    public static final String TEMPORAL_EM_PROPERTY = TemporalEntityManager.class.getName();

    /**
     * TODO
     */
    private Long effective;

    /**
     * TODO
     */
    private EditionSet editionSet;

    /**
     * TODO
     * 
     * @param em
     * @return
     */
    public static TemporalEntityManager getInstance(EntityManager em) {
        if (TemporalEntityManager.class.isAssignableFrom(em.getClass())) {
            return (TemporalEntityManager) em;
        }
        TemporalEntityManager tem = (TemporalEntityManager) em.getProperties().get(TEMPORAL_EM_PROPERTY);
        if (tem == null) {
            tem = new TemporalEntityManager(em);
        }
        return tem;
    }

    /**
     * Lookup the {@link TemporalEntityManager} as a property within the
     * provided session.
     * 
     * @throws IllegalStateException
     *             no {@link TemporalEntityManager} is found for the key
     *             {@value #TEMPORAL_EM_PROPERTY}
     */
    public static TemporalEntityManager getInstance(Session session) {
        TemporalEntityManager tem = (TemporalEntityManager) session.getProperty(TEMPORAL_EM_PROPERTY);
        if (tem == null) {
            throw new IllegalStateException("No TemporalEntityManager found in: " + session);
        }
        return tem;
    }

    private TemporalEntityManager(EntityManager em) {
        super(em);
        em.setProperty(TEMPORAL_EM_PROPERTY, this);
    }

    private RepeatableWriteUnitOfWork getUnitOfWork() {
        return unwrap(RepeatableWriteUnitOfWork.class);
    }

    public EditionSet setEffectiveTime(Long startTime, boolean initializeEditionSet) {
        this.effective = startTime;
        setProperty(EFF_TS_PROPERTY, startTime);

        if (getEditionSet() != null && getEditionSet().getEffective() != startTime) {
            this.editionSet = null;
        }
        if (initializeEditionSet) {
            this.editionSet = initializeEditionSet();
        }
        return this.editionSet;
    }

    public EditionSet setEffectiveTime(Long startTime) {
        return setEffectiveTime(startTime, false);
    }

    public void clearEffectiveTime() {
        this.effective = null;
        setProperty(EFF_TS_PROPERTY, null);
    }

    public Long getEffectiveTime() {
        // Lookup the EditionSet and use its effective if one exists.
        EditionSet editionSet = getEditionSet();
        if (editionSet != null) {
            return editionSet.getEffective();
        }

        return this.effective;
    }

    public boolean hasEffectiveTime() {
        return getEffectiveTime() != null;
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
    public <T extends TemporalEntity<?>> T newEdition(T source) {
        if (source == null) {
            return null;
        }
        AbstractSession session = getEntityManager().unwrap(RepeatableWriteUnitOfWork.class);
        Long start = getEffectiveTime();

        if (start == null || start == Effectivity.BOT) {
            throw new IllegalStateException("Cannot create an eddition without an effective time set");
        }

        ClassDescriptor editionDesc = getEditionDescriptor(session, source.getClass());

        if (editionDesc == null) {
            throw new IllegalArgumentException("No edition descriptor for: " + source);
        }

        // Lookup the EditionSet and throw and exception if one was not created.
        EditionSet editionSet = getEditionSet();
        if (editionSet == null) {
            throw new IllegalStateException("No EditionSet associated with this EntityManager");
        }

        TemporalEntity<T> edition = (TemporalEntity<T>) editionDesc.getInstantiationPolicy().buildNewInstance();
        edition.setContinuity((T) source.getContinuity());
        edition.setPreviousEdition(source);

        // Copy the mapped values from source to new edition
        for (DatabaseMapping mapping : editionDesc.getMappings()) {
            copyValue(session, mapping, source, edition);
        }

        edition.getEffectivity().setStart(start);
        edition.getEffectivity().setEnd(source.getEffectivity().getEnd());
        source.getEffectivity().setEnd(start);
        getEntityManager().persist(edition);

        editionSet.add(edition);

        // Flush the transaction so that any changes made to the new edition are
        // tracked and the EditionSet can be properly populated at commit.
        if (getEntityManager().getTransaction().isActive()) {
            getEntityManager().flush();
        }

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
    public <T extends Temporal> T newTemporal(Class<T> temporalClass) {
        AbstractSession session = getEntityManager().unwrap(RepeatableWriteUnitOfWork.class);
        Long start = getEffectiveTime();
        ClassDescriptor descriptor = session.getClassDescriptor(temporalClass);

        if (descriptor == null) {
            throw new IllegalArgumentException("No descriptor for: " + temporalClass);
        }
        // Lookup the EditionSet and throw and exception if one was not created.
        EditionSet editionSet = getEditionSet();
        if (editionSet == null && start != null) {
            throw new IllegalStateException("No EditionSet associated with this EntityManager");
        }

        Temporal newInstance = (Temporal) descriptor.getInstantiationPolicy().buildNewInstance();

        // TODO: Unsure why but seeing a null effectivity
        if (newInstance.getEffectivity() == null) {
            AggregateObjectMapping effMapping = (AggregateObjectMapping) descriptor.getMappingForAttributeName("effectivity");
            Effectivity eff = (Effectivity) effMapping.getReferenceDescriptor().getInstantiationPolicy().buildNewInstance();
            effMapping.setAttributeValueInObject(newInstance, eff);
        }

        if (start != null) {
            newInstance.getEffectivity().setStart(start);
        }
        getEntityManager().persist(newInstance);

        if (editionSet != null) {
            editionSet.add(newInstance);
        }

        // TODO: Enable if change tracking required
        // em.flush();

        return (T) newInstance;
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
    public <T extends TemporalEntity<?>> T newEntity(Class<T> entityClass) {
        AbstractSession session = getEntityManager().unwrap(RepeatableWriteUnitOfWork.class);
        Long start = getEffectiveTime();
        ClassDescriptor descriptor = session.getClassDescriptor(entityClass);

        if (start != null) {
            descriptor = getEditionDescriptor(session, (Class<TemporalEntity<T>>) entityClass);
        }
        if (descriptor == null) {
            throw new IllegalArgumentException("No descriptor for: " + entityClass);
        }
        // Lookup the EditionSet and throw and exception if one was not created.
        EditionSet editionSet = getEditionSet();
        if (editionSet == null && start != null) {
            throw new IllegalStateException("No EditionSet associated with this EntityManager");
        }

        TemporalEntity<T> edition = (TemporalEntity<T>) descriptor.getInstantiationPolicy().buildNewInstance();
        edition.setContinuity((T) edition);
        if (start != null) {
            edition.getEffectivity().setStart(start);
        }
        getEntityManager().persist(edition);

        if (editionSet != null) {
            editionSet.add(edition);
        }

        getEntityManager().flush();

        return (T) edition;
    }

    /**
     * Copy mapped value from source to new edition. This copies the real
     * attribute value.
     */
    protected void copyValue(AbstractSession session, DatabaseMapping mapping, TemporalEntity<?> source, TemporalEntity<?> target) {
        if (mapping.getAttributeName().equals("effectivity")) {
            return;
        }

        String nonTemporal = (String) mapping.getProperty(NON_TEMPORAL);
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

        if (mapping.isCollectionMapping()) {
            value = ((CollectionMapping) mapping).getContainerPolicy().cloneFor(value);
        }

        mapping.setRealAttributeValueInObject(target, value);
    }

    /**
     * Get an edition for a {@link TemporalEntity} handling it being a current
     * or edition.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends TemporalEntity<?>> T getEdition(T entity) {
        ClassDescriptor descriptor = getEditionDescriptor(getEntityManager().unwrap(Session.class), entity.getClass());

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
        Long original = getEffectiveTime();
        if (original == null) {
            setEffectiveTime(Effectivity.BOT);
        }
        try {
            return (T) find(descriptor.getJavaClass(), entity.getContinuity().getId());
        } finally {
            setEffectiveTime(original);
        }
    }

    public EditionSet getEditionSet() {
        return this.editionSet;
    }

    /**
     * Initialize a new EditionSet
     */
    public EditionSet initializeEditionSet() {
        if (!hasEffectiveTime()) {
            throw new IllegalStateException("No effective time configured");
        }
        Long effective = getEffectiveTime();

        if (getEditionSet() == null || getEditionSet().getEffective() != effective) {
            EditionSet es = getEntityManager().find(EditionSet.class, effective);
            if (es == null) {
                es = new EditionSet(effective);
                getEntityManager().persist(es);
            }
            this.editionSet = es;
        }
        return editionSet;
    }

    /**
     * TODO: Remove this method when em.find works on either current or edition
     * based on temporal effectivity of EntityManager
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        if (hasEffectiveTime() && TemporalHelper.isTemporalEntity(entityClass)) {
            RepeatableWriteUnitOfWork uow = getUnitOfWork();
            ClassDescriptor descriptor = DescriptorHelper.getEditionDescriptor(uow, entityClass);

            Query query = createNamedQuery(descriptor.getAlias() + ".find");
            query.setParameter("ID", primaryKey);

            return (T) query.getSingleResult();
        }

        return (T) super.find(entityClass, primaryKey);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> clazz) {
        if (TemporalEntityManager.class == clazz) {
            return (T) this;
        }
        return getEntityManager().unwrap(clazz);
    }

    @Override
    public Query createQuery(String qlString) {
        Query query = super.createQuery(qlString);
        updateTemporalQuery(query);
        return query;
    }

    @Override
    public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
        TypedQuery<T> query = super.createQuery(qlString, resultClass);
        updateTemporalQuery(query);
        return query;
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        TypedQuery<T> query = super.createQuery(criteriaQuery);
        updateTemporalQuery(query);
        return query;
    }

    /**
     * Update the provided JPA query based on the effective time of use the
     * correct target entity or edition
     */
    private void updateTemporalQuery(Query query) {
        DatabaseQuery elQuery = JpaHelper.getDatabaseQuery(query);
        
        if (hasEffectiveTime() && TemporalHelper.isTemporalEntity(elQuery.getReferenceClass())) {
            RepeatableWriteUnitOfWork uow = getUnitOfWork();
            ClassDescriptor descriptor = DescriptorHelper.getEditionDescriptor(uow, elQuery.getReferenceClass());
            ((ObjectLevelReadQuery) elQuery).setReferenceClass(descriptor.getJavaClass());
            
            // TODO: Should this be set every time?
            if (elQuery.getDescriptor() != null) {
                elQuery.setDescriptor(descriptor);
            }
        }
    }


    public String toString() {
        return "TemporalEntityManager@" + getEffectiveTime() + "[" + getEntityManager() + "]";
    }
}
