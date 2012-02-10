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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;


/**
 * {@link Proxy} wrapper for {@link TemporalEntity} to handle the update
 * scenarios where changes made to an edition should result in new editions
 * being created.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class EditionWrapperHandler<T extends TemporalEntity<?>> implements InvocationHandler, TemporalEdition<T> {

    /**
     * The original edition being used by the client. This edition will not be
     * changed with the exception of the end effectivity time if a new edition
     * is created.
     */
    private T original;

    /**
     * The new edition created if a set method is invoked
     */
    private T newEdition;

    private EntityManager entityManager;

    /**
     * 
     */
    private Map<String, Object[]> changes;

    public EditionWrapperHandler(T original, EntityManager em) {
        this.original = original;
        this.entityManager = em;
    }

    public T getOriginal() {
        return original;
    }

    public T getNewEdition() {
        return newEdition;
    }

    public T getEntity() {
        if (getNewEdition() != null) {
            return getNewEdition();
        }
        return getOriginal();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public Map<String, Object[]> getChanges() {
        return changes;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == TemporalEdition.class) {
            return method.invoke(this, args);
        }

        if (args != null && args.length > 0) {
            if (this.newEdition == null) {
                this.newEdition = TemporalHelper.createEdition(getEntityManager(), getOriginal());
                this.changes = new HashMap<String, Object[]>();
            }
            getChanges().put(method.getName(), args);
        }

        Object result = method.invoke(getEntity(), args);

        if (result != null && TemporalHelper.isTemporalEntity(result)) {
            result = EditionWrapperHelper.wrap(getEntityManager(), result);
        }

        return result;
    }

    @Override
    public boolean hasChanges() {
        if (getNewEdition() == null) {
            return false;
        }

        return !getChanges().isEmpty();
    }

    /**
     * Verify if the new edition created by modifications conflicts with any
     * future editions which already exist.
     */
    @Override
    public boolean hasConflicts() {
        // TODO: Check for future versions that have conflicting changes
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    /**
     * TODO
     */
    @SuppressWarnings("unchecked")
    @Override
    public T newEdition() {
        return (T) TemporalHelper.newInstance(getEntityManager(), this.original.getClass());
    }

    @Override
    public EditionSetEntry getEditionSetEntry() {
        // TODO Auto-generated method stub
        return null;
    }

}
