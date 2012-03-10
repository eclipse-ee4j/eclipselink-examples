/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors: dclarke - Bug 361016: Future Versions Examples
 ******************************************************************************/
package temporal;

import temporal.persistence.ConfigureTemporalDescriptors;

/**
 * Marker interface used to indicate classes and their subclasses that are
 * temporal. All descriptors for classes with this interface will be customized
 * by {@link ConfigureTemporalDescriptors}.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public interface TemporalEntity<T extends TemporalEntity<?>> extends Temporal {

    int getId();

    T getContinuity();

    void setContinuity(T continuity);

    T getPreviousEdition();

    void setPreviousEdition(T edition);

    long getVersion();

    boolean isContinuity();

    /**
     * When an edition is promoted to become the continuity this method is
     * invoked. This method is invoked after the {@link EditionSetHelper} has
     * copied all of the mapped values over and allows for entity specific logic
     * to be applied.
     */
    @SuppressWarnings("rawtypes")
    void applyEdition(TemporalEntity edition);


}
