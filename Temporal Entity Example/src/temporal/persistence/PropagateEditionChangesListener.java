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

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.ChangeRecord;
import org.eclipse.persistence.internal.sessions.DirectToFieldChangeRecord;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

import temporal.EditionSet;
import temporal.EditionSetEntry;
import temporal.TemporalEntity;
import temporal.TemporalHelper;

/**
 * During the initial phases of a commit this listener will look at all proposed
 * edition changes and propagate them through all future editions applying the
 * change if that future edition has the same old value as this edition did
 * before the change.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class PropagateEditionChangesListener extends SessionEventAdapter {

    @Override
    public void preCalculateUnitOfWorkChangeSet(SessionEvent event) {
        RepeatableWriteUnitOfWork uow = (RepeatableWriteUnitOfWork) event.getSession();
        UnitOfWorkChangeSet uowCS = (UnitOfWorkChangeSet) uow.getUnitOfWorkChangeSet();
        EditionSet es = (EditionSet) uow.getProperty(TemporalHelper.EDITION_SET_PROPERTY);

        if (es != null && es.hasEntries() && uowCS.hasChanges()) {
            for (EditionSetEntry entry : es.getEntries()) {
                ObjectChangeSet objCS = uowCS.getCloneToObjectChangeSet().get(entry.getEdition());
                List<TemporalEntity<?>> futures = findFutureEditions(uow, entry);

                if (objCS != null && objCS.hasChanges()) {
                    for (String attr : objCS.getChangedAttributeNames()) {
                        ChangeRecord cr = (ChangeRecord) objCS.getAttributesToChanges().get(attr);
                        entry.getAttributes().add(attr);

                        propogateChanges(uow, futures, entry, cr);
                    }
                }
            }
        }
    }

    /**
     * Find all of the future EditionView instances ordered by the start time.
     * These will be used to propagate changes. A native expression query is
     * used since we are already under the JPA layer.
     */
    @SuppressWarnings("unchecked")
    private List<TemporalEntity<?>> findFutureEditions(RepeatableWriteUnitOfWork uow, EditionSetEntry entry) {
        ClassDescriptor desc = uow.getClassDescriptor(entry.getEdition());
        desc = (ClassDescriptor) desc.getProperty(TemporalHelper.EDITION_VIEW);

        ReadAllQuery raq = new ReadAllQuery(desc.getJavaClass());
        ExpressionBuilder eb = raq.getExpressionBuilder();
        Expression cidExp = eb.get("cid").equal(entry.getEdition().getContinuity().getId());
        Expression startExp = eb.get("effectivity").get("start");
        Expression futureExp = startExp.greaterThan(entry.getEditionSet().getEffective());
        raq.setSelectionCriteria(cidExp.and(futureExp));
        raq.addOrdering(startExp.ascending());
        raq.getContainerPolicy().setContainerClass(ArrayList.class);

        return (List<TemporalEntity<?>>) uow.executeQuery(raq);
    }

    /**
     * Carry and changes forward through future editions that
     */
    private void propogateChanges(RepeatableWriteUnitOfWork uow, List<TemporalEntity<?>> futures, EditionSetEntry entry, ChangeRecord record) {
        if (!futures.isEmpty() && !(record instanceof DirectToFieldChangeRecord)) {
            throw new UnsupportedOperationException("Only direct mapping changes can be propagated");
        }

        for (TemporalEntity<?> future : futures) {
            Object newValue = record.getMapping().getRealAttributeValueFromObject(entry.getEdition(), uow);
            Object futureValue = record.getMapping().getRealAttributeValueFromObject(future, uow);

            if ((futureValue == null && record.getOldValue() == null) || futureValue.equals(record.getOldValue())) {
                record.getMapping().setRealAttributeValueInObject(future, newValue);
                if (future instanceof ChangeTracker) {
                    ((ChangeTracker) future)._persistence_getPropertyChangeListener().propertyChange(new PropertyChangeEvent(future, record.getAttribute(), record.getOldValue(), newValue));
                }
            } else {
                // Stop propagating when you hit the first non-match
                return;
            }
        }
    }

}
