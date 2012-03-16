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

import static temporal.persistence.DescriptorHelper.EDITION;
import static temporal.persistence.DescriptorHelper.EDITION_VIEW;
import static temporal.persistence.DescriptorHelper.getEditionDescriptor;

import java.util.Collection;
import java.util.Map;

import javax.persistence.EntityManager;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Session;

/**
 * This helper is used in to configure and access the temporal values of an
 * {@link EntityManager} and its managed entities.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class TemporalHelper {

    /**
     * {@link TemporalEntity} interface name
     */
    public static final String INTERFACE = TemporalEntity.class.getName();

    /**
     * Mapping property used to indicate that a mapping holds a non-temporal
     * value. The result is that only the continuity row will hold the value.
     */
    public static final String NON_TEMPORAL = "example.NonTemporal";

    /**
     * @return <code>true</code> if this is an instance of the edition subclass.
     */
    public static boolean isEdition(EntityManager em, TemporalEntity<?> entity) {
        ClassDescriptor descriptor = getEditionDescriptor(em.unwrap(Session.class), entity.getClass());
        return descriptor != null && descriptor.getJavaClass() == entity.getClass();
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
     * TODO
     * 
     * @param entityClass
     * @return
     */
    public static boolean isEditionViewClass(Class<BaseEntity> entityClass) {
        // TODO: Change to adding a marker interface on created edition class
        return entityClass.getName().endsWith(EDITION_VIEW);
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
            return isTemporal((Class<?>) value, true);
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

    public static boolean isTemporal(Class<?> value, boolean includeTemporalEntity) {
        if (value == null || (!includeTemporalEntity && isTemporalEntity(value))) {
            return false;
        }
        return Temporal.class.isAssignableFrom(value);
    }

}
