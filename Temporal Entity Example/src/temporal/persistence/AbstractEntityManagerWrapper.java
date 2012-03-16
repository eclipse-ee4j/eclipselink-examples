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

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;

/**
 * Utility class that provides a simplified wrapper for an {@link EntityManager}
 * allowing subclasses to just customize or enhance the behaviour they require.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.2
 */
public abstract class AbstractEntityManagerWrapper implements EntityManager {

    /**
     * Wrapped {@link EntityManager} which all operations will be performed
     * against
     */
    private EntityManager entityManager;

    protected AbstractEntityManagerWrapper(EntityManager em) {
        this.entityManager = em;
    }

    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        return getEntityManager().find(entityClass, primaryKey);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
        return getEntityManager().find(entityClass, primaryKey, properties);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
        return getEntityManager().find(entityClass, primaryKey, lockMode);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
        return getEntityManager().find(entityClass, primaryKey, lockMode, properties);
    }

    @Override
    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        return getEntityManager().getReference(entityClass, primaryKey);
    }

    @Override
    public void flush() {
        getEntityManager().flush();
    }

    @Override
    public FlushModeType getFlushMode() {
        return getEntityManager().getFlushMode();
    }

    @Override
    public void clear() {
        getEntityManager().clear();
    }

    @Override
    public void detach(Object entity) {
        getEntityManager().detach(entity);
    }

    @Override
    public boolean contains(Object entity) {
        return getEntityManager().contains(entity);
    }

    @Override
    public LockModeType getLockMode(Object entity) {
        return getEntityManager().getLockMode(entity);
    }

    @Override
    public Map<String, Object> getProperties() {
        return getEntityManager().getProperties();
    }

    @Override
    public Query createQuery(String qlString) {
        return getEntityManager().createQuery(qlString);
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        return getEntityManager().createQuery(criteriaQuery);
    }

    @Override
    public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
        return getEntityManager().createQuery(qlString, resultClass);
    }

    @Override
    public Query createNamedQuery(String name) {
        return getEntityManager().createNamedQuery(name);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
        return getEntityManager().createNamedQuery(name, resultClass);
    }

    @Override
    public Query createNativeQuery(String sqlString) {
        return getEntityManager().createNativeQuery(sqlString);
    }

    @Override
    public Query createNativeQuery(String sqlString, @SuppressWarnings("rawtypes") Class resultClass) {
        return getEntityManager().createNativeQuery(sqlString, resultClass);
    }

    @Override
    public Query createNativeQuery(String sqlString, String resultSetMapping) {
        return getEntityManager().createNativeQuery(sqlString, resultSetMapping);
    }

    @Override
    public Object getDelegate() {
        return getEntityManager().getDelegate();
    }

    @Override
    public void close() {
        getEntityManager().close();
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return getEntityManager().getEntityManagerFactory();
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return getEntityManager().getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        return getEntityManager().getMetamodel();
    }

    @Override
    public EntityTransaction getTransaction() {
        return getEntityManager().getTransaction();
    }

    @Override
    public boolean isOpen() {
        return getEntityManager().isOpen();
    }

    @Override
    public void joinTransaction() {
        getEntityManager().joinTransaction();
    }

    @Override
    public void lock(Object entity, LockModeType lockModeType) {
        getEntityManager().lock(entity, lockModeType);
    }

    @Override
    public void lock(Object entity, LockModeType lockModeType, Map<String, Object> properties) {
        getEntityManager().lock(entity, lockModeType, properties);
    }

    @Override
    public <T> T merge(T entity) {
        return getEntityManager().merge(entity);
    }

    @Override
    public void persist(Object entity) {
        getEntityManager().persist(entity);
    }

    @Override
    public void refresh(Object entity) {
        getEntityManager().refresh(entity);
    }

    @Override
    public void refresh(Object entity, Map<String, Object> properties) {
        getEntityManager().refresh(entity, properties);
    }

    @Override
    public void refresh(Object entity, LockModeType lockModeType) {
        getEntityManager().refresh(entity, lockModeType);
    }

    @Override
    public void refresh(Object entity, LockModeType lockModeType, Map<String, Object> properties) {
        getEntityManager().refresh(entity, lockModeType, properties);
    }

    @Override
    public void remove(Object entity) {
        getEntityManager().remove(entity);
    }

    @Override
    public void setFlushMode(FlushModeType flushModeType) {
        getEntityManager().setFlushMode(flushModeType);
    }

    @Override
    public void setProperty(String name, Object value) {
        getEntityManager().setProperty(name, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> clazz) {
        if (EntityManager.class == clazz) {
            return (T) getEntityManager();
        }
        return getEntityManager().unwrap(clazz);
    }
}
