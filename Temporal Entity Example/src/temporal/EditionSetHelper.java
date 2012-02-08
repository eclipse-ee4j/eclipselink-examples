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

import javax.persistence.EntityManager;

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
     * TODO
     * 
     * @param em
     * @param editionSet
     */
    public static void apply(EntityManager em, EditionSet editionSet) {

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
