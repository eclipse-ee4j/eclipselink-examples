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


/**
 * Additional interface implemented on edition wrappers returned from
 * {@link EditionWrapperHelper#wrap(javax.persistence.EntityManager, Object)}
 * which allows users of the wrapper to know if changes exist and to verify if
 * their change has temporal conflicts with future editions.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public interface TemporalEdition<T extends TemporalEntity<?>> {

    /**
     * Check if a new edition has been created and if it has changes.
     */
    boolean hasChanges();

    /**
     * Verify if the new edition created by modifications conflicts with any
     * future editions which already exist.
     */
    boolean hasConflicts();
    
    /**
     * Create a new edition for the current effectivity
     */
    T newEdition();
    
    /**
     * TODO
     */
    EditionSetEntry getEditionSetEntry();
}
