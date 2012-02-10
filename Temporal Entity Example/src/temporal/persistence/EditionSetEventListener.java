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
package temporal.persistence;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.descriptors.DescriptorEventListener;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;

import temporal.EditionSet;
import temporal.EditionSetEntry;
import temporal.TemporalHelper;

/**
 * Listener which adds the modified attributes to
 * {@link EditionSetEntry#getAttributes()} during the
 * {@link DescriptorEventListener#preWrite(DescriptorEvent)} event. This
 * provides the {@link EditionSetEntry} with a set of attribute names that were
 * modified.
 * <p>
 * This listener relies on the fact that all new editions are first flushed to
 * the database before any changes are made. If this does not happen then there
 * is no {@link ObjectChangeSet} for the new edition instance and thus the
 * attributes modified are not known.
 * 
 * @see TemporalHelper#createEdition(javax.persistence.EntityManager, temporal.TemporalEntity)
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class EditionSetEventListener extends DescriptorEventAdapter implements DescriptorCustomizer {

    @Override
    public void customize(ClassDescriptor descriptor) throws Exception {
        descriptor.getEventManager().addListener(this);
    }

    @Override
    public void preWrite(DescriptorEvent event) {
        EditionSet es = (EditionSet) event.getSource();
        RepeatableWriteUnitOfWork uow = (RepeatableWriteUnitOfWork) event.getSession();
        UnitOfWorkChangeSet uowCS = (UnitOfWorkChangeSet) uow.getUnitOfWorkChangeSet();

        if (es.hasEntries() && uowCS.hasChanges()) {
            for (EditionSetEntry entry : es.getEntries()) {
                ObjectChangeSet objCS = uowCS.getCloneToObjectChangeSet().get(entry.getEdition());
                if (objCS != null && objCS.hasChanges()) {
                    for (Object attr : objCS.getAttributesToChanges().keySet()) {
                        entry.getAttributes().add((String) attr);
                    }
                }
            }
        }
    }

}
