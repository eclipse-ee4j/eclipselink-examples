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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.persistence.EntityManager;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.mappings.DatabaseMapping;

import temporal.persistence.DescriptorHelper;

/**
 * Apply an {@link EditionSet} making all of its contained editions the current.
 * This involves copying all relevant values into the current including the
 * {@link Effectivity} and then delete this edition. The OID of the current
 * should be changed to that of the edition when the operation is complete.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class EditionSetHelper {

    /**
     * Apply the {@link EditionSet} causing all its editions to become the
     * continuity.
     * 
     * @param em
     * @param editionSet
     */
    public static void apply(EntityManager em, EditionSet editionSet) {
        for (EditionSetEntry ese : editionSet.getEntries()) {
            copyValues(em, ese);

            em.remove(ese.getEdition());

        }
    }

    public static void copyValues(EntityManager em, EditionSetEntry entry) {
        TemporalEntity<?> edition = entry.getEdition();
        TemporalEntity<?> continuity = entry.getEdition().getContinuity();

        AbstractSession session = em.unwrap(RepeatableWriteUnitOfWork.class);
        ClassDescriptor descriptor = DescriptorHelper.getCurrentDescriptor(session, edition.getClass());

        for (String attr : entry.getAttributes()) {
            DatabaseMapping mapping = descriptor.getMappingForAttributeName(attr);

            if (!mapping.isForeignReferenceMapping()) {
                Object value = mapping.getRealAttributeValueFromObject(edition, session);
                Object oldValue = mapping.getRealAttributeValueFromObject(continuity, session);
                mapping.setRealAttributeValueInObject(continuity, value);
                if (continuity instanceof ChangeTracker) {
                    PropertyChangeListener listener = ((ChangeTracker) continuity)._persistence_getPropertyChangeListener();
                    listener.propertyChange(new PropertyChangeEvent(continuity, attr, oldValue, value));
                }
            }
        }

        continuity.getEffectivity().setEnd(edition.getEffectivity().getEnd());
    }

    /**
     * TODO
     * 
     * @param em
     * @param editionSet
     * @param effective
     */
    public static void move(EntityManager em, EditionSet editionSet, long effective) {

    }
}
